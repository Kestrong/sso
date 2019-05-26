package com.xjbg.sso.core.util;

import java.util.UUID;

/**
 * @author kesc
 * @since 2019/5/16
 */
public class RequestIdUtil {
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    public static String get() {
        return REQUEST_ID.get();
    }

    public static void generate() {
        REQUEST_ID.set(UUID.randomUUID().toString());
    }

    public static void remove() {
        REQUEST_ID.remove();
    }
}
