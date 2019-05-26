package com.xjbg.sso.server.advice;

import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.core.util.RequestIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author kesc
 * @since 2019/5/16
 */
@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {

    private static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e) {
        if (RequestIdUtil.get() == null) {
            RequestIdUtil.generate();
        }
        logger.error("system error,request id:" + RequestIdUtil.get(), e);
        BaseResponse response = new BaseResponse();
        // 未知异常
        response.setCode(BusinessExceptionEnum.SYSTEM_ERROR.getCode());
        response.setMsg(BusinessExceptionEnum.SYSTEM_ERROR.getMsg());
        response.setRequestId(RequestIdUtil.get());
        return response;
    }
}
