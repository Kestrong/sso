package com.xjbg.sso.client.filter;

import com.xjbg.sso.client.ConfigKeyConstants;
import com.xjbg.sso.client.session.HashMapSessionStorageImpl;
import com.xjbg.sso.client.session.SessionStorage;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ReflectionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.web.multipart.support.MultipartResolutionDelegate.isMultipartRequest;

/**
 * @author kesc
 * @since 2019/5/21
 */
@Slf4j
@Setter
@Getter
public class CasSingleLogoutFilter extends AbstractCasFilter {
    private String logoutParameterName = "logoutRequest";
    private SessionStorage sessionStorage;
    private boolean eagerlyCreateSessions = true;
    private String sessionStorageClass;

    protected void initSessionStorage() {
        if (StringUtil.isBlank(sessionStorageClass)) {
            sessionStorage = new HashMapSessionStorageImpl();
        } else {
            sessionStorage = ReflectionUtils.createInstanceIfPresent(sessionStorageClass, new HashMapSessionStorageImpl());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        String logoutParam = filterConfig.getInitParameter(ConfigKeyConstants.LOGOUT_PARAMETER_KEY);
        if (StringUtil.isNotBlank(logoutParam)) {
            logoutParameterName = logoutParam;
        }
        String createSessionEarlyParam = filterConfig.getInitParameter(ConfigKeyConstants.EAGERLY_CREATE_SESSIONS_KEY);
        if (StringUtil.isNotBlank(createSessionEarlyParam)) {
            eagerlyCreateSessions = Boolean.valueOf(createSessionEarlyParam);
        }
        sessionStorageClass = filterConfig.getInitParameter(ConfigKeyConstants.SESSION_STORAGE_CLASS_KEY);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String ticket = retrieveTicketFromRequest(request);
        if (sessionStorage == null) {
            initSessionStorage();
        }
        if (StringUtil.isNotBlank(ticket)) {
            log.trace("Received a token request");
            recordSession(request);
            filterChain.doFilter(request, response);
        }
        if (isLogoutRequest(request)) {
            log.trace("Received a logout request");
            destroySession(request);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Destroys the current HTTP session for the given CAS logout request.
     *
     * @param request HTTP request containing a CAS logout message.
     */
    private void destroySession(final HttpServletRequest request) {
        final String token = CommonUtil.safeGetParameter(request, this.logoutParameterName);
        if (StringUtil.isBlank(token)) {
            log.error("Could not locate logout message of the request from {}", this.logoutParameterName);
            return;
        }
        log.trace("Logout request:\n{}", token);
        if (StringUtil.isNotBlank(token)) {
            final HttpSession session = this.sessionStorage.removeSessionByMappingId(token);

            if (session != null) {
                final String sessionID = session.getId();
                log.debug("Invalidating session [{}] for token [{}]", sessionID, token);
                try {
                    session.invalidate();
                } catch (final IllegalStateException e) {
                    log.debug("Error invalidating session.", e);
                }
                if (isServlet30()) {
                    try {
                        request.logout();
                    } catch (final ServletException e) {
                        log.debug("Error performing request.logout.");
                    }
                }
            }
        }
    }

    private static boolean isServlet30() {
        try {
            return HttpServletRequest.class.getMethod("logout") != null;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Associates a token request with the current HTTP session by recording the mapping
     * in the the configured {@link SessionStorage} container.
     *
     * @param request HTTP request containing an authentication token.
     */
    private void recordSession(final HttpServletRequest request) {
        final HttpSession session = request.getSession(this.eagerlyCreateSessions);

        if (session == null) {
            log.debug("No session currently exists (and none created).  Cannot record session information for single sign out.");
            return;
        }

        final String token = CommonUtil.safeGetParameter(request, getProtocol().getArtifactParameterName());
        log.debug("Recording session for token {}", token);

        try {
            this.sessionStorage.removeBySessionById(session.getId());
        } catch (final Exception e) {
            // ignore if the session is already marked as invalid. Nothing we can do!
        }
        sessionStorage.addSessionById(token, session);
    }

    /**
     * Determines whether the given request is a CAS  logout request.
     *
     * @param request HTTP request.
     * @return True if request is logout request, false otherwise.
     */
    private boolean isLogoutRequest(final HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            return !isMultipartRequest(request)
                    && StringUtil.isNotBlank(CommonUtil.safeGetParameter(request, this.logoutParameterName));
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return StringUtil.isNotBlank(CommonUtil.safeGetParameter(request, this.logoutParameterName));
        }
        return false;
    }
}
