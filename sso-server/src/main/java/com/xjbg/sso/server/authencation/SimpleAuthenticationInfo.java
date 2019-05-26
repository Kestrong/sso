package com.xjbg.sso.server.authencation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/18
 */
@Getter
@Setter
public class SimpleAuthenticationInfo implements SaltedAuthenticationInfo {
    private Principal principals;
    private String credentials;
    private String salt;

    public SimpleAuthenticationInfo() {
    }

    public SimpleAuthenticationInfo(Principal principals, String credentials, String salt) {
        this.principals = principals;
        this.credentials = credentials;
        this.salt = salt;
    }
}
