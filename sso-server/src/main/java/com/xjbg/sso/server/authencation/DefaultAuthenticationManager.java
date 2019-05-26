package com.xjbg.sso.server.authencation;

import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import com.xjbg.sso.core.util.CollectionUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author kesc
 * @since 2019/5/20
 */
@Getter
@Setter
@Slf4j
public class DefaultAuthenticationManager implements AuthenticationManager {

    private List<AuthenticationHandler> handlers;

    public DefaultAuthenticationManager() {
    }

    public DefaultAuthenticationManager(List<AuthenticationHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public AuthenticationInfo doAuthenticate(Credentials credentials) {
        if (CollectionUtil.isEmpty(handlers)) {
            log.warn("please set handlers to authenticate provided credentials");
            throw BusinessExceptionEnum.NO_HANDLER_SUPPORT_THIS_CREDENTIAL.getException();
        }
        for (AuthenticationHandler handler : handlers) {
            if (handler.support(credentials)) {
                return handler.authenticate(credentials);
            }
        }
        throw BusinessExceptionEnum.NO_HANDLER_SUPPORT_THIS_CREDENTIAL.getException();
    }
}
