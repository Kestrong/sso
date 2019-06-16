package com.xjbg.sso.core.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author kesc
 * @since 2019/6/15
 */
@Getter
@Setter
public class AuthRequest {
    @NotBlank
    private String appId;
    @NotBlank
    private String appKey;
    @NotBlank
    private String grandType;
    private String scope;
    private String code;
}
