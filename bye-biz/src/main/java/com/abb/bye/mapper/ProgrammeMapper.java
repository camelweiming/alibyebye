package com.abb.bye.mapper;

import com.abb.bye.client.domain.ProgrammeDO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface ProgrammeMapper {
    /**
     * insert
     *
     * @param programmeDO
     * @return
     */
    long insert(ProgrammeDO programmeDO);

    /**
     * 插入或更新
     *
     * @param programmeDO
     * @return
     */
    long copyFromSource(ProgrammeDO programmeDO);

    /**
     * get
     *
     * @param id
     * @return
     */
    ProgrammeDO get(long id);
}
