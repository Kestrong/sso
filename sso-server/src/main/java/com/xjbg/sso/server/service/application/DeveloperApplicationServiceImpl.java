package com.xjbg.sso.server.service.application;

import com.xjbg.sso.server.dao.DeveloperApplicationMapper;
import com.xjbg.sso.server.model.DeveloperApplication;
import com.xjbg.sso.server.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author kesc
 * @since 2019/6/13
 */
@Service
public class DeveloperApplicationServiceImpl extends BaseServiceImpl<DeveloperApplication, DeveloperApplicationMapper> implements IDeveloperApplicationService {
}
