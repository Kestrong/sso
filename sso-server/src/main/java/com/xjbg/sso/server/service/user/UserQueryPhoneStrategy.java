package com.xjbg.sso.server.service.user;

import com.xjbg.sso.server.model.User;

import java.util.regex.Pattern;

/**
 * @author kesc
 * @since 2019/5/17
 */
public class UserQueryPhoneStrategy implements UserQueryStrategy<String> {
    private final static Pattern PATTERN = Pattern.compile("^1\\d{10}$");
    private UserQueryStrategy<String> next;
    private IUserService userService;

    public UserQueryPhoneStrategy(IUserService userService) {
        this.userService = userService;
    }

    public UserQueryPhoneStrategy(UserQueryStrategy<String> next, IUserService userService) {
        this.next = next;
        this.userService = userService;
    }

    public void setNext(UserQueryStrategy<String> next) {
        this.next = next;
    }

    @Override
    public User getUser(String token) {
        User user = userService.queryByPhone(token);
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
