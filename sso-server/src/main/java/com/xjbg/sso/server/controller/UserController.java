package com.xjbg.sso.server.controller;

import com.xjbg.sso.core.ScopeConstants;
import com.xjbg.sso.core.annonation.AuthScope;
import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.request.UserRequest;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.server.service.user.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author kesc
 * @since 2019/5/22
 */
@RestController
@RequestMapping(value = "/user")
@Api(description = "User Api")
public class UserController extends BaseController {
    @Autowired
    private IUserService userService;

    @PostMapping(value = "/register")
    @ApiOperation(value = "Register")
    public BaseResponse<UserDTO> register(@RequestBody @Valid UserRequest userRequest) {
        return super.setResponse(userService.register(userRequest));
    }

    @GetMapping(value = "/info", params = "accessToken")
    @ApiOperation(value = "user info")
    @AuthScope(hasScope = ScopeConstants.USER_INFO)
    public BaseResponse<UserDTO> userInfo(@RequestParam(value = "openId") String openId) {
        return super.setResponse(userService.userInfo(openId));
    }
}
