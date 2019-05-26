package com.xjbg.sso.server.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kesc
 * @since 2019/5/15
 */
@Getter
@Setter
public class BaseModel implements Serializable {
    private Integer id;
    private Date createTime;
    private Date updateTime;
    private Integer delFlg;
}
