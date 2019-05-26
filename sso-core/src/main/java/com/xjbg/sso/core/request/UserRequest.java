package com.xjbg.sso.core.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

/**
 * @author kesc
 * @since 2019/5/22
 */
@Getter
@Setter
@ToString
public class UserRequest extends BaseRequest {
    private String avatar;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String firstName;
    private String lastName;
    private String telephone;
    @Email
    private String mail;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private String sex;
    private String zipCode;
    private String country;
    private String province;
    private String city;
    private String zone;
    private String address;
    private String idCard;
    private String job;
    private String description;
    private String systemCode;
}
