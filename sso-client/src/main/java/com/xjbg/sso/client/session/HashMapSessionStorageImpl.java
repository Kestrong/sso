package com.xjbg.sso.client.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kesc
 * @since 2019/5/26
 */
public class HashMapSessionStorageImpl implements SessionStorage {
    /**
     * Maps the ID from the CAS server to the Session.
     */
    private final Map<String, HttpSession> MANAGED_SESSIONS = new ConcurrentHashMap<>();

    /**
     * Maps the Session ID to the key from the CAS Server.
     */
    private final Map<String, String> ID_TO_SESSION_KEY_MAPPING = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public synchronized void addSessionById(String mappingId, HttpSession session) {
        ID_TO_SESSION_KEY_MAPPING.put(session.getId(), mappingId);
        MANAGED_SESSIONS.put(mappingId, session);

    }

    @Override
    public synchronized void removeBySessionById(final String sessionId) {
        logger.debug("Attempting to remove Session=[{}]", sessionId);

        final String key = ID_TO_SESSION_KEY_MAPPING.get(sessionId);

        if (logger.isDebugEnabled()) {
            if (key != null) {
                logger.debug("Found mapping for session.  Session Removed.");
            } else {
                logger.debug("No mapping for session found.  Ignoring.");
            }
        }
        MANAGED_SESSIONS.remove(key);
        ID_TO_SESSION_KEY_MAPPING.remove(sessionId);
    }

    @Override
    public synchronized HttpSession removeSessionByMappingId(String mappingId) {
        final HttpSession session = MANAGED_SESSIONS.get(mappingId);

        if (session != null) {
            removeBySessionById(session.getId());
        }

        return session;
    }


}
