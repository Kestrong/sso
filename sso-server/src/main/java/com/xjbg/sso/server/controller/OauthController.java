package com.xjbg.sso.server.controller;

import com.xjbg.sso.core.dto.AccessTokenDTO;
import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.request.AuthRequest;
import com.xjbg.sso.core.request.TicketRequest;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.server.authencation.UsernamePasswordCredentials;
import com.xjbg.sso.server.service.oauth.OauthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author kesc
 * @since 2019/6/13
 */
@Api(description = "OAuth2.0 Api")
@RestController
@RequestMapping(value = "/oauth2")
public class OauthController extends BaseController {
    @Autowired
    private OauthService oauthService;

    @PostMapping(value = "/login")
    @ApiOperation(value = "login")
    public BaseResponse<UserDTO> register(@RequestBody @Valid TicketRequest request) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(request.getUsername(), request.getPassword());
        return super.setResponse(oauthService.login(credentials));
    }

    @GetMapping(value = "/authorize")
    @ApiOperation(value = "authorize")
    public BaseResponse<UserDTO> userInfo(@RequestParam(value = "responseType") String responseType,
                                          @RequestParam(value = "appId") String appId,
                                          @RequestParam(value = "redirectUri") String redirectUri,
                                          @RequestParam(value = "scope", required = false) String scope,
                                          @RequestParam(value = "state", required = false) String state,
                                          @RequestParam(value = "openId", required = false) String openId) {
        oauthService.authorize(responseType, appId, redirectUri, scope, state, openId);
        return super.setResponse();
    }

    @PostMapping(value = "/accessToken")
    @ApiOperation(value = "get access token")
    public BaseResponse<AccessTokenDTO> accessToken(@RequestBody @Valid AuthRequest request) {
        return super.setResponse(oauthService.accessToken(request));
    }

    @GetMapping(value = "/valid")
    @ApiOperation(value = "valid access token")
    public BaseResponse<Boolean> valid(@RequestParam(value = "accessToken") String accessToken,
                                       @RequestParam(value = "scope") String scope,
                                       @RequestParam(value = "containAll", defaultValue = "true") boolean containAll) {
        return super.setResponse(oauthService.valid(accessToken, scope, containAll));
    }

    @GetMapping(value = "/openId")
    @ApiOperation(value = "get open id,if you present when authorize")
    public BaseResponse<String> openId(@RequestParam(value = "accessToken") String accessToken) {
        return super.setResponse(oauthService.openId(accessToken));
    }
}
