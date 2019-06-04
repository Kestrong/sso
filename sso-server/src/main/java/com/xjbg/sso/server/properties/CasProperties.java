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
    private String logoutRequestParam;
    private Long ticketExpireInMills;
    private Long serviceTicketExpireInMills;
    private String ticketGrantCookieName;
    private String rememberMeCookieName;
    private Integer rememberCookieExpireInSeconds;
    private Integer ticketGrantCookieExpireInSeconds;
    /**
     * cross domains
     */
    private String allowOrigins;
    /**
     * safe urls allow to visit
     */
    private String whiteList;
}
