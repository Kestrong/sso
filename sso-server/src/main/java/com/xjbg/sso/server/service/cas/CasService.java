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
    private Protocol protocol = Protocol.CAS1;
    @Autowired
    private CasProperties casProperties;
    /**
     * if service url not present,will redirect to this url after login success
     */
    private String loginSuccessUrl = "login-success";
    /**
     * custom login url to redirect
     */
    private String customLoginUrl;
    private String logoutRequestParam = "logoutRequest";

    public CasService() {
    }

    public String loginOrRedirect(HttpServletRequest request, HttpServletResponse response, boolean renew) {
        TicketGrantingTicket tgt = getTgt(request, response, true, TicketGrantingTicketImpl.class);
        if (!renew && tgt != null) {
            ServiceTicket serviceTicket = grantServiceTicket(tgt, CommonUtil.safeGetParameter(request, protocol.getServiceParameterName()));
            CommonUtil.sendRedirect(response, CommonUtil.constructTicketUrl(serviceTicket.getService(), protocol.getArtifactParameterName(), serviceTicket.getId()));
        }
        if (customLoginUrl != null) {
            CommonUtil.sendRedirect(response, CommonUtil.constructRedirectUrl(customLoginUrl, protocol.getServiceParameterName(), CommonUtil.safeGetParameter(request, protocol.getServiceParameterName()), renew));
        }
        return LOGIN_VIEW;
    }

    public void login(UsernamePasswordCredentials credentials, HttpServletRequest request, HttpServletResponse response) {
        AuthenticationInfo authenticationInfo = authenticationManager.doAuthenticate(credentials);
        TicketGrantingTicket ticketGrantingTicket = grantingTicketFactory.create(authenticationInfo.getPrincipals(), expireInMills);
        String service = CommonUtil.safeGetParameter(request, protocol.getServiceParameterName());
        String urlToRedirect;
        if (StringUtil.isNotBlank(service)) {
            ServiceTicket serviceTicket = ticketGrantingTicket.grantServiceTicket(idGenerator.nextId(), service, serviceTicketExpireInMills);
            cacheTemplate.valueSet(serviceTicket.getId(), serviceTicket, serviceTicketExpireInMills, TimeUnit.MILLISECONDS);
            urlToRedirect = CommonUtil.constructTicketUrl(service, protocol.getArtifactParameterName(), serviceTicket.getId());
        } else {
            urlToRedirect = loginSuccessUrl;
        }
        cookieManager.addGrantTicketCookie(request, response, ticketGrantingTicket.getId());
        if (credentials.isRememberMe()) {
            cookieManager.addRememberMeCookie(request, response, aesOperator == null ? credentials.getUsername() : aesOperator.encrypt(credentials.getUsername()));
        }
        cacheTemplate.valueSet(ticketGrantingTicket.getId(), ticketGrantingTicket, expireInMills, TimeUnit.MILLISECONDS);
        CommonUtil.sendRedirect(response, urlToRedirect);
    }

    public TicketValidationDTO validate(String service, String ticket) {
        ServiceTicketImpl serviceTicket = cacheTemplate.valueGet(ticket, ServiceTicketImpl.class);
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

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        TicketGrantingTicket tgt = getTgt(request, response, false, TicketGrantingTicketImpl.class);
        if (tgt == null) {
            return;
        }
        cacheTemplate.remove(tgt.getId());
        cookieManager.deleteCookie(request, response, cookieManager.getTicketGrantCookieName());
        cookieManager.deleteCookie(request, response, cookieManager.getRememberMeCookieName());
        Map<String, String> services = tgt.getServices();
        for (Map.Entry<String, String> entry : services.entrySet()) {
            CommonUtil.logoutRequest(entry.getValue(), logoutRequestParam, entry.getKey());
        }
    }

    protected <T extends TicketGrantingTicket> TicketGrantingTicket getTgt(HttpServletRequest request, HttpServletResponse response, boolean create, Class<T> tClass) {
        Cookie cookie = cookieManager.getGrantTicketCookie(request);
        if (cookie == null) {
            if (create) {
                Cookie rememberMeCookie = cookieManager.getRememberMeCookie(request);
                if (rememberMeCookie != null) {
                    Principal principal = new SimplePrincipal(aesOperator != null ? aesOperator.decrypt(rememberMeCookie.getValue()) : rememberMeCookie.getValue());
                    TicketGrantingTicket grantingTicket = grantingTicketFactory.create(principal, expireInMills);
                    cacheTemplate.valueSet(grantingTicket.getId(), grantingTicket, expireInMills, TimeUnit.MILLISECONDS);
                    cookieManager.addGrantTicketCookie(request, response, grantingTicket.getId());
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
            if (StringUtil.isNotBlank(casProperties.getCustomLoginUrl())) {
                customLoginUrl = casProperties.getCustomLoginUrl();
            }
            if (StringUtil.isNotBlank(casProperties.getLoginSuccessUrl())) {
                loginSuccessUrl = casProperties.getLoginSuccessUrl();
            }
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
