package com.xjbg.sso.client.api;

import com.xjbg.sso.client.api.impl.DefaultFallbackCasApiImpl;
import com.xjbg.sso.core.dto.TicketValidationDTO;
import com.xjbg.sso.core.response.BaseResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kesc
 * @since 2019/5/21
 */
@FeignClient(name = "sso-server", path = "/sso-server", fallback = DefaultFallbackCasApiImpl.class)
public interface CasApi {

    /**
     * validate ticket and service
     *
     * @param ticket
     * @param service
     * @return
     */
    @RequestMapping(value = "/cas/validate", method = RequestMethod.GET)
    BaseResponse<TicketValidationDTO> validate(@RequestParam(value = "ticket") String ticket,
                                               @RequestParam(value = "service") String service);

    /**
     * create a service ticket
     *
     * @param tgtId
     * @param service
     * @return service ticket id
     */
    @RequestMapping(value = "/cas/tickets/{tgtId}", method = RequestMethod.POST)
    BaseResponse<String> serviceTicket(@PathVariable(value = "tgtId") String tgtId,
                                       @RequestParam(value = "service") String service);

    /**
     * create a proxy ticket
     *
     * @param tgtId
     * @param service
     * @return
     */
    @RequestMapping(value = "/cas/tickets/proxy/{tgtId}", method = RequestMethod.POST)
    BaseResponse<String> proxyTicket(@PathVariable(value = "tgtId") String tgtId,
                                     @RequestParam(value = "service") String service);
}
