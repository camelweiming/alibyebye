package com.abb.bye.client.service;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
public interface SequenceService {
    /**
     * 取ID
     *
     * @param name
     * @return
     */
    long next(final String name);
}
