package com.abb.bye.client.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/4
 */
public interface SimpleCache {
    /**
     * 查询
     *
     * @param key
     * @return
     * @throws Exception
     */
    Serializable get(String key) throws Exception;

    /**
     * 批量获取
     *
     * @param keys
     * @return
     * @throws Exception
     */
    List<Serializable> mGet(List<String> keys) throws Exception;

    /**
     * 查询,不抛异常
     *
     * @param key
     * @return
     */
    Serializable getQuietly(String key);

    /**
     * 存数据
     *
     * @param key
     * @param data
     * @param expireSeconds
     * @return
     * @throws IOException
     */
    boolean put(String key, Serializable data, int expireSeconds) throws IOException;

    /**
     * 存数据，不抛异常
     *
     * @param key
     * @param data
     * @param expireSeconds
     * @return
     */
    boolean putQuietly(String key, Serializable data, int expireSeconds);

    /**
     * 删除
     *
     * @param key
     * @return
     */
    boolean delete(String key);
}
