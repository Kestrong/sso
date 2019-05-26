package com.xjbg.sso.core.exception;


import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @ClassName BusinessException
 * @Description 验证异常
 * @date 2017年9月6日 上午8:50:22
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {
    private Object bindingResult;
    private String code;

    public BusinessException(BusinessExceptionEnum exceptionEnum, Object bindingResult) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
        this.bindingResult = bindingResult;
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable arg1) {
        super(message, arg1);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(BusinessExceptionEnum exceptionEnum, Throwable arg1) {
        super(exceptionEnum.getMsg(), arg1);
        this.code = exceptionEnum.getCode();
    }

    public BusinessException(String code, String message, Throwable arg1) {
        super(message, arg1);
        this.code = code;
    }
}
