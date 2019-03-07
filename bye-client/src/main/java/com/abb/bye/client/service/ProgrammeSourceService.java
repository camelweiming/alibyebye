package com.abb.bye.client.service;

import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.domain.ResultDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
public interface ProgrammeSourceService {
    /**
     * 更新
     *
     * @param programmeSourceDO
     */
    ResultDTO<Void> insertOrUpdate(ProgrammeSourceDO programmeSourceDO);
}
