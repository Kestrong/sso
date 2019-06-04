package com.xjbg.sso.client.filter;

import com.xjbg.sso.client.ConfigKeyConstants;
import com.xjbg.sso.client.api.CasApi;
import com.xjbg.sso.core.dto.TicketValidationDTO;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author kesc
 * @since 2019/5/21
 */
@Getter
@Setter
@Slf4j
public class CasValidationFilter extends AbstractCasFilter {
    private CasApi casApi;
    private boolean redirectAfterSuccess = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        ServletContext context = filterConfig.getServletContext();
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
        casApi = ac.getBean(CasApi.class);
        String redirectAfterSuccess = filterConfig.getInitParameter(ConfigKeyConstants.REDIRECT_AFTER_SUCCESS_KEY);
        if (StringUtil.isNotBlank(redirectAfterSuccess)) {
            this.redirectAfterSuccess = Boolean.valueOf(redirectAfterSuccess);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String ticket = retrieveTicketFromRequest(request);
        final HttpSession session = request.getSession(false);
        boolean loginFlag = session != null && session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) != null;
        if (!loginFlag && StringUtil.isNotBlank(ticket)) {
            log.debug("Attempting to validate ticket: {}", ticket);
            try {
                String serviceUrl = constructServiceUrl(request, response);
                BaseResponse<TicketValidationDTO> validateResponse = casApi.validate(ticket, serviceUrl);
                if (validateResponse == null || !validateResponse.isSuccess() || !validateResponse.getData().getOk()) {
                    log.error("validate ticket fail,ticket:{},service:{}", ticket, serviceUrl);
                    response.setStatus(403);
                    return;
                }
                log.debug("Successfully authenticated user: {}", validateResponse.getData().getUsername());

                super.setConstCasAssertion(request, validateResponse.getData().getUsername());
                if (redirectAfterSuccess) {
                    log.debug("Redirecting after successful ticket validation.");
                    CommonUtil.sendRedirect(response, serviceUrl);
                }
                return;
            } catch (final Exception e) {
                log.error(e.getMessage(), e);
                response.setStatus(403);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
