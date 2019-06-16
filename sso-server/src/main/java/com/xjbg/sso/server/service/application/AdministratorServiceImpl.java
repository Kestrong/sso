package com.xjbg.sso.server.service.application;

import com.xjbg.sso.server.dao.AdministratorMapper;
import com.xjbg.sso.server.model.Administrator;
import com.xjbg.sso.server.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author kesc
 * @since 2019/6/13
 */
@Service
public class AdministratorServiceImpl extends BaseServiceImpl<Administrator, AdministratorMapper> implements IAdministratorService {
}
