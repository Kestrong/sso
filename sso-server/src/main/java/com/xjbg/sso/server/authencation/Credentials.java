package com.xjbg.sso.server.authencation;

import java.io.Serializable;

/**
 * @author kesc
 * @since 2019/5/16
 */
public interface Credentials extends Serializable {

    /**
     * Returns the account identity submitted during the authentication process.
     *
     * @return the account identity submitted during the authentication process.
     */
    Object getPrincipal();

    /**
     * Returns the credentials submitted by the user during the authentication process that verifies
     * the submitted {@link #getPrincipal() account identity}.
     *
     * @return the credential submitted by the user during the authentication process.
     */
    Object getCredentials();
}
