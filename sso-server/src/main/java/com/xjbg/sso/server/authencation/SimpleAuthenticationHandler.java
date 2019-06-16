package com.xjbg.sso.server.authencation;

import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.util.ConvertUtils;
import com.xjbg.sso.server.model.User;
import com.xjbg.sso.server.service.user.IUserService;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/20
 */
@Getter
@Setter
public class SimpleAuthenticationHandler extends AbstractAuthenticationHandler {
    private IUserService userService;

    public SimpleAuthenticationHandler() {
    }

    public SimpleAuthenticationHandler(IUserService userService) {
        this.userService = userService;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticate(Credentials credentials) {
        UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) credentials;
        User user = userService.queryByStrategy(usernamePasswordCredentials.getUsername());
        return user == null ? null : new SimpleAuthenticationInfo(new SimplePrincipal(user.getUsername(), ConvertUtils.convert(user, UserDTO.class)), user.getPasswordHash(), user.getSalt());
    }
}
