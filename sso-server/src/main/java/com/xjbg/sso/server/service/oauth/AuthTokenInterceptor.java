package com.xjbg.sso.server.service.oauth;

import com.google.common.base.Joiner;
import com.xjbg.sso.core.annonation.AuthScope;
import com.xjbg.sso.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author kesc
 * @since 2019/3/18
 */
@Slf4j
@Getter
@Setter
public final class AuthTokenInterceptor extends HandlerInterceptorAdapter {
    private OauthService oauthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            AuthScope authScope = method.getAnnotation(AuthScope.class);
            if (authScope == null) {
                return true;
            }
            String accessToken = request.getParameter("accessToken");
            if (StringUtil.isBlank(accessToken)) {
                response.setStatus(401);
                return false;
            }
            if (!oauthService.valid(accessToken, Joiner.on(StringUtil.COMMA).join(authScope.hasScope()), authScope.retainAll())) {
                response.setStatus(401);
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }

}
