package com.abb.bye.utils.http;

import org.apache.http.HttpHost;

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
public interface PriorityProxyProvider {
    /**
     * 设置代理
     *
     * @return
     */
    HttpHost getProxy();

    /**
     * 成功
     *
     * @param httpHost
     * @param cost
     */
    void success(HttpHost httpHost, long cost);

    /**
     * 失败
     *
     * @param httpHost
     */
    void failed(HttpHost httpHost);
}
