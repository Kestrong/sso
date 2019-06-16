package com.xjbg.sso.client.api;

import com.xjbg.sso.client.api.impl.DefaultFallbackOauthApiImpl;
import com.xjbg.sso.client.api.impl.DefaultFallbackUserApiImpl;
import com.xjbg.sso.core.dto.AccessTokenDTO;
import com.xjbg.sso.core.request.AuthRequest;
import com.xjbg.sso.core.response.BaseResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @author kesc
 * @since 2019/6/15
 */
@FeignClient(name = "sso-server", path = "/sso-server", fallback = DefaultFallbackOauthApiImpl.class)
public interface OauthApi {
    /**
     * get access token
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/oauth2/accessToken", method = RequestMethod.POST)
    BaseResponse<AccessTokenDTO> accessToken(@RequestBody @Valid AuthRequest request);

    /**
     * valid access token
     *
     * @param accessToken
     * @param scope
     * @param containAll
     * @return
     */
    @RequestMapping(value = "/oauth2/valid", method = RequestMethod.GET)
    BaseResponse<Boolean> valid(@RequestParam(value = "accessToken") String accessToken,
                                @RequestParam(value = "scope") String scope,
                                @RequestParam(value = "containAll", defaultValue = "true") boolean containAll);

    /**
     * get open id
     *
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/oauth2/openId", method = RequestMethod.GET)
    BaseResponse<String> openId(@RequestParam(value = "accessToken") String accessToken);
}
