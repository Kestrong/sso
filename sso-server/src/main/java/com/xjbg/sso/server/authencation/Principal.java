package com.xjbg.sso.server.authencation;

/**
 * @author kesc
 * @since 2019/5/18
 */

public interface Principal {
    /**
     * get principal
     *
     * @return
     */
    Object getPrincipal();

    /**
     * may be username/mail/phone/openId etc.
     *
     * @return
     */
    String getId();
}
