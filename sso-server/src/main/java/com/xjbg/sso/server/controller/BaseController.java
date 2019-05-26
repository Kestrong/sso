package com.xjbg.sso.server.controller;

import com.xjbg.sso.core.response.BaseResponse;

/**
 * @author kesc
 * @since 2019/5/16
 */
public class BaseController {
    protected final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    protected BaseResponse setResponse() {
        return new BaseResponse<>();
    }

    protected <T> BaseResponse<T> setResponse(T data) {
        return new BaseResponse<>(data);
    }

}
