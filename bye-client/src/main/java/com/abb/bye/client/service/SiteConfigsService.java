package com.abb.bye.client.service;

import com.abb.bye.client.domain.SiteConfigsDO;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/1/18
 */
public interface SiteConfigsService {
    /**
     * 插入
     *
     * @param siteConfigsDO
     */
    void insert(SiteConfigsDO siteConfigsDO);

    /**
     * 更改
     *
     * @param siteConfigsDO
     * @return
     */
    boolean update(SiteConfigsDO siteConfigsDO);

    /**
     * 查询
     *
     * @param site
     * @return
     */
    List<SiteConfigsDO> list(int site);

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    SiteConfigsDO get(long id);
}
