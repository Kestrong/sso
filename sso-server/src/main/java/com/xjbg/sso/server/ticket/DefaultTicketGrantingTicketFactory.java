package com.xjbg.sso.server.ticket;

import com.xjbg.sso.server.authencation.Principal;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
@Setter
public class DefaultTicketGrantingTicketFactory implements TicketGrantingTicketFactory {
    private IdGenerator idGenerator;

    public DefaultTicketGrantingTicketFactory() {
    }

    public DefaultTicketGrantingTicketFactory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public TicketGrantingTicket create(final Principal principal, final long expireInMills) {
        return new TicketGrantingTicketImpl(produceTicketId(), principal, expireInMills);
    }

    protected String produceTicketId() {
        String id = idGenerator.nextId();
        return TicketGrantingTicket.PREFIX + id;
    }
}
