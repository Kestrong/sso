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
    private List<String> whiteList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String whiteTableString = filterConfig.getInitParameter("whiteList");
        if (StringUtil.isNotBlank(whiteTableString)) {
            String[] whiteTableArray = whiteTableString.split(",");
            whiteList = Arrays.asList(whiteTableArray);
        } else {
            whiteList = Collections.emptyList();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        String webPath = CommonUtil.getBaseWebPath(request);
        if (whiteList.isEmpty()) {
            filterChain.doFilter(request, response);
        } else if (StringUtil.isNotBlank(webPath) && whiteList.contains(webPath)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(403);
        }
    }

    @Override
    public void destroy() {

    }
}
