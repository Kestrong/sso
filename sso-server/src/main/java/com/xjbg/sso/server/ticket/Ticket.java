package com.xjbg.sso.server.ticket;

/**
 * @author kesc
 * @since 2019/5/17
 */
public interface Ticket {
    /**
     * get id
     *
     * @return
     */
    String getId();

    /**
     * is expired
     *
     * @return
     */
    boolean isExpire();
}
