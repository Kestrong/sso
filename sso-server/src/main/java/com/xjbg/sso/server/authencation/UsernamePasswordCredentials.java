package com.xjbg.sso.server.authencation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/16
 */
@Getter
@Setter
public class UsernamePasswordCredentials implements Credentials {
    private String username;
    private String password;
    private boolean rememberMe;

    public UsernamePasswordCredentials() {
        rememberMe = false;
    }

    public UsernamePasswordCredentials(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    @Override
    public Object getPrincipal() {
        return getUsername();
    }

    @Override
    public Object getCredentials() {
        return getPassword();
    }
}
