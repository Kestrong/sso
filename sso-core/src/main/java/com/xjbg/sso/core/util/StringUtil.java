package com.xjbg.sso.core.util;


import org.apache.commons.lang3.StringUtils;


/**
 * @author kesc
 * @since 2017/12/25
 */
public final class StringUtil {
    public static final String EMPTY = "";
    public static final String COMMA = ",";

    public static boolean isBlank(String string) {
        return StringUtils.isBlank(string);
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static boolean isEmpty(String string) {
        return StringUtils.isEmpty(string);
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static String substringBefore(String str, String separator) {
        return StringUtils.substringBefore(str, separator);
    }
}
