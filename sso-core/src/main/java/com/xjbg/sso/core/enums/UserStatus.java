package com.xjbg.sso.core.enums;

import lombok.Getter;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
public enum UserStatus {
    NORMAL("normal"), BLOCKED("blocked");
    private String status;

    UserStatus(String status) {
        this.status = status;
    }
}
