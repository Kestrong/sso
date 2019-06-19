package com.xjbg.sso.core.interceptor;

import com.google.common.base.Joiner;
import com.xjbg.sso.core.annonation.AuthScope;
import com.xjbg.sso.core.util.CollectionUtil;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author kesc
 * @since 2019/6/19
 */
@Slf4j
@Getter
@Setter
public abstract class AbstractAuthTokenInterceptor extends HandlerInterceptorAdapter {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private Map<Pair, List<String>> authUrlMap;

    /**
     * to do valid access token and scope
     *
     * @param accessToken
     * @param scope       only check if present
     * @param containAll
     * @return
     */
    protected abstract boolean valid(String accessToken, String scope, boolean containAll);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            String accessToken = request.getParameter("accessToken");
            AuthScope authScope = method.getAnnotation(AuthScope.class);
            if (authScope != null) {
                if (StringUtil.isBlank(accessToken)) {
                    response.setStatus(401);
                    return false;
                }
                if (!valid(accessToken, Joiner.on(StringUtil.COMMA).join(authScope.hasScope()), authScope.retainAll())) {
                    log.warn("invalid access token:[{}]", accessToken);
                    response.setStatus(401);
                    return false;
                }
                return true;
            }
            if (CollectionUtil.isNotEmpty(authUrlMap)) {
                final String requestURI = request.getRequestURI();
                for (Map.Entry<Pair, List<String>> entry : authUrlMap.entrySet()) {
                    if (!pathMatcher.match(entry.getKey().getKey().toString(), requestURI)) {
                        continue;
                    }
                    if (!valid(accessToken, Joiner.on(StringUtil.COMMA).join(entry.getValue()), (Boolean) entry.getKey().getValue())) {
                        log.warn("invalid access token:[{}]", accessToken);
                        response.setStatus(401);
                        return false;
                    }
                    return true;
                }
            }
        }
        return super.preHandle(request, response, handler);
    }

}
