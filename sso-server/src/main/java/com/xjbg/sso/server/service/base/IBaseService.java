package com.xjbg.sso.server.service.base;

/**
 * @author kesc
 * @since 2019/5/15
 */
public interface IBaseService<T> {

    /**
     * 新增
     *
     * @param record
     * @return
     */
    T add(T record);

    /**
     * 删除
     *
     * @param id
     */
    void delete(Integer id);

    /**
     * 修改
     *
     * @param record
     * @return
     */
    T update(T record);

    /**
     * 查询
     *
     * @param id
     * @return
     */
    T query(Integer id);
}
