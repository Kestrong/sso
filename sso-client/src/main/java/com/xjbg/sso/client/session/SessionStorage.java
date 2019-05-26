package com.xjbg.sso.client.session;

import javax.servlet.http.HttpSession;

/**
 * manage sessions for logout request,you can implement your session storage,e.g. spring session
 *
 * @author kesc
 * @since 2019/5/26
 */
public interface SessionStorage {

    /**
     * Remove the HttpSession based on the mappingId.
     *
     * @param mappingId the id the session is keyed under.
     * @return the HttpSession if it exists.
     */
    HttpSession removeSessionByMappingId(String mappingId);

    /**
     * Remove a session by its Id.
     *
     * @param sessionId the id of the session.
     */
    void removeBySessionById(String sessionId);

    /**
     * Add a session by its mapping Id.
     *
     * @param mappingId the id to map the session to.
     * @param session   the HttpSession.
     */
    void addSessionById(String mappingId, HttpSession session);
}
