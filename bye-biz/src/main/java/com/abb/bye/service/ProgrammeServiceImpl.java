package com.abb.bye.service;

import com.abb.bye.client.domain.ProgrammeDO;
import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.service.ProgrammeService;
import com.abb.bye.mapper.ProgrammeMapper;
import com.abb.bye.utils.Tracer;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/3/7
 */
@Service("programmeService")
public class ProgrammeServiceImpl implements ProgrammeService {
    @Resource
    private ProgrammeMapper programmeMapper;
    private Tracer tracer = new Tracer("PROGRAMME_SOURCE");

    @Override
    public ResultDTO<Void> copyFromSource(ProgrammeDO programmeDO) {
        try {
            programmeMapper.copyFromSource(programmeDO);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            tracer.trace("Error copyFromSource:" + programmeDO, e);
            return ResultDTO.buildError(e.getMessage());
        }
    }
}
