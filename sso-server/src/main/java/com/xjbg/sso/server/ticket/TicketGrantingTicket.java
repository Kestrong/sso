package com.xjbg.sso.server.ticket;

import java.util.Map;

/**
 * @author kesc
 * @since 2019/5/17
 */
public interface TicketGrantingTicket extends Ticket {
    String PREFIX = "TGC-";

    /**
     * get principal
     *
     * @return
     */
    String getPrincipal();

    /**
     * Gets an immutable map of service ticket and services accessed by this ticket-granting ticket.
     *
     * @return
     */
    Map<String, String> getServices();

    /**
     * Remove all services of the TGT (at logout).
     */
    void removeAllServices();

    /**
     * Grant a ServiceTicket for a specific service.
     *
     * @param serviceTicketExpireInMills
     * @param id                         The unique identifier for this ticket.
     * @param service                    The service for which we are granting a ticket
     * @return the service ticket granted to a specific service for the principal of the TicketGrantingTicket
     */
    ServiceTicket grantServiceTicket(String id, String service, long serviceTicketExpireInMills);

    /**
     * Grant a ProxyTicket for a specific service.
     *
     * @param id
     * @param targetService
     * @param serviceTicketExpireInMills
     * @return
     */
    ProxyTicket grantProxyTicket(String id, String targetService, long serviceTicketExpireInMills);
}
