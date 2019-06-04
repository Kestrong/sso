package com.xjbg.sso.core.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author kesc
 * @since 2019/5/30
 */
@Getter
@Setter
@ToString
public class TicketRequest extends BaseRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private Boolean rememberMe = false;
}
