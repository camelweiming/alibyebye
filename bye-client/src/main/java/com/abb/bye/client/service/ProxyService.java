package com.abb.bye.client.service;

import com.abb.bye.client.domain.ProxyDO;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
public interface ProxyService {
    /**
     * 取最优代理列表
     *
     * @param count
     * @param successRate
     * @return
     */
    List<ProxyDO> list(int count, double successRate);

    /**
     * 选取proxy
     *
     * @return
     */
    ProxyDO get();
}
