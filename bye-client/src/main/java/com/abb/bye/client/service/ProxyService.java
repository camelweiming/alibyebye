package com.abb.bye.client.service;

import com.abb.bye.client.domain.Proxy;
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
     * @param maxFailedCount
     * @return
     */
    List<String> list(int count, int maxFailedCount);

    /**
     * 检测
     */
    void check();

    /**
     * 置为成功
     *
     * @param proxyDO
     */
    void makeSuccess(ProxyDO proxyDO);

    /**
     * 置为失败
     *
     * @param host
     */
    void makeFailed(String host);

    /**
     * 代理
     *
     * @return
     */
    Proxy getProxy();

    /**
     * 活动代理
     *
     * @param n
     * @return
     */
    List<Proxy> getProxy(int n);

    /**
     * 汇报状态
     *
     * @param proxy
     */
    void report(Proxy proxy);
}
