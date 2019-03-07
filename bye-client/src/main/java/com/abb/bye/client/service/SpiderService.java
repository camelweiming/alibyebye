package com.abb.bye.client.service;

import com.abb.bye.client.domain.ResultDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface SpiderService {
    /**
     * 启动
     *
     * @param site
     * @return
     */
    ResultDTO<Void> start(int site);

    /**
     * 停止
     *
     * @param site
     * @return
     */
    ResultDTO<Void> stop(int site);
}
