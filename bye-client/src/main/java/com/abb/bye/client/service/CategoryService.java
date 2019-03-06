package com.abb.bye.client.service;

import com.abb.bye.client.domain.CategoryDO;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface CategoryService {
    /**
     * 查询目录
     *
     * @param id
     * @return
     */
    CategoryDO get(long id);

    /**
     * 查询类目
     *
     * @return
     */
    List<CategoryDO> list();

    /**
     * 插入目录
     *
     * @param categoryDO
     */
    void insert(CategoryDO categoryDO);

    /**
     * 更新目录
     *
     * @param categoryDO
     */
    void update(CategoryDO categoryDO);
}
