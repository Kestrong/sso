package com.xjbg.sso.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kesc
 * @since 2019/6/21
 */
@Getter
@Setter
@ConfigurationProperties(prefix = SafeUrlProperties.PREFIX)
public class SafeUrlProperties {
    public static final String PREFIX = "safe";
    private String blackList;
    private String whiteList;
}
