package com.abb.bye.client.service;

import com.abb.bye.client.domain.SiteDO;
import com.abb.bye.client.domain.enums.SiteTag;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface SiteService {
    /**
     * 查询
     *
     * @return
     */
    List<SiteDO> list();

    /**
     * db加载
     *
     * @return
     */
    List<SiteDO> listFromDB();

    /**
     * 查询
     *
     * @param tags
     * @param list
     * @param status
     * @return
     */
    List<SiteDO> filter(List<SiteDO> list, List<SiteTag> tags, Byte status);

    /**
     * 通过ID获取
     *
     * @param id
     * @return
     */
    SiteDO get(long id);

    /**
     * 通过ID获取
     *
     * @param id
     * @return
     */
    SiteDO getFromDB(long id);

    /**
     * 通过appKey获取
     *
     * @param appKey
     * @return
     */
    SiteDO getBySiteKey(String appKey);

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
}
