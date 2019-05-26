package com.xjbg.sso.server.service.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjbg.sso.server.model.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author kesc
 * @since 2019/5/15
 */
public class BaseServiceImpl<T extends BaseModel, M extends BaseMapper<T>> implements IBaseService<T> {
    protected final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    protected M mapper;

    @Override
    public T add(T record) {
        this.completeAddModel(record);
        mapper.insert(record);
        return record;
    }

    @Override
    public void delete(Integer id) {
        mapper.deleteById(id);
    }

    @Override
    public T update(T record) {
        this.completeUpdateModel(record);
        mapper.updateById(record);
        return record;
    }

    @Override
    public T query(Integer id) {
        return mapper.selectById(id);
    }

    private void completeUpdateModel(T record) {
        if (record.getDelFlg() == null) {
            record.setDelFlg(1);
        }
        if (record.getUpdateTime() == null) {
            record.setCreateTime(new Date());
        }
    }

    private void completeAddModel(T record) {
        this.completeUpdateModel(record);
        if (record.getCreateTime() == null) {
            record.setCreateTime(record.getUpdateTime());
        }
    }
}
