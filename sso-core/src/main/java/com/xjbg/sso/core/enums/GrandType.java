package com.xjbg.sso.core.enums;

import lombok.Getter;

/**
 * @author kesc
 * @since 2019/6/15
 */
@Getter
public enum GrandType {
    AUTHORIZATION_CODE("authorization_code"),
    IMPLICIT("implicit"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials");
    private String grandType;

    GrandType(String grandType) {
        this.grandType = grandType;
    }
}
