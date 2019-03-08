package com.abb.bye.client.spider;

import com.abb.bye.client.domain.PageDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public interface SpiderProcessor {
    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 处理页面
     *
     * @param pageDTO
     */
    void process(PageDTO pageDTO);

    /**
     * 源ID
     *
     * @param url
     * @return
     */
    String parseSourceId(String url);
}
