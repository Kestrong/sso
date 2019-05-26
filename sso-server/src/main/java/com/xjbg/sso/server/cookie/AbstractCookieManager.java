package com.xjbg.sso.server.cookie;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kesc
 * @since 2019/5/20
 */
@Getter
@Setter
public abstract class AbstractCookieManager implements ICookieManager {
    private final static int DEFAULT_EXPIRE = 60 * 60 * 2;
    private int expireInSeconds = DEFAULT_EXPIRE;

    @Override
    public Cookie getCookie(HttpServletRequest request, final String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    @Override
    public void addCookie(HttpServletRequest request, HttpServletResponse response, final String name, final String value) {
        Cookie cookie = createCookie(request, name, value);
        cookie.setMaxAge(expireInSeconds);
        response.addCookie(cookie);
    }

    @Override
    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                }
            }
        }
    }

    protected Cookie createCookie(HttpServletRequest request, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        if (request.isSecure()) {
            cookie.setSecure(true);
        }
        return cookie;
    }
}
