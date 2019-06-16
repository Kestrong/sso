package com.xjbg.sso.core.dto;

import lombok.Data;

/**
 * @author kesc
 * @since 2019/6/15
 */
@Data
public class ScopeAndOpenIdDTO extends BaseDTO {
    private String openId;
    private String scope;

    public ScopeAndOpenIdDTO() {
    }

    public ScopeAndOpenIdDTO(String openId, String scope) {
        this.openId = openId;
        this.scope = scope;
    }
}
