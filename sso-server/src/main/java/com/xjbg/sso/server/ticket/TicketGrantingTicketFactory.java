package com.xjbg.sso.server.ticket;

import com.xjbg.sso.server.authencation.Principal;

/**
 * @author kesc
 * @since 2019/5/17
 */
public interface TicketGrantingTicketFactory {
    /**
     * create TGT
     *
     * @param expireInMills
     * @param principal
     * @return
     */
    TicketGrantingTicket create(Principal principal, long expireInMills);
}
