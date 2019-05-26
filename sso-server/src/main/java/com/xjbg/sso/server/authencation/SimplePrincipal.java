package com.xjbg.sso.server.authencation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/18
 */
@Getter
@Setter
public class SimplePrincipal implements Principal {
    private String principal;

    public SimplePrincipal() {
    }

    public SimplePrincipal(String principal) {
        this.principal = principal;
    }

    @Override
    public String getId() {
        return principal;
    }
}
