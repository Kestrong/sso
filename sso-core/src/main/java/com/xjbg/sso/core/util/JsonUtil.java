package com.xjbg.sso.core.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

/**
 * @author kesc
 * @since 2018/1/27
 */
public final class JsonUtil {
    private static ObjectMapper objectMapper = new CustomObjectMapper();

    static {
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    public static <T> String toJsonString(T source) {
        try {
            return objectMapper.writeValueAsString(source);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static <T> T toObject(String source, Class<T> clazz) {
        try {
            return objectMapper.readValue(source, clazz);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static <T> List<T> toArray(String source, Class<T> tClass) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, new Class[]{tClass});
            return objectMapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
