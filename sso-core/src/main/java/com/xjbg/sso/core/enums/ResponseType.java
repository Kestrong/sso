package com.xjbg.sso.core.enums;

import lombok.Getter;

/**
 * @author kesc
 * @since 2019/6/15
 */
@Getter
public enum ResponseType {
    TOKEN("token"), CODE("code"),;
    private String responseType;

    ResponseType(String responseType) {
        this.responseType = responseType;
    }
}
