package com.xjbg.sso.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author kesc
 * @since 2019/5/17
 */
@Getter
@Setter
@ToString
public class UserDTO extends BaseDTO {
    private String avatar;
    private String openId;
    private String username;
    private Date birthday;
    private String sex;
    private String country;
    private String systemCode;
}
