package com.abb.bye.client.service;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
public interface SequenceService {
    /**
     * 取ID
     *
     * @param key
     * @return
     */
    long next(final String key);
}
