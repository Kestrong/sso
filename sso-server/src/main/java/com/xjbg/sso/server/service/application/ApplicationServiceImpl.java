package com.xjbg.sso.server.service.application;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjbg.sso.core.enums.StatusEnum;
import com.xjbg.sso.server.dao.ApplicationMapper;
import com.xjbg.sso.server.model.Application;
import com.xjbg.sso.server.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author kesc
 * @since 2019/6/13
 */
@Service
public class ApplicationServiceImpl extends BaseServiceImpl<Application, ApplicationMapper> implements IApplicationService {

    @Override
    public Application queryByAppId(String appId) {
        return mapper.selectOne(new QueryWrapper<Application>().eq("app_id", appId)
                .eq("status", StatusEnum.NORMAL.getStatus()));
    }

}
