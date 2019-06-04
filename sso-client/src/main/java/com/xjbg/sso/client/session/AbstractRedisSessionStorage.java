package com.xjbg.sso.client.session;

import com.xjbg.sso.core.cache.CacheTemplate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @author kesc
 * @since 2019/6/3
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractRedisSessionStorage implements SessionStorage {
    protected static final String MANAGED_SESSIONS_KEY = "MANAGED_SESSIONS_KEY:";
    private static final long DEFAULT_TIME_OUT = 60 * 30 * 1000L;
    private CacheTemplate cacheTemplate;
    private long timeoutMills = DEFAULT_TIME_OUT;

    @Override
    public HttpSession removeSessionByMappingId(String mappingId) {
        String sessionId = getCacheTemplate().valueGet(MANAGED_SESSIONS_KEY + mappingId);
        if (sessionId != null) {
            log.debug("Found mapping[{}] for session.  Session[{}] Removed.", mappingId, sessionId);
            removeBySessionById(sessionId);
        }
        return null;
    }

    @Override
    public void addSessionById(String mappingId, HttpSession session) {
        getCacheTemplate().valueSet(MANAGED_SESSIONS_KEY + mappingId, session.getId(), getTimeoutMills(), TimeUnit.MILLISECONDS);
    }
}
