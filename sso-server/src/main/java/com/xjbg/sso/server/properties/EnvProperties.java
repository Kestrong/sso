package com.xjbg.sso.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author kesc
 * @since : 17/8/23
 */
@Setter
@Getter
@ConfigurationProperties(prefix = EnvProperties.ENV_PREFIX)
public class EnvProperties {
    public static final String ENV_PREFIX = "env";
    private String env;
    private Map<String, String> resourceMapping;
}
