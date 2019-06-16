package com.xjbg.sso.client.filter;

import com.xjbg.sso.client.ConfigKeyConstants;
import com.xjbg.sso.core.Protocol;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kesc
 * @since 2019/5/21
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractCasFilter implements Filter {
    public static final String CONST_CAS_ASSERTION = "_const_cas_assertion_";
    private Protocol protocol = Protocol.CAS1;
    /**
     * Sets where response.encodeUrl should be called on service urls when constructed.
     */
    private boolean encodeServiceUrl = true;
    /**
     * The name of the server.  Should be in the following format: {protocol}:{hostName}:{port}.
     * Standard ports can be excluded.
     */
    private String serverName;

    /**
     * The exact url of the service.
     */
    private String service;

    public AbstractCasFilter() {
    }

    public AbstractCasFilter(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String encodeServiceUrlParameter = filterConfig.getInitParameter(ConfigKeyConstants.ENCODE_SERVICE_URL_KEY);
        encodeServiceUrl = StringUtil.isBlank(encodeServiceUrlParameter) ? true : Boolean.valueOf(encodeServiceUrlParameter);
        serverName = filterConfig.getInitParameter(ConfigKeyConstants.SERVER_NAME_KEY);
        service = filterConfig.getInitParameter(ConfigKeyConstants.SERVICE_KEY);
    }

    @Override
    public void destroy() {

    }

    /**
     * Template method to allow you to change how you retrieve the ticket.
     *
     * @param request the HTTP ServletRequest.  CANNOT be NULL.
     * @return the ticket if its found, null otherwise.
     */
    protected String retrieveTicketFromRequest(final HttpServletRequest request) {
        String parameter = CommonUtil.safeGetParameter(request, this.protocol.getArtifactParameterName());
        if (StringUtil.isBlank(parameter)) {
            parameter = request.getHeader(this.protocol.getArtifactParameterName());
        }
        return parameter;
    }

    protected final String constructServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
        return CommonUtil.constructServiceUrl(request, response, this.service, this.serverName,
                this.protocol.getServiceParameterName(),
                this.protocol.getArtifactParameterName(), this.encodeServiceUrl);
    }

    /**
     * Note that trailing slashes should not be used in the serverName.  As a convenience for this common misconfiguration, we strip them from the provided
     * value.
     *
     * @param serverName the serverName. If this method is called, this should not be null.  This AND service should not be both configured.
     */
    public final void setServerName(final String serverName) {
        if (serverName != null && serverName.endsWith("/")) {
            this.serverName = serverName.substring(0, serverName.length() - 1);
            log.info("Eliminated extra slash from serverName [{}].  It is now [{}]", serverName, this.serverName);
        } else {
            this.serverName = serverName;
        }
    }
}
