package com.xjbg.sso.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
@Setter
@ConfigurationProperties(prefix = CasProperties.CAS_PREFIX)
public class CasProperties {
    public static final String CAS_PREFIX = "cas";
    /**
     * if service url not present,will redirect to this url after login success
     */
    private String loginSuccessUrl;
    /**
     * custom login url to redirect
     */
    private String customLoginUrl;
    private String logoutRequestParam;
    private Long ticketExpireInMills;
    private Long serviceTicketExpireInMills;
    private String ticketGrantCookieName;
    private String rememberMeCookieName;
    private Integer rememberCookieExpireInSeconds;
    private Integer ticketGrantCookieExpireInSeconds;
}
