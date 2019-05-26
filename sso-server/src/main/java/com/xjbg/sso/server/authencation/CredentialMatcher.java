package com.xjbg.sso.server.authencation;

/**
 * @author kesc
 * @since 2019/5/20
 */
public interface CredentialMatcher {
    /**
     * Returns {@code true} if the provided token credentials match the stored account credentials,
     * {@code false} otherwise.
     *
     * @param credentials
     * @param info
     * @return
     */
    boolean doCredentialsMatch(Credentials credentials, AuthenticationInfo info);
}
