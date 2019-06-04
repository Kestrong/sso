package com.xjbg.sso.client.api.impl;

import com.xjbg.sso.client.api.CasApi;
import com.xjbg.sso.core.dto.TicketValidationDTO;
import com.xjbg.sso.core.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author kesc
 * @since 2019/5/21
 */
@Service
@Slf4j
public class DefaultFallbackCasApiImpl implements CasApi {

    @Override
    public BaseResponse<TicketValidationDTO> validate(String ticket, String service) {
        log.warn("remote service [CasApi.validate] is not available,return to fallback service");
        return new BaseResponse<>(new TicketValidationDTO(false));
    }

    @Override
    public BaseResponse<String> serviceTicket(String tgtId, String service) {
        log.warn("remote service [CasApi.serviceTicket] is not available,return to fallback service");
        return new BaseResponse<>();
    }

    @Override
    public BaseResponse<String> proxyTicket(String tgtId, String service) {
        log.warn("remote service [CasApi.proxyTicket] is not available,return to fallback service");
        return new BaseResponse<>();
    }
}
