package com.xjbg.sso.server.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kesc
 * @since 2019/5/17
 */
public interface ICookieManager {

    /**
     * find cookie with specify name
     *
     * @param request
     * @param name
     * @return
     */
    Cookie getCookie(HttpServletRequest request, String name);

    /**
     * add cookie
     *
     * @param name
     * @param request
     * @param response
     * @param value
     */
    void addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value);

    /**
     * cookie expired seconds
     *
     * @param expireInSeconds
     */
    void setExpireInSeconds(int expireInSeconds);

    /**
     * delete cookie
     *
     * @param request
     * @param response
     * @param name
     */
    void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name);
}
