package com.xjbg.sso.server.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kesc
 * @since 2019/5/20
 */
public interface ICasCookieManager extends ICookieManager {
    /**
     * find grant ticket cookie
     *
     * @param request
     * @return
     */
    Cookie getGrantTicketCookie(HttpServletRequest request);

    /**
     * find remember me cookie
     *
     * @param request
     * @return
     */
    Cookie getRememberMeCookie(HttpServletRequest request);

    /**
     * add ticket grant cookie
     *
     * @param request
     * @param response
     * @param value
     */
    void addGrantTicketCookie(HttpServletRequest request, HttpServletResponse response, String value);

    /**
     * add remember me cookie
     *
     * @param request
     * @param response
     * @param value
     */
    void addRememberMeCookie(HttpServletRequest request, HttpServletResponse response, String value);
}
