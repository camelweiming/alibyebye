package com.abb.bye.client.service;

import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.domain.ResultDTO;

import java.util.Date;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
public interface ProgrammeSourceService {
    /**
     * 更新
     *
     * @param programmeSourceDO
     * @return
     */
    ResultDTO<Void> insertOrUpdate(ProgrammeSourceDO programmeSourceDO);

    /**
     * 加载所有源ID
     *
     * @param site
     * @param gmtModified
     * @return
     */
    List<String> getSourceIds(int site, Date gmtModified);
}
