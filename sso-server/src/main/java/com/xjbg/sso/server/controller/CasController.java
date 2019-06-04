package com.xjbg.sso.server.controller;

import com.xjbg.sso.core.dto.TicketValidationDTO;
import com.xjbg.sso.core.request.TicketRequest;
import com.xjbg.sso.core.response.BaseResponse;
import com.xjbg.sso.server.authencation.UsernamePasswordCredentials;
import com.xjbg.sso.server.service.cas.CasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author kesc
 * @since 2019/5/16
 */
@RestController
@RequestMapping(value = "/cas")
@Api(description = "CAS Restful api")
public class CasController extends BaseController {
    @Autowired
    private CasService casService;

    @PostMapping(value = "/tickets")
    @ApiOperation(value = "Login Api")
    public BaseResponse<String> login(@RequestBody @Valid TicketRequest request) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(request.getUsername(), request.getPassword());
        credentials.setRememberMe(request.getRememberMe());
        return super.setResponse(casService.login(credentials));
    }

    @GetMapping(value = "/login")
    @ApiOperation(value = "Login or redirect api")
    public BaseResponse<String> loginOrRedirect() {
        casService.loginOrRedirect();
        return super.setResponse();
    }

    @PostMapping(value = "/tickets/{tgtId}")
    @ApiOperation(value = "Get Service Ticket")
    public BaseResponse<String> serviceTicket(@PathVariable(value = "tgtId") String tgtId,
                                              @RequestParam(value = "service") String service) {
        return super.setResponse(casService.serviceTicket(tgtId, service));
    }

    @PostMapping(value = "/tickets/proxy/{tgtId}")
    @ApiOperation(value = "Get Proxy Ticket")
    public BaseResponse<String> proxyTicket(@PathVariable(value = "tgtId") String tgtId,
                                            @RequestParam(value = "service") String service) {
        return super.setResponse(casService.proxyTicket(tgtId, service));
    }

    @GetMapping(value = "/validate")
    @ApiOperation(value = "Validate Service Ticket")
    public BaseResponse<TicketValidationDTO> validate(
            @RequestParam(value = "ticket") String ticket,
            @RequestParam(value = "service") String service) {
        return super.setResponse(casService.validate(service, ticket));
    }

    @DeleteMapping(value = "/tickets/{tgtId}")
    @ApiOperation(value = "Logout Api")
    public BaseResponse<Void> logout(@PathVariable(value = "tgtId") String tgtId) {
        casService.logout(tgtId);
        return super.setResponse();
    }
}
