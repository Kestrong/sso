package com.xjbg.sso.server.service.application;

import com.xjbg.sso.server.dao.DeveloperMapper;
import com.xjbg.sso.server.model.Developer;
import com.xjbg.sso.server.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author kesc
 * @since 2019/6/13
 */
@Service
public class DeveloperServiceImpl extends BaseServiceImpl<Developer, DeveloperMapper> implements IDeveloperService {
}
