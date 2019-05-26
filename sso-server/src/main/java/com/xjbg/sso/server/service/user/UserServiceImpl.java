package com.xjbg.sso.server.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import com.xjbg.sso.core.request.UserRequest;
import com.xjbg.sso.core.util.Authenticator;
import com.xjbg.sso.core.util.ConvertUtils;
import com.xjbg.sso.server.dao.UserMapper;
import com.xjbg.sso.server.model.User;
import com.xjbg.sso.server.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author kesc
 * @since 2019/5/16
 */
@Service(value = "userService")
public class UserServiceImpl extends BaseServiceImpl<User, UserMapper> implements IUserService {
    @Autowired
    private UserQueryStrategies userQueryStrategies;
    @Autowired
    private Authenticator authenticator;

    @Override
    public User queryByUsername(String username) {
        return mapper.selectOne(new QueryWrapper<User>().eq("username", username));
    }

    @Override
    public User queryByMail(String mail) {
        return mapper.selectOne(new QueryWrapper<User>().eq("mail", mail));
    }

    @Override
    public User queryByPhone(String phone) {
        return mapper.selectOne(new QueryWrapper<User>().eq("telephone", phone));
    }

    @Override
    public User queryByStrategy(String token) {
        UserQueryStrategy<String> strategy = userQueryStrategies.getStrategy(token);
        return strategy.getUser(token);
    }

    @Override
    public UserDTO register(UserRequest userRequest) {
        User user = ConvertUtils.convert(userRequest, User.class);
        byte[] salt = authenticator.createSalt();
        try {
            byte[] hash = authenticator.createHash(userRequest.getPassword(), salt);
            user.setPasswordHash(authenticator.byteToBase64String(hash));
            String saltString = authenticator.byteToBase64String(salt);
            user.setSalt(saltString);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw BusinessExceptionEnum.SYSTEM_ERROR.getException();
        }
        user.setOpenId(UUID.randomUUID().toString());
        try {
            super.add(user);
        } catch (DuplicateKeyException e) {
            logger.error(e.getMessage(), e);
            throw BusinessExceptionEnum.USER_EXIST.getException();
        }
        return ConvertUtils.convert(user, UserDTO.class);
    }
}

