package com.xjbg.sso.server.service.user;

import com.xjbg.sso.server.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kesc
 * @since 2019/5/17
 */
public final class UserQueryStrategies {
    private List<UserQueryStrategy> userQueryStrategies;
    private UserQueryStrategy defaultStrategy;

    public UserQueryStrategies(IUserService userService) {
        this.userQueryStrategies = new ArrayList<>();
        this.defaultStrategy = new DefaultUserQueryStrategy(userService);
        this.userQueryStrategies.add(new UserQueryMailStrategy(userService, defaultStrategy));
        this.userQueryStrategies.add(new UserQueryPhoneStrategy(defaultStrategy, userService));
    }

    public boolean addStrategy(UserQueryStrategy userQueryStrategy) {
        return userQueryStrategies.add(userQueryStrategy);
    }

    public UserQueryStrategy getStrategy(String token) {
        for (UserQueryStrategy strategy : userQueryStrategies) {
            if (strategy.support(token)) {
                return strategy;
            }
        }
        return defaultStrategy;
    }

    private class DefaultUserQueryStrategy implements UserQueryStrategy<String> {
        private IUserService userService;

        public DefaultUserQueryStrategy(IUserService userService) {
            this.userService = userService;
        }

        @Override
        public User getUser(String token) {
            return userService.queryByUsername(token);
        }

        @Override
        public boolean support(String token) {
            return true;
        }
    }

}
