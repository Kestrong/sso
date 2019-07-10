package com.xjbg.sso.client.filter;

import com.xjbg.sso.client.ConfigKeyConstants;
import com.xjbg.sso.core.util.CollectionUtil;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author kesc
 * @since 2019/5/21
 */
@Setter
@Getter
@Slf4j
public class CasAuthenticationFilter extends AbstractCasFilter {
    private String casServerLoginUrl;
    private List<String> ignoreUrlPatterns;
    private final AntPathMatcher ignorePathMatcher = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        casServerLoginUrl = filterConfig.getInitParameter(ConfigKeyConstants.CAS_SERVER_LOGIN_URL_KEY);
        Assert.notNull(casServerLoginUrl, "casServerLoginUrl is null");
        String ignoreUrlPattern = filterConfig.getInitParameter(ConfigKeyConstants.IGNORE_URL_PATTERN_KEY);
        if (StringUtil.isNotBlank(ignoreUrlPattern)) {
            ignoreUrlPatterns = Arrays.asList(ignoreUrlPattern.split(","));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (isRequestUrlExcluded(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        final HttpSession session = request.getSession(false);
        boolean loginFlag = session != null && session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) != null;
        if (loginFlag) {
            filterChain.doFilter(request, response);
            return;
        }
        final String ticket = retrieveTicketFromRequest(request);
        if (StringUtil.isNotBlank(ticket)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String serviceUrl = constructServiceUrl(request, response);
        final String urlToRedirectTo = CommonUtil.constructRedirectUrl(this.casServerLoginUrl,
                getProtocol().getServiceParameterName(), serviceUrl);
        log.debug("redirecting to \"{}\"", urlToRedirectTo);
        CommonUtil.sendRedirect(response, urlToRedirectTo);
    }


    protected boolean isRequestUrlExcluded(final HttpServletRequest request) {
        if ("OPTIONS".equals(request.getMethod().toUpperCase())) {
            return true;
        }
        if (CollectionUtil.isEmpty(ignoreUrlPatterns)) {
            return false;
        }
        final String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        contextPath = "/".equals(contextPath) ? StringUtil.EMPTY : contextPath;
        for (String pattern : ignoreUrlPatterns) {
            if (ignorePathMatcher.match(contextPath + pattern, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
