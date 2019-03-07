package com.abb.bye.service;

import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.service.ProgrammeSourceService;
import com.abb.bye.mapper.ProgrammeSourceMapper;
import com.abb.bye.utils.Tracer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
@Service("programmeSourceService")
public class ProgrammeSourceServiceImpl implements ProgrammeSourceService {
    @Resource
    private ProgrammeSourceMapper programmeSourceMapper;
    private Tracer tracer = new Tracer("PROGRAMME_SOURCE");

    @Override
    public ResultDTO<Void> insertOrUpdate(ProgrammeSourceDO programmeSourceDO) {
        try {
            programmeSourceMapper.replace(programmeSourceDO);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            tracer.trace("Error insertOrUpdate:" + programmeSourceDO, e);
            return ResultDTO.buildError(e.getMessage());
        }
    }
}