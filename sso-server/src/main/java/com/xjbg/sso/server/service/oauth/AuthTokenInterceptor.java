package com.xjbg.sso.server.service.oauth;

import com.xjbg.sso.core.interceptor.AbstractAuthTokenInterceptor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kesc
 * @since 2019/3/18
 */
@Slf4j
@Getter
@Setter
public final class AuthTokenInterceptor extends AbstractAuthTokenInterceptor {
    private OauthService oauthService;

    @Override
    protected boolean valid(String accessToken, String scope, boolean containAll) {
        return oauthService.valid(accessToken, scope, containAll);
    }

}
