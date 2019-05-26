package com.xjbg.sso.server.service.user;

import com.xjbg.sso.server.model.User;

/**
 * @author kesc
 * @since 2019/5/17
 */
public interface UserQueryStrategy<T> {
    /**
     * get user
     *
     * @param token
     * @return
     */
    User getUser(T token);

    /**
     * valid the token is support by this strategy
     *
     * @param token
     * @return
     */
    boolean support(T token);
}
