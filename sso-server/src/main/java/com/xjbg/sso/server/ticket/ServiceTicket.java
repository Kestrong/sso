package com.xjbg.sso.server.ticket;

/**
 * @author kesc
 * @since 2019/5/17
 */
public interface ServiceTicket extends Ticket {
    /**
     * Prefix generally applied to unique ids generated
     * by UniqueTicketIdGenerator.
     */
    String PREFIX = "ST-";

    /**
     * get service
     *
     * @return
     */
    String getService();

    /**
     * get TGT id
     *
     * @return
     */
    String getTicketGrantTicketId();

    /**
     * Attempts to ensure that the service specified matches the service associated with the ticket.
     *
     * @param serviceProvide
     * @return
     */
    boolean isValidFor(String serviceProvide);
}
