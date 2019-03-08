package com.abb.bye.mapper;

import com.abb.bye.client.domain.ProgrammeSourceDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface ProgrammeSourceMapper {
    /**
     * insert
     *
     * @param programmeSourceDO
     * @return
     */
    long insert(ProgrammeSourceDO programmeSourceDO);

    /**
     * 插入或更新
     *
     * @param programmeSourceDO
     * @return
     */
    long replace(ProgrammeSourceDO programmeSourceDO);

    /**
     * get
     *
     * @param id
     * @return
     */
    ProgrammeSourceDO get(long id);

    /**
     * 取所有源ID
     *
     * @param site
     * @param gmtModified
     * @return
     */
    List<String> getSourceIds(@Param("site") int site, @Param("gmtModified") Date gmtModified);
}
