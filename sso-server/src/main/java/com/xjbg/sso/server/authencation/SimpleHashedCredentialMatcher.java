package com.xjbg.sso.server.authencation;

import com.xjbg.sso.core.cache.CacheTemplate;
import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import com.xjbg.sso.core.util.Authenticator;
import com.xjbg.sso.server.RedisKeyConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author kesc
 * @since 2019/5/20
 */
@Getter
@Setter
@Slf4j
public class SimpleHashedCredentialMatcher implements CredentialMatcher {
    private Authenticator authenticator;
    private CacheTemplate cacheTemplate;

    public SimpleHashedCredentialMatcher() {
    }

    public SimpleHashedCredentialMatcher(Authenticator authenticator, CacheTemplate cacheTemplate) {
        this.authenticator = authenticator;
        this.cacheTemplate = cacheTemplate;
    }

    @Override
    public boolean doCredentialsMatch(Credentials credentials, AuthenticationInfo info) {
        UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) credentials;
        String lockKey = RedisKeyConstants.LOGIN_RETRY_LIMIT_KEY + usernamePasswordCredentials.getUsername();
        Long retryTimes = cacheTemplate.valueIncrement(lockKey);
        if (retryTimes == 1L) {
            try {
                cacheTemplate.expire(lockKey, 1L, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                cacheTemplate.remove(lockKey);
            }
        } else if (retryTimes > 5L) {
            cacheTemplate.expire(lockKey, 1L, TimeUnit.HOURS);
            throw BusinessExceptionEnum.EXCESSIVE_LOGIN.getException();
        }
        if (info == null) {
            throw BusinessExceptionEnum.ABNORMAL_ACCOUNT.getException();
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = (SimpleAuthenticationInfo) info;
        return authenticator.validate(usernamePasswordCredentials.getPassword(), simpleAuthenticationInfo.getCredentials(), simpleAuthenticationInfo.getSalt());
    }

}
