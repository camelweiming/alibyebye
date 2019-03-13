package com.abb.bye.mapper;

import com.abb.bye.client.domain.ProxyDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
public interface ProxyMapper {
    /**
     * 插入
     *
     * @param proxyDO
     */
    void insert(ProxyDO proxyDO);

    /**
     * 获取代理
     *
     * @param length
     * @param maxFailedCount
     * @return
     */
    List<String> list(@Param("length") int length, @Param("maxFailedCount") int maxFailedCount);

    /**
     * 查询所有
     *
     * @param id
     * @param maxFailedCount
     * @return
     */
    List<String> listAll(@Param("id") long id, @Param("length") int length, @Param("maxFailedCount") int maxFailedCount);
}
