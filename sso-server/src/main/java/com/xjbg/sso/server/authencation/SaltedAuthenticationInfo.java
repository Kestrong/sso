package com.xjbg.sso.server.authencation;

/**
 * @author kesc
 * @since 2019/5/20
 */
public interface SaltedAuthenticationInfo extends AuthenticationInfo {

    /**
     * salt
     *
     * @return
     */
    Object getSalt();
}
