package com.xjbg.sso.server.filter;

import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author kesc
 * @since 2019/5/28
 */
public class SafeUrlFilter implements Filter {
    public static final String WHITE_LIST_KEY = "whiteList";
    public static final String BLACK_LIST_KEY = "blackList";
    private List<String> whiteList;
    private List<String> blackList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String whiteTableString = filterConfig.getInitParameter(WHITE_LIST_KEY);
        String blackTableString = filterConfig.getInitParameter(BLACK_LIST_KEY);
        if (StringUtil.isNotBlank(whiteTableString)) {
            String[] whiteTableArray = whiteTableString.split(StringUtil.COMMA);
            whiteList = Arrays.asList(whiteTableArray);
        } else {
            whiteList = Collections.emptyList();
        }
        if (StringUtil.isNotBlank(blackTableString)) {
            String[] blackTableArray = blackTableString.split(StringUtil.COMMA);
            blackList = Arrays.asList(blackTableArray);
        } else {
            blackList = Collections.emptyList();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        String webPath = CommonUtil.getBaseWebPath(request);
        if (!blackList.isEmpty() && blackList.contains(webPath)) {
            response.sendError(403);
        } else if (!whiteList.isEmpty() && !whiteList.contains(webPath)) {
            response.sendError(403);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
