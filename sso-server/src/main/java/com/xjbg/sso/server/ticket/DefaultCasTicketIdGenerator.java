package com.xjbg.sso.server.ticket;

import java.util.UUID;

/**
 * @author kesc
 * @since 2019/5/17
 */
public class DefaultCasTicketIdGenerator implements IdGenerator {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
