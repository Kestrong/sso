package com.xjbg.sso.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author kesc
 * @since 2019/6/15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccessTokenDTO extends BaseDTO {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private Integer expiredIn;
    private String scope;
    private String openId;

    public AccessTokenDTO() {
        this.tokenType = "bearer";
    }
}
