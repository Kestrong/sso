package com.xjbg.sso.server.service.application;

import com.xjbg.sso.server.model.Application;
import com.xjbg.sso.server.service.base.IBaseService;

/**
 * @author kesc
 * @since 2019/6/13
 */
public interface IApplicationService extends IBaseService<Application> {


    /**
     * appid
     *
     * @param appId
     * @return
     */
    Application queryByAppId(String appId);
}
