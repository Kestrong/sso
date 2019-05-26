package com.xjbg.sso.server.advice;

import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import com.xjbg.sso.core.exception.BusinessException;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.core.util.RequestIdUtil;
import com.xjbg.sso.server.util.LogJsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kesc
 * @since 2019/5/16
 */
@Aspect
@Order(1)
@Component
public class ApiAdvisor {

    private static Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Around("execution(public * com.xjbg.sso.server..*..*Controller.*(..))")
    public Object invokeAPI(ProceedingJoinPoint pjp) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RequestIdUtil.generate();
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().getSimpleName();
        String apiName = className + "#" + methodName;
        String requestId = RequestIdUtil.get();
        LOGGER.info("@@{} started, requestId:{}", apiName, requestId);
        Class<?> returnClazz = ((MethodSignature) pjp.getSignature()).getReturnType();
        Object returnValue = null;
        Object[] args = pjp.getArgs();
        try {
            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    log(apiName, arg);
                }
            }
            returnValue = pjp.proceed();
            this.handleSuccess(returnValue);
        } catch (BusinessException e) {
            returnValue = this.handleBusinessError(e, apiName, returnClazz);
        } catch (Throwable e) {
            returnValue = this.handleSystemError(e, apiName, returnClazz);
        }
        stopWatch.stop();
        LOGGER.info("@@{} done,response:{},time spends {} ms", apiName, LogJsonUtil.toJson(returnValue), stopWatch.getTotalTimeSeconds());
        return returnValue;
    }

    private void log(String apiName, Object arg) {
        try {
            if ((arg instanceof HttpServletRequest) || (arg instanceof HttpServletResponse) || arg instanceof Model) {
                //ignore
            } else if (arg instanceof MultipartFile) {
                String fileName = ((MultipartFile) arg).getName();
                LOGGER.info("@@{} started,request: file[{}]", apiName, fileName);
            } else {
                LOGGER.info("@@{} started,request:{}", apiName, LogJsonUtil.toJson(arg));
            }
        } catch (Exception e) {
            LOGGER.info("@@{} started,param parse error", apiName, e);
        }
    }

    private Object handleBusinessError(BusinessException e, String apiName, Class<?> returnClazz) {
        String errorCode = e.getCode();
        String errorMessage = e.getMessage();
        LOGGER.error("@Meet error when do {} [{}]:{}", apiName, errorCode, errorMessage);
        return handle(errorCode, errorMessage, returnClazz);
    }

    private Object handleSystemError(Throwable e, String apiName, Class<?> returnClazz) {
        String errorMessage = e.getMessage();
        LOGGER.error("@Meet unknown error when do {} :{}", apiName, errorMessage);
        LOGGER.error(e.getMessage(), e);
        return handle(BusinessExceptionEnum.SYSTEM_ERROR.getCode(), BusinessExceptionEnum.SYSTEM_ERROR.getMsg(), returnClazz);
    }

    private Object handle(String errorCode, String errorMessage, Class<?> returnClazz) {
        Object returnValue;
        try {
            returnValue = returnClazz.newInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        if (returnValue instanceof BaseResponse) {
            BaseResponse response = (BaseResponse) returnValue;
            response.setRequestId(RequestIdUtil.get());
            response.setCode(errorCode);
            response.setMsg(errorMessage);
        }
        return returnValue;
    }

    private void handleSuccess(Object returnValue) {
        if (returnValue instanceof BaseResponse) {
            BaseResponse response = (BaseResponse) returnValue;
            response.setRequestId(RequestIdUtil.get());
        }
    }

}
