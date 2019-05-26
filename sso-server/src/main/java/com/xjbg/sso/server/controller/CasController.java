package com.xjbg.sso.server.controller;

import com.xjbg.sso.core.dto.TicketValidationDTO;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.server.authencation.UsernamePasswordCredentials;
import com.xjbg.sso.server.service.cas.CasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kesc
 * @since 2019/5/16
 */
@Controller
@RequestMapping(value = "/cas")
public class CasController extends BaseController {
    @Autowired
    private CasService casService;

    @GetMapping(value = "/login")
    public String loginOrRedirect(Model model, HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam(required = false, defaultValue = "false") boolean renew) {
        model.addAttribute("redirectValue", CommonUtil.safeGetParameter(request, casService.getProtocol().getServiceParameterName()));
        model.addAttribute("redirectParam", casService.getProtocol().getServiceParameterName());
        return casService.loginOrRedirect(request, response, renew);
    }

    @GetMapping(value = "/login-success")
    public String loginSuccess() {
        return "login-success";
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public BaseResponse login(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam String username,
                              @RequestParam String password,
                              @RequestParam(required = false, defaultValue = "false") boolean rememberMe) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        credentials.setRememberMe(rememberMe);
        casService.login(credentials, request, response);
        return super.setResponse();
    }

    @GetMapping(value = "/validate")
    @ResponseBody
    public BaseResponse<TicketValidationDTO> validate(
            @RequestParam(value = "ticket") String ticket,
            @RequestParam(value = "service") String service) {
        return super.setResponse(casService.validate(service, ticket));
    }

    @GetMapping(value = "/logout")
    @ResponseBody
    public BaseResponse logout(HttpServletRequest request, HttpServletResponse response) {
        casService.logout(request, response);
        return super.setResponse();
    }
}
