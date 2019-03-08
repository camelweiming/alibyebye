package com.abb.bye.client.service;

import com.abb.bye.client.domain.RejectStrategyConfig;

/**
 * 前置拦截
 *
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public interface RejectStrategy {
    /**
     * 启动
     *
     * @param site
     * @param rejectStrategyConfig
     */
    void init(int site, RejectStrategyConfig rejectStrategyConfig);

    /**
     * 销毁
     *
     * @param site
     */
    void destroy(int site);

    /**
     * 阻断处理
     *
     * @param site
     * @param sourceId
     * @param rejectStrategyConfig
     * @return
     */
    boolean reject(int site, String sourceId, RejectStrategyConfig rejectStrategyConfig);
}
