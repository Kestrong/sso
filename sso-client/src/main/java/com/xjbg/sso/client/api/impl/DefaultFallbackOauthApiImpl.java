package com.xjbg.sso.client.api.impl;

import com.xjbg.sso.client.api.OauthApi;
import com.xjbg.sso.core.dto.AccessTokenDTO;
import com.xjbg.sso.core.request.AuthRequest;
import com.xjbg.sso.core.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author kesc
 * @since 2019/6/15
 */
@Service
@Slf4j
public class DefaultFallbackOauthApiImpl implements OauthApi {
    @Override
    public BaseResponse<AccessTokenDTO> accessToken(AuthRequest request) {
        log.warn("remote service [OauthApi.accessToken] is not available,return to fallback service");
        return null;
    }

    @Override
    public BaseResponse<Boolean> valid(String accessToken, String scope, boolean containAll) {
        log.warn("remote service [OauthApi.valid] is not available,return to fallback service");
        return null;
    }

    @Override
    public BaseResponse<String> openId(String accessToken) {
        log.warn("remote service [OauthApi.openId] is not available,return to fallback service");
        return null;
    }
}
