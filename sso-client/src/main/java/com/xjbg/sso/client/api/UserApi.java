package com.xjbg.sso.client.api;

import com.xjbg.sso.client.api.impl.DefaultFallbackUserApiImpl;
import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.request.UserRequest;
import com.xjbg.sso.core.response.BaseResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author kesc
 * @since 2019/5/22
 */
@FeignClient(name = "sso-server", path = "/sso-server", fallback = DefaultFallbackUserApiImpl.class)
public interface UserApi {

    /**
     * register
     *
     * @param userRequest
     * @return
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    BaseResponse<UserDTO> register(@RequestBody UserRequest userRequest);
}
