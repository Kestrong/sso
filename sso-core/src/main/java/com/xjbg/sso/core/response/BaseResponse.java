package com.xjbg.sso.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author kesc
 * @since 2019/5/16
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> implements Serializable {
    private static final String SUCCESS_CODE = "00";
    private String requestId;
    private String code = SUCCESS_CODE;
    private String msg;
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(T data) {
        this.data = data;
    }

    public BaseResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
