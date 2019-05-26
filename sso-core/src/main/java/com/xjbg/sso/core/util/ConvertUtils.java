package com.xjbg.sso.core.util;

import com.google.common.collect.Lists;
import com.xjbg.sso.core.exception.BusinessException;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author kesc
 * @since 2018/1/17
 */
public class ConvertUtils {

    public static <S, R> List<R> convert(List<S> source, Class<R> clazz) {
        if (CollectionUtil.isEmpty(source)) {
            return Collections.emptyList();
        }
        List<R> result = Lists.newArrayList();
        source.forEach(x -> {
            try {
                R instance = clazz.newInstance();
                BeanUtils.copyProperties(x, instance);
                result.add(instance);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new BusinessException(e.getMessage(), e);
            }
        });
        return result;
    }

    public static <S, R> R convert(S source, Class<R> clazz) {
        try {
            R instance = clazz.newInstance();
            if (source == null) {
                return instance;
            }
            BeanUtils.copyProperties(source, instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }
}
