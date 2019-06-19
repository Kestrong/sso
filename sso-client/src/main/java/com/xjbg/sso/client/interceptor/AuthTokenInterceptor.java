package com.xjbg.sso.client.interceptor;

import com.xjbg.sso.client.api.OauthApi;
import com.xjbg.sso.core.interceptor.AbstractAuthTokenInterceptor;
import com.xjbg.sso.core.response.BaseResponse;
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
    private OauthApi oauthApi;

    @Override
    protected boolean valid(String accessToken, String scope, boolean containAll) {
        BaseResponse<Boolean> validResponse = oauthApi.valid(accessToken, scope, containAll);
        if (validResponse == null || !validResponse.isSuccess() || !validResponse.getData()) {
            log.warn("invalid access token:[{}]", accessToken);
            return false;
        }
        return true;
    }
}
