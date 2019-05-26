package com.xjbg.sso.client.api.impl;

import com.xjbg.sso.client.api.UserApi;
import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.request.UserRequest;
import com.xjbg.sso.core.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author kesc
 * @since 2019/5/22
 */
@Service
@Slf4j
public class DefaultFallbackUserApiImpl implements UserApi {

    @Override
    public BaseResponse<UserDTO> register(UserRequest userRequest) {
        log.warn("remote service [UserApi.register] is not available,return to fallback service");
        return null;
    }
}
