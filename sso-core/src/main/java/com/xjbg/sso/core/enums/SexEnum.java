package com.xjbg.sso.core.enums;

import lombok.Getter;

/**
 * @author kesc
 * @since 2019/5/23
 */
@Getter
public enum SexEnum {
    MALE("male"), FEMALE("female"), UNKNOWN("unknown");
    private String sex;

    SexEnum(String sex) {
        this.sex = sex;
    }
}
