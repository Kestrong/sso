package com.xjbg.sso.core.enums;


import com.xjbg.sso.core.exception.BusinessException;
import lombok.Getter;

/**
 * @author kesc
 * @since 2017/12/24
 */
@Getter
public enum BusinessExceptionEnum {
    LOGIN_FAILURE("B10001", "Incorrect username or password"),
    ABNORMAL_ACCOUNT("B10002", "Account was abnormal or disabled"),
    EXCESSIVE_LOGIN("B10003", "Excessive attempts,retry after 1 hour"),
    TGT_MUST_PRESENT("B10004", "Ticket grant ticket must present"),
    NO_HANDLER_SUPPORT_THIS_CREDENTIAL("B10005", "no handler can authenticate provided credentials"),
    USER_EXIST("B10005", "user already exist,please check your username,phone or mail"),
    INVALID_STATUS("B10006", "invalid status"),
    INVALID_APP_ID("B10007", "invalid application"),
    INVALID_RESPONSE_TYPE("B10008", "invalid response type"),
    INVALID_GRAND_TYPE("B1009", "invalid grand type"),
    INVALID_CODE("B10010", "invalid code"),
    INVALID_REDIRECT_URI("B10011", "redirect uri is inconsistent with background configuration"),


    UNAUTHORIZED("401", "UNAUTHORIZED"),
    FORBIDDEN("403", "FORBIDDEN"),
    SYSTEM_ERROR("500", "INTERNAL SERVER ERROR.");

    private String code;
    private String msg;

    BusinessExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BusinessException getException() {
        return new BusinessException(this.getCode(), this.getMsg());
    }

    public BusinessException getException(Throwable e) {
        return new BusinessException(this.getCode(), this.getMsg(), e);
    }
}
