package com.xjbg.sso.core.enums;

import lombok.Getter;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
public enum StatusEnum {
    NORMAL("normal"), BLOCKED("blocked");
    private String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public static StatusEnum getStatus(String code) {
        for (StatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
