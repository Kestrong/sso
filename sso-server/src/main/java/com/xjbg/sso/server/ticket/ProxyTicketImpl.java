package com.xjbg.sso.server.ticket;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/6/4
 */
@Getter
@Setter
public class ProxyTicketImpl extends ServiceTicketImpl implements ProxyTicket {

    public ProxyTicketImpl() {
        super();
    }

    public ProxyTicketImpl(final String id, final long expireInMills, final String service, final String ticketGrantTicketId) {
        super(id, expireInMills, service, ticketGrantTicketId);
    }
}
