package com.xjbg.sso.server.service.user;

import com.xjbg.sso.server.model.User;

import java.util.regex.Pattern;

/**
 * @author kesc
 * @since 2019/5/17
 */
public class UserQueryMailStrategy implements UserQueryStrategy<String> {
    private final static Pattern PATTERN = Pattern.compile("^[\\w_.-]+@[\\w-._]+$");
    private IUserService userService;
    private UserQueryStrategy<String> next;

    public UserQueryMailStrategy(IUserService userService) {
        this.userService = userService;
    }

    public UserQueryMailStrategy(IUserService userService, UserQueryStrategy<String> next) {
        this.userService = userService;
        this.next = next;
    }

    public void setNext(UserQueryStrategy<String> next) {
        this.next = next;
    }

    @Override
    public User getUser(String token) {
        User user = userService.queryByMail(token);
        if (user == null && next != null) {
            return next.getUser(token);
        }
        return user;
    }

    @Override
    public boolean support(String token) {
        return PATTERN.matcher(token).find();
    }
}
