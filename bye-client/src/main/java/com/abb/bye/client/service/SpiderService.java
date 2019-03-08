package com.abb.bye.client.service;

import com.abb.bye.client.domain.ResultDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface SpiderService {
    /**
     * 执行job
     *
     * @param site
     * @return
     */
    ResultDTO<Void> doJob(int site);

    /**
     * 创建job
     *
     * @param site
     * @return
     */
    ResultDTO<Runnable> createJob(int site);
}
