package com.xjbg.sso.server.cookie;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
@Setter
public class CasCookieManager extends AbstractCookieManager implements ICasCookieManager {
    private String ticketGrantCookieName = "CASTGC";
    private String rememberMeCookieName = "rememberMe";
    private int rememberExpireInSeconds = 60 * 60 * 24;

    @Override
    public Cookie getGrantTicketCookie(HttpServletRequest request) {
        return getCookie(request, ticketGrantCookieName);
    }

    @Override
    public Cookie getRememberMeCookie(HttpServletRequest request) {
        return getCookie(request, rememberMeCookieName);
    }

    @Override
    public void addGrantTicketCookie(HttpServletRequest request, HttpServletResponse response, String value) {
        addCookie(request, response, ticketGrantCookieName, value);
    }

    @Override
    public void addRememberMeCookie(HttpServletRequest request, HttpServletResponse response, String value) {
        Cookie cookie = createCookie(request, rememberMeCookieName, value);
        cookie.setMaxAge(rememberExpireInSeconds);
        response.addCookie(cookie);
    }
}
