package com.abb.bye.client.service;

import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.domain.ResultDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
public interface ProgrammeService {
    /**
     * 更新
     *
     * @param programmeSourceDO
     * @return
     */
    ResultDTO<Void> copyFromSource(ProgrammeSourceDO programmeSourceDO);
}
