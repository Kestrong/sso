package com.xjbg.sso.server.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kesc
 * @since 2019/5/13
 */
@Getter
@Setter
public class Application extends BaseModel {
    private String appId;
    private String appKey;
    private String applicationName;
    private String status;
    private String description;
    private String scope;
    private String redirectUri;
    private String grandType;
}
