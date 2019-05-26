package com.xjbg.sso.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author kesc
 * @since 2019/5/23
 */
@Getter
@Setter
@ToString
public class TicketValidationDTO extends BaseDTO {
    private Boolean ok;
    private String username;

    public TicketValidationDTO() {
    }

    public TicketValidationDTO(Boolean ok) {
        this.ok = ok;
    }

    public TicketValidationDTO(Boolean ok, String username) {
        this.ok = ok;
        this.username = username;
    }
}
