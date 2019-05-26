package com.xjbg.sso.server.authencation;

import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kesc
 * @since 2019/5/20
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler {
    private Class<? extends Credentials> credentialClass;
    private CredentialMatcher credentialMatcher;


    @Override
    public AuthenticationInfo authenticate(Credentials credentials) {
        AuthenticationInfo info = getFromCache(credentials.getPrincipal());
        if (info == null) {
            info = doGetAuthenticate(credentials);
        }
        if (info == null) {
            throw BusinessExceptionEnum.ABNORMAL_ACCOUNT.getException();
        }
        if (getCredentialMatcher() != null) {
            boolean match = getCredentialMatcher().doCredentialsMatch(credentials, info);
            if (!match) {
                throw BusinessExceptionEnum.LOGIN_FAILURE.getException();
            }
        } else {
            log.warn("please set CredentialMatcher to verify provided credentials");
        }
        cache(info);
        return info;
    }

    @Override
    public boolean support(Credentials credentials) {
        return credentials != null && getCredentialClass().isAssignableFrom(credentials.getClass());
    }

    protected abstract AuthenticationInfo doGetAuthenticate(Credentials credentials);

    @Override
    public void cache(AuthenticationInfo authenticationInfo) {

    }

    @Override
    public AuthenticationInfo getFromCache(Object key) {
        return null;
    }
}
