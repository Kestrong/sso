package com.xjbg.sso.server.ticket;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
@Setter
public abstract class AbstractTicket implements Ticket {
    protected String id;
    /**
     * time mills when ticket is expire
     */
    protected long expireAt;

    @Override
    public boolean isExpire() {
        return System.currentTimeMillis() >= getExpireAt();
    }
}
