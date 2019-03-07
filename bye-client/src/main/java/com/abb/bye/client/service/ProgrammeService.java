package com.abb.bye.client.service;

import com.abb.bye.client.domain.ProgrammeDO;
import com.abb.bye.client.domain.ResultDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
public interface ProgrammeService {
    /**
     * 更新
     *
     * @param programmeDO
     * @return
     */
    ResultDTO<Void> copyFromSource(ProgrammeDO programmeDO);
}
