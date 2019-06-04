package com.xjbg.sso.server.filter;


import com.xjbg.sso.core.util.CollectionUtil;
import com.xjbg.sso.core.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author kesc
 * @since 2019/1/21
 */
public class CrosFilter implements Filter {
    private List<String> allowOrigins;
    private String allowMethods;
    private String allowCredentials;
    private String allowHeaders;
    private String exposeHeaders;

    public CrosFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String allowOrigins = filterConfig.getInitParameter("allowOrigins");
        if (StringUtil.isNotBlank(allowOrigins)) {
            this.allowOrigins = Arrays.asList(allowOrigins.split(","));
        }
        this.allowMethods = filterConfig.getInitParameter("allowMethods");
        this.allowCredentials = filterConfig.getInitParameter("allowCredentials");
        this.allowHeaders = filterConfig.getInitParameter("allowHeaders");
        this.exposeHeaders = filterConfig.getInitParameter("exposeHeaders");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (CollectionUtil.isNotEmpty(allowOrigins)) {
            String currentOrigin = request.getHeader("Origin");
            if (allowOrigins.contains(currentOrigin) || "*".equals(allowOrigins.get(0))) {
                response.setHeader("Access-Control-Allow-Origin", currentOrigin);
            }
        }

        if (StringUtil.isNotEmpty(this.allowMethods)) {
            response.setHeader("Access-Control-Allow-Methods", this.allowMethods);
        }

        if (StringUtil.isNotEmpty(this.allowCredentials)) {
            response.setHeader("Access-Control-Allow-Credentials", this.allowCredentials);
        }

        if (StringUtil.isNotEmpty(this.allowHeaders)) {
            response.setHeader("Access-Control-Allow-Headers", this.allowHeaders);
        }

        if (StringUtil.isNotEmpty(this.exposeHeaders)) {
            response.setHeader("Access-Control-Expose-Headers", this.exposeHeaders);
        }

        response.setHeader("X-Frame-Options", "*");
        filterChain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
