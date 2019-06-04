package com.xjbg.sso.server.service.user;

import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.request.UserRequest;
import com.xjbg.sso.server.model.User;
import com.xjbg.sso.server.service.base.IBaseService;

/**
 * @author kesc
 * @since 2019/5/16
 */
public interface IUserService extends IBaseService<User> {

    /**
     * query user by username
     *
     * @param username
     * @return
     */
    User queryByUsername(String username);

    /**
     * query user by mail
     *
     * @param mail
     * @return
     */
    User queryByMail(String mail);

    /**
     * query user by phone
     *
     * @param phone
     * @return
     */
    User queryByPhone(String phone);

    /**
     * query by strategy
     *
     * @param token
     * @return
     */
    User queryByStrategy(String token);

    /**
     * register
     *
     * @param userRequest
     * @return
     */
    UserDTO register(UserRequest userRequest);
}
