package com.abb.bye.mapper;

import com.abb.bye.client.domain.SiteConfigsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface SiteConfigMapper {
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
     * 加载所有配置
     *
     * @param env
     * @return
     */
    List<SiteConfigsDO> listAll(@Param("env") String env);

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    SiteConfigsDO get(long id);

    /**
     * 获取key
     *
     * @param site
     * @param configKey
     * @param env
     * @return
     */
    SiteConfigsDO getConfig(@Param("site") int site, @Param("configKey") String configKey, @Param("env") String env);
}
