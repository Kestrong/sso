package com.xjbg.sso.core.annonation;

import java.lang.annotation.*;

/**
 * @author kesc
 * @since 2019/6/15
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthScope {
    String[] hasScope() default {};

    boolean retainAll() default true;
}
