package com.xjbg.sso.server.authencation;

/**
 * @author kesc
 * @since 2019/5/20
 */
public interface AuthenticationHandler {
    /**
     * Authenticates a user based on the submitted {@link Credentials}.
     *
     * @param credentials
     * @return
     */
    AuthenticationInfo authenticate(Credentials credentials);

    /**
     * Returns <tt>true</tt> if this handler wishes to authenticate the given {@link Credentials} credentials
     *
     * @param credentials
     * @return
     */
    boolean support(Credentials credentials);

    /**
     * cache
     *
     * @param authenticationInfo
     */
    void cache(AuthenticationInfo authenticationInfo);

    /**
     * find authentication info from cache
     *
     * @param key
     * @return
     */
    AuthenticationInfo getFromCache(Object key);
}
