package com.xjbg.sso.client.filter;

import com.xjbg.sso.client.ConfigKeyConstants;
import com.xjbg.sso.core.util.CollectionUtil;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

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
        if (CollectionUtil.isEmpty(ignoreUrlPatterns)) {
            return false;
        }
        final StringBuffer urlBuffer = request.getRequestURL();
        if (request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        final String requestUri = urlBuffer.toString();
        for (String pattern : ignoreUrlPatterns) {
            if (ignorePathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }
}
