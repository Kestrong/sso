package com.xjbg.sso.client.session;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.DefaultSessionManager;

/**
 * @author kesc
 * @since 2019/6/3
 */
@Getter
@Setter
public class DefaultShiroSessionStorageImpl extends AbstractRedisSessionStorage {
    private DefaultSessionManager sessionManager;

    @Override
    public void removeBySessionById(String sessionId) {
        Session session = sessionManager.getSession(new DefaultSessionKey(sessionId));
        if (session != null) {
            sessionManager.stop(new DefaultSessionKey(sessionId));
        }
    }
}
