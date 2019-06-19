package com.xjbg.sso.server.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.TimeZone;

/**
 * @author kesc
 * @since 2018/1/27
 */
public final class LogJsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.addMixIn(Object.class, MixPassword.class);
    }

    private LogJsonUtil() {

    }

    @JsonIgnoreProperties(value = {"tgtId", "password", "salt", "passwordHash", "appKey", "accessToken"})
    public static class MixPassword {
    }

    /**
     * 将 POJO 对象转为 JSON 字符串
     */
    public static <T> String toJson(T pojo) {
        String json;
        try {
            json = objectMapper.writeValueAsString(pojo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return json;
    }

}
