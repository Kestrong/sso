package com.xjbg.sso.server.service.cas;

import com.xjbg.sso.core.Protocol;
import com.xjbg.sso.core.cache.CacheTemplate;
import com.xjbg.sso.core.dto.TicketValidationDTO;
import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import com.xjbg.sso.core.util.AESOperator;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import com.xjbg.sso.server.authencation.*;
import com.xjbg.sso.server.cookie.CasCookieManager;
import com.xjbg.sso.server.cookie.ICasCookieManager;
import com.xjbg.sso.server.properties.CasProperties;
import com.xjbg.sso.server.ticket.*;
import com.xjbg.sso.server.util.WebContextUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Service
@Getter
@Setter
public class CasService implements InitializingBean {
    private final static long DEFAULT_EXPIRE = 1000 * 60 * 60 * 2;
    private final static long DEFAULT_SERVICE_TICKET_EXPIRE = 1000 * 60 * 5;
    private final static String LOGIN_VIEW = "login";
    private long expireInMills = DEFAULT_EXPIRE;
    private long serviceTicketExpireInMills = DEFAULT_SERVICE_TICKET_EXPIRE;
    @Autowired
    private CacheTemplate cacheTemplate;
    @Resource(type = ICasCookieManager.class)
    private CasCookieManager cookieManager;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private TicketGrantingTicketFactory grantingTicketFactory;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AESOperator aesOperator;
    @Autowired
    private CasProperties casProperties;
    private String logoutRequestParam = "logoutRequest";
    private Protocol protocol = Protocol.CAS1;

    public CasService() {
    }

    public AuthenticationInfo authentication(UsernamePasswordCredentials credentials) {
        return authenticationManager.doAuthenticate(credentials);
    }

    public void loginOrRedirect() {
        TicketGrantingTicket tgt = getTgt(true, TicketGrantingTicketImpl.class);
        if (tgt == null) {
            throw BusinessExceptionEnum.UNAUTHORIZED.getException();
        }
        ServiceTicket serviceTicket = grantServiceTicket(tgt, CommonUtil.safeGetParameter(WebContextUtil.getRequest(), protocol.getServiceParameterName()));
        CommonUtil.sendRedirect(WebContextUtil.getResponse(), CommonUtil.constructTicketUrl(serviceTicket.getService(), protocol.getArtifactParameterName(), serviceTicket.getId()));
    }

    public String login(UsernamePasswordCredentials credentials) {
        HttpServletRequest request = WebContextUtil.getRequest();
        HttpServletResponse response = WebContextUtil.getResponse();
        AuthenticationInfo authenticationInfo = this.authentication(credentials);
        TicketGrantingTicket ticketGrantingTicket = grantingTicketFactory.create(authenticationInfo.getPrincipals(), expireInMills);
        cookieManager.addGrantTicketCookie(request, response, ticketGrantingTicket.getId());
        if (credentials.isRememberMe()) {
            cookieManager.addRememberMeCookie(request, response, aesOperator == null ? credentials.getUsername() : aesOperator.encrypt(credentials.getUsername()));
        }
        cacheTemplate.valueSet(ticketGrantingTicket.getId(), ticketGrantingTicket, expireInMills, TimeUnit.MILLISECONDS);
        return ticketGrantingTicket.getId();
    }

    public String serviceTicket(String tgtId, String service) {
        TicketGrantingTicket tgt = cacheTemplate.valueGet(tgtId, TicketGrantingTicketImpl.class);
        if (tgt != null) {
            return grantServiceTicket(tgt, service).getId();
        }
        return null;
    }

    public String proxyTicket(String tgtId, String service) {
        TicketGrantingTicket tgt = cacheTemplate.valueGet(tgtId, TicketGrantingTicketImpl.class);
        if (tgt != null) {
            return grantProxyTicket(tgt, service).getId();
        }
        return null;
    }

    public TicketValidationDTO validate(String service, String ticket) {
        ServiceTicket serviceTicket;
        if (ticket.startsWith(ServiceTicket.PREFIX)) {
            serviceTicket = cacheTemplate.valueGet(ticket, ServiceTicketImpl.class);
        } else {
            serviceTicket = cacheTemplate.valueGet(ticket, ProxyTicketImpl.class);
        }
        boolean b = serviceTicket != null && serviceTicket.isValidFor(service);
        cacheTemplate.remove(ticket);
        if (b) {
            TicketGrantingTicketImpl tgt = cacheTemplate.valueGet(serviceTicket.getTicketGrantTicketId(), TicketGrantingTicketImpl.class);
            if (tgt != null) {
                return new TicketValidationDTO(true, tgt.getPrincipal());
            }
        }
        return new TicketValidationDTO(false);
    }

    public void logout(String tgtId) {
        TicketGrantingTicket tgt = getTgt(false, TicketGrantingTicketImpl.class);
        if (tgt == null || !tgt.getId().equals(tgtId)) {
            return;
        }
        HttpServletRequest request = WebContextUtil.getRequest();
        HttpServletResponse response = WebContextUtil.getResponse();
        cacheTemplate.remove(tgt.getId());
        cookieManager.deleteCookie(request, response, cookieManager.getTicketGrantCookieName());
        cookieManager.deleteCookie(request, response, cookieManager.getRememberMeCookieName());
        Map<String, String> services = tgt.getServices();
        for (Map.Entry<String, String> entry : services.entrySet()) {
            CommonUtil.logoutRequest(entry.getValue(), logoutRequestParam, entry.getKey());
        }
    }

    public <T extends TicketGrantingTicket> TicketGrantingTicket getTgt(boolean create, Class<T> tClass) {
        HttpServletRequest request = WebContextUtil.getRequest();
        Cookie cookie = cookieManager.getGrantTicketCookie(request);
        if (cookie == null) {
            if (create) {
                Cookie rememberMeCookie = cookieManager.getRememberMeCookie(request);
                if (rememberMeCookie != null) {
                    Principal principal = new SimplePrincipal(aesOperator != null ? aesOperator.decrypt(rememberMeCookie.getValue()) : rememberMeCookie.getValue());
                    TicketGrantingTicket grantingTicket = grantingTicketFactory.create(principal, expireInMills);
                    cacheTemplate.valueSet(grantingTicket.getId(), grantingTicket, expireInMills, TimeUnit.MILLISECONDS);
                    cookieManager.addGrantTicketCookie(request, WebContextUtil.getResponse(), grantingTicket.getId());
                    return grantingTicket;
                }
            }
            return null;
        }
        return cacheTemplate.valueGet(cookie.getValue(), tClass);
    }

    protected ServiceTicket grantServiceTicket(TicketGrantingTicket ticketGrantingTicket, String service) {
        if (ticketGrantingTicket == null) {
            throw BusinessExceptionEnum.TGT_MUST_PRESENT.getException();
        }
        ServiceTicket serviceTicket = ticketGrantingTicket.grantServiceTicket(idGenerator.nextId(), service, serviceTicketExpireInMills);
        cacheTemplate.valueSet(ticketGrantingTicket.getId(), ticketGrantingTicket);
        cacheTemplate.valueSet(serviceTicket.getId(), serviceTicket, serviceTicketExpireInMills, TimeUnit.MILLISECONDS);
        return serviceTicket;
    }

    protected ServiceTicket grantProxyTicket(TicketGrantingTicket ticketGrantingTicket, String service) {
        if (ticketGrantingTicket == null) {
            throw BusinessExceptionEnum.TGT_MUST_PRESENT.getException();
        }
        ProxyTicket proxyTicket = ticketGrantingTicket.grantProxyTicket(idGenerator.nextId(), service, serviceTicketExpireInMills);
        cacheTemplate.valueSet(proxyTicket.getId(), proxyTicket, serviceTicketExpireInMills, TimeUnit.MILLISECONDS);
        return proxyTicket;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cacheTemplate, "cacheTemplate is null");
        Assert.notNull(authenticationManager, "authenticationManager is null");
        if (cookieManager == null) {
            cookieManager = new CasCookieManager();
        }
        if (idGenerator == null) {
            idGenerator = new DefaultCasTicketIdGenerator();
        }
        if (grantingTicketFactory == null) {
            grantingTicketFactory = new DefaultTicketGrantingTicketFactory(idGenerator);
        }
        if (casProperties != null) {
            if (StringUtil.isNotBlank(casProperties.getLogoutRequestParam())) {
                logoutRequestParam = casProperties.getLogoutRequestParam();
            }
            if (casProperties.getTicketExpireInMills() != null && casProperties.getTicketExpireInMills() > 0) {
                expireInMills = casProperties.getTicketExpireInMills();
            }
            if (casProperties.getServiceTicketExpireInMills() != null && casProperties.getServiceTicketExpireInMills() > 0) {
                serviceTicketExpireInMills = casProperties.getServiceTicketExpireInMills();
            }
        }
    }
}
