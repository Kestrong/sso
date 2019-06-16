package com.xjbg.sso.core.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
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
    @Length(max = 256)
    private String avatar;
    @NotBlank
    @Length(max = 32)
    private String username;
    @NotBlank
    @Length(max = 32)
    private String password;
    @Length(max = 32)
    private String firstName;
    @Length(max = 32)
    private String lastName;
    @Length(max = 16)
    private String telephone;
    @Email
    @Length(max = 64)
    private String mail;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    @Length(max = 8)
    private String sex;
    @Length(max = 16)
    private String zipCode;
    @Length(max = 32)
    private String country;
    @Length(max = 32)
    private String province;
    @Length(max = 32)
    private String city;
    @Length(max = 32)
    private String zone;
    @Length(max = 256)
    private String address;
    @Length(max = 64)
    private String idCard;
    @Length(max = 32)
    private String job;
    @Length(max = 128)
    private String description;
    @Length(max = 16)
    private String systemCode;
}
