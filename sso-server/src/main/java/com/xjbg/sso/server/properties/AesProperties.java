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
@ConfigurationProperties(prefix = AesProperties.PREFIX)
public class AesProperties {
    public static final String PREFIX = "aes";
    private String sKey;
    private String ivParameter;
}
