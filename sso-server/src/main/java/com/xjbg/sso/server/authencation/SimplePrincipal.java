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
    private String id;
    private Object principal;

    public SimplePrincipal() {
    }

    public SimplePrincipal(String id) {
        this.id = id;
    }

    public SimplePrincipal(String id, Object principal) {
        this.id = id;
        this.principal = principal;
    }
}
