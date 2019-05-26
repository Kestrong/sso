package com.xjbg.sso.server.controller;

import com.xjbg.sso.core.request.UserRequest;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.server.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author kesc
 * @since 2019/5/22
 */
@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {
    @Autowired
    private IUserService userService;

    @PostMapping(value = "/register")
    public BaseResponse register(@RequestBody @Valid UserRequest userRequest) {
        return super.setResponse(userService.register(userRequest));
    }
}
