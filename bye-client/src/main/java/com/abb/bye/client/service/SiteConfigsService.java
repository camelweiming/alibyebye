package com.abb.bye.client.service;

import com.abb.bye.client.domain.SiteConfigsDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author cenpeng.lwm
 * @since 2019/1/18
 */
public interface SiteConfigsService {
    /**
     * 匹配
     *
     * @param domain
     * @return
     */
    List<SiteConfigsDO> match(String domain);

    /**
     * 获得所有域名
     *
     * @return
     */
    Map<Integer, Set<String>> getDomains();

    /**
     * 获得系统配置
     *
     * @param site
     * @param configKey
     * @return
     */
    String getSiteSystemConfig(int site, String configKey);

    /**
     * 匹配domain
     *
     * @param domain
     * @return
     */
    Integer matchSite(String domain);

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

    /**
     * 通过key查询
     *
     * @param site
     * @param configKey
     * @return
     */
    String getFromDB(int site, String configKey);
}
