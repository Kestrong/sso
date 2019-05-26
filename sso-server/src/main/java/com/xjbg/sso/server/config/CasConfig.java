package com.xjbg.sso.server.config;

import com.xjbg.sso.core.cache.CacheTemplate;
import com.xjbg.sso.core.util.AESOperator;
import com.xjbg.sso.core.util.Authenticator;
import com.xjbg.sso.core.util.StringUtil;
import com.xjbg.sso.server.authencation.*;
import com.xjbg.sso.server.cookie.CasCookieManager;
import com.xjbg.sso.server.cookie.ICasCookieManager;
import com.xjbg.sso.server.properties.AesProperties;
import com.xjbg.sso.server.properties.CasProperties;
import com.xjbg.sso.server.service.user.IUserService;
import com.xjbg.sso.server.service.user.UserQueryStrategies;
import com.xjbg.sso.server.ticket.DefaultCasTicketIdGenerator;
import com.xjbg.sso.server.ticket.DefaultTicketGrantingTicketFactory;
import com.xjbg.sso.server.ticket.IdGenerator;
import com.xjbg.sso.server.ticket.TicketGrantingTicketFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Configuration
@EnableConfigurationProperties(value = {AesProperties.class, CasProperties.class})
public class CasConfig {

    @Bean
    public AESOperator aesOperator(AesProperties aesProperties) {
        AESOperator aesOperator = new AESOperator();
        aesOperator.setIvParameter(aesProperties.getIvParameter());
        aesOperator.setsKey(aesProperties.getSKey());
        return aesOperator;
    }

    @Bean
    public Authenticator authenticator() {
        return new Authenticator();
    }

    @Bean
    public UserQueryStrategies userQueryStrategies(IUserService userService) {
        return new UserQueryStrategies(userService);
    }

    @Bean
    public ICasCookieManager cookieManager(CasProperties casProperties) {
        CasCookieManager casCookieManager = new CasCookieManager();
        if (StringUtil.isNotBlank(casProperties.getTicketGrantCookieName())) {
            casCookieManager.setTicketGrantCookieName(casProperties.getTicketGrantCookieName());
        }
        if (StringUtil.isNotBlank(casProperties.getRememberMeCookieName())) {
            casCookieManager.setRememberMeCookieName(casProperties.getRememberMeCookieName());
        }
        if (casProperties.getRememberCookieExpireInSeconds() != null) {
            casCookieManager.setRememberExpireInSeconds(casProperties.getRememberCookieExpireInSeconds());
        }
        if (casProperties.getTicketGrantCookieExpireInSeconds() != null) {
            casCookieManager.setExpireInSeconds(casProperties.getTicketGrantCookieExpireInSeconds());
        }
        return casCookieManager;
    }

    @Bean
    public IdGenerator idGenerator() {
        return new DefaultCasTicketIdGenerator();
    }

    @Bean
    public TicketGrantingTicketFactory ticketGrantingTicketFactory(IdGenerator idGenerator) {
        return new DefaultTicketGrantingTicketFactory(idGenerator);
    }

    @Bean
    public AuthenticationManager authenticationManager(IUserService userService, Authenticator authenticator, CacheTemplate cacheTemplate) {
        SimpleAuthenticationHandler handler = new SimpleAuthenticationHandler(userService);
        handler.setCredentialClass(UsernamePasswordCredentials.class);
        handler.setCredentialMatcher(new SimpleHashedCredentialMatcher(authenticator, cacheTemplate));
        return new DefaultAuthenticationManager(Collections.singletonList(handler));
    }
}
