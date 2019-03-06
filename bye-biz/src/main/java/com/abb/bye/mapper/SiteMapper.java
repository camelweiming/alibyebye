package com.abb.bye.mapper;

import com.abb.bye.client.domain.SiteDO;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface SiteMapper {
    /**
     * 插入
     *
     * @param appDO
     */
    void insert(SiteDO appDO);

    /**
     * 更改
     *
     * @param appDO
     * @return
     */
    boolean update(SiteDO appDO);

    /**
     * 查询
     *
     * @return
     */
    List<SiteDO> list();

    /**
     * 通过ID获取
     *
     * @param id
     * @return
     */
    SiteDO get(long id);

    /**
     * 通过appKey获取
     *
     * @param appKey
     * @return
     */
    SiteDO getBySiteKey(String appKey);

    /**
     * 通过站点查询
     *
     * @param site
     * @return
     */
    SiteDO getBySite(int site);
}
