package com.xjbg.sso.server.authencation;

/**
 * @author kesc
 * @since 2019/5/18
 */
public interface AuthenticationInfo {
    /**
     * Returns all principals associated with the corresponding Subject.  Each principal is an identifying piece of
     * information useful to the application such as a username, or user id, a given name, etc - anything useful
     * to the application to identify the current <code>Subject</code>.
     * <p/>
     * The returned principals should <em>not</em> contain any credentials used to verify principals, such
     * as passwords, private keys, etc.  Those should be instead returned by {@link #getCredentials() getCredentials()}.
     *
     * @return all principals associated with the corresponding Subject.
     */
    Principal getPrincipals();

    /**
     * Returns the credentials associated with the corresponding Subject.  A credential verifies one or more of the
     * {@link #getPrincipals() principals} associated with the Subject, such as a password or private key.  Credentials
     * are used by Shiro particularly during the authentication process to ensure that submitted credentials
     * during a login attempt match exactly the credentials here in the <code>AuthenticationInfo</code> instance.
     *
     * @return the credentials associated with the corresponding Subject.
     */
    Object getCredentials();
}
