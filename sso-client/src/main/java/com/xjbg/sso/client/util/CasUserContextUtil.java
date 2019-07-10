package com.xjbg.sso.client.util;

import com.xjbg.sso.client.filter.AbstractCasFilter;
import com.xjbg.sso.core.util.WebContextUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用于获取cas登录验证ticket通过后的username
 *
 * @author kesc
 * @since 2019/7/10
 */
public class CasUserContextUtil {

    public static String getUserName() {
        HttpServletRequest request = WebContextUtil.getRequest();
        if (request == null) {
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            Object attribute = request.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
            return attribute == null ? null : attribute.toString();
        }
        Object attribute = session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        return attribute == null ? null : attribute.toString();
    }

}
