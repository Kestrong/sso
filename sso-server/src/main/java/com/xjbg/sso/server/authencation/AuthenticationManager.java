package com.xjbg.sso.server.authencation;

/**
 * @author kesc
 * @since 2019/5/20
 */
public interface AuthenticationManager {

    /**
     * do authenticate
     *
     * @param credentials
     * @return
     */
    AuthenticationInfo doAuthenticate(Credentials credentials);
}
