package com.xjbg.sso.server.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author kesc
 * @since 2019/5/13
 */
@Getter
@Setter
public class User extends BaseModel {
    private String avatar;
    private String openId;
    private String username;
    private String passwordHash;
    private String salt;
    private String firstName;
    private String lastName;
    private String telephone;
    private String mail;
    private Date birthday;
    private String sex;
    private String zipCode;
    private String country;
    private String province;
    private String city;
    private String zone;
    private String address;
    private String idCard;
    private String systemCode;
    private String status;
    private String job;
    private String description;
}
