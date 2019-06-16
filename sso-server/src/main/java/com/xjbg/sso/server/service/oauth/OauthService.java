package com.xjbg.sso.server.service.oauth;

import com.google.common.base.Joiner;
import com.xjbg.sso.core.cache.CacheTemplate;
import com.xjbg.sso.core.dto.AccessTokenDTO;
import com.xjbg.sso.core.dto.ScopeAndOpenIdDTO;
import com.xjbg.sso.core.dto.UserDTO;
import com.xjbg.sso.core.enums.BusinessExceptionEnum;
import com.xjbg.sso.core.enums.GrandType;
import com.xjbg.sso.core.enums.ResponseType;
import com.xjbg.sso.core.request.AuthRequest;
import com.xjbg.sso.core.util.CollectionUtil;
import com.xjbg.sso.core.util.CommonUtil;
import com.xjbg.sso.core.util.StringUtil;
import com.xjbg.sso.server.authencation.AuthenticationInfo;
import com.xjbg.sso.server.authencation.AuthenticationManager;
import com.xjbg.sso.server.authencation.UsernamePasswordCredentials;
import com.xjbg.sso.server.model.Application;
import com.xjbg.sso.server.service.application.IApplicationService;
import com.xjbg.sso.server.util.WebContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author kesc
 * @since 2019/6/13
 */
@Service
public class OauthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private CacheTemplate cacheTemplate;

    public UserDTO login(UsernamePasswordCredentials credentials) {
        AuthenticationInfo authenticationInfo = authenticationManager.doAuthenticate(credentials);
        return (UserDTO) authenticationInfo.getPrincipals().getPrincipal();
    }

    private void checkOrigin(String source, String target) {
        if (!target.startsWith(source)) {
            throw BusinessExceptionEnum.INVALID_REDIRECT_URI.getException();
        }
    }

    public void authorize(String responseType,
                          String appId,
                          String redirectUri,
                          String scope,
                          String state,
                          String openId) {
        Application application = applicationService.queryByAppId(appId);
        if (application == null) {
            throw BusinessExceptionEnum.INVALID_APP_ID.getException();
        }
        this.checkOrigin(application.getRedirectUri(), redirectUri);
        StringBuilder sb = new StringBuilder(redirectUri).append(redirectUri.contains("#") ? '&' : '#');
        if (ResponseType.CODE.getResponseType().equals(responseType)) {
            String code = UUID.randomUUID().toString();
            sb.append("code=").append(code);
            if (StringUtil.isNotBlank(state)) {
                sb.append('&').append("state=").append(state);
            }
            cacheTemplate.valueSet(code, new ScopeAndOpenIdDTO(openId, scopeRetrieve(scope, application.getScope())), 5L, TimeUnit.MINUTES);
        } else if (ResponseType.TOKEN.getResponseType().equals(responseType)) {
            AccessTokenDTO accessToken = this.createAccessToken(scopeRetrieve(scope, application.getScope()), openId, null);
            sb.append("accessToken=").append(accessToken.getAccessToken())
                    .append("&tokenType=bearer").append("&expiredIn=").append(accessToken.getExpiredIn())
                    .append("&scope=").append(accessToken.getScope()).append("&openId=").append(openId);
            if (StringUtil.isNotBlank(state)) {
                sb.append('&').append("state=").append(state);
            }
        } else {
            throw BusinessExceptionEnum.INVALID_RESPONSE_TYPE.getException();
        }
        CommonUtil.sendRedirect(WebContextUtil.getResponse(), sb.toString());
    }

    private String scopeRetrieve(String target, String source) {
        if (StringUtil.isBlank(target) || StringUtil.isBlank(source)) {
            return StringUtil.EMPTY;
        }
        List<String> sourceScopes = Arrays.asList(source.split(StringUtil.COMMA));
        List<String> targetScopes = Arrays.asList(target.split(StringUtil.COMMA));
        List<String> result = CollectionUtil.filter(targetScopes, sourceScopes::contains);
        return Joiner.on(StringUtil.COMMA).join(result);
    }

    private AccessTokenDTO createAccessToken(String scope, String openId, String refreshToken) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setExpiredIn(60 * 60);
        accessTokenDTO.setAccessToken(UUID.randomUUID().toString());
        accessTokenDTO.setRefreshToken(refreshToken);
        accessTokenDTO.setScope(scope);
        accessTokenDTO.setOpenId(openId);
        cacheTemplate.valueSet(accessTokenDTO.getAccessToken(), accessTokenDTO, 1L, TimeUnit.HOURS);
        return accessTokenDTO;
    }

    public AccessTokenDTO accessToken(AuthRequest authRequest) {
        Application application = applicationService.queryByAppId(authRequest.getAppId());
        if (application == null || !application.getAppKey().equals(authRequest.getAppKey())) {
            throw BusinessExceptionEnum.INVALID_APP_ID.getException();
        }
        if (StringUtil.isBlank(authRequest.getCode())) {
            throw BusinessExceptionEnum.INVALID_CODE.getException();
        }
        AccessTokenDTO accessTokenDTO;
        if (GrandType.AUTHORIZATION_CODE.getGrandType().equals(authRequest.getGrandType())) {
            ScopeAndOpenIdDTO scopeAndOpenIdDTO = cacheTemplate.valueGet(authRequest.getCode(), ScopeAndOpenIdDTO.class);
            if (scopeAndOpenIdDTO == null) {
                throw BusinessExceptionEnum.INVALID_CODE.getException();
            }
            cacheTemplate.remove(authRequest.getCode());
            accessTokenDTO = this.createAccessToken(scopeAndOpenIdDTO.getScope(), scopeAndOpenIdDTO.getOpenId(), UUID.randomUUID().toString());
            cacheTemplate.valueSet(accessTokenDTO.getRefreshToken(), scopeAndOpenIdDTO, 30L, TimeUnit.DAYS);
        } else if (GrandType.REFRESH_TOKEN.getGrandType().equals(authRequest.getGrandType())) {
            ScopeAndOpenIdDTO scope = cacheTemplate.valueGet(authRequest.getCode(), ScopeAndOpenIdDTO.class);
            if (scope == null) {
                throw BusinessExceptionEnum.INVALID_CODE.getException();
            }
            accessTokenDTO = this.createAccessToken(scope.getScope(), scope.getOpenId(), authRequest.getCode());
        } else if (GrandType.CLIENT_CREDENTIALS.getGrandType().equals(authRequest.getGrandType())) {
            accessTokenDTO = this.createAccessToken(this.scopeRetrieve(authRequest.getScope(), application.getScope()), null, null);
        } else {
            throw BusinessExceptionEnum.INVALID_GRAND_TYPE.getException();
        }
        return accessTokenDTO;
    }

    public boolean valid(String accessToken, String scope, boolean containAll) {
        AccessTokenDTO accessTokenDTO = cacheTemplate.valueGet(accessToken, AccessTokenDTO.class);
        if (accessTokenDTO == null) {
            return false;
        }
        if (StringUtil.isBlank(scope) || StringUtil.isBlank(accessTokenDTO.getScope())) {
            return false;
        }
        List<String> hasScopes = Arrays.asList(accessTokenDTO.getScope().split(StringUtil.COMMA));
        List<String> checkScopes = Arrays.asList(scope.split(StringUtil.COMMA));
        if (!containAll) {
            for (String s : checkScopes) {
                if (hasScopes.contains(s)) {
                    return true;
                }
            }
            return false;
        }
        return hasScopes.containsAll(checkScopes);
    }

    public String openId(String accessToken) {
        AccessTokenDTO accessTokenDTO = cacheTemplate.valueGet(accessToken, AccessTokenDTO.class);
        if (accessTokenDTO == null) {
            throw BusinessExceptionEnum.UNAUTHORIZED.getException();
        }
        return accessTokenDTO.getOpenId();
    }
}
