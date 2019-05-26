package com.xjbg.sso.server.ticket;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
@Setter
public class ServiceTicketImpl extends AbstractTicket implements ServiceTicket {
    private String service;
    private String ticketGrantTicketId;

    public ServiceTicketImpl() {
    }

    public ServiceTicketImpl(final String id, final long expireInMills, final String service, final String ticketGrantTicketId) {
        this.id = id;
        this.expireAt = expireInMills + System.currentTimeMillis();
        this.service = service;
        this.ticketGrantTicketId = ticketGrantTicketId;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getTicketGrantTicketId() {
        return ticketGrantTicketId;
    }

    @Override
    public boolean isValidFor(final String serviceProvide) {
        return service.equals(serviceProvide);
    }
}
