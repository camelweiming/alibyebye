package com.abb.bye.service;

import com.abb.bye.client.domain.ProgrammeDO;
import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.enums.ProgrammeTag;
import com.abb.bye.client.service.ProgrammeService;
import com.abb.bye.mapper.ProgrammeMapper;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.Md5;
import com.abb.bye.utils.ProgrammeHelper;
import com.abb.bye.utils.Tracer;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    public ResultDTO<Void> copyFromSource(ProgrammeSourceDO programmeSourceDO) {
        try {
            ProgrammeDO programmeDO = ProgrammeHelper.convert(programmeSourceDO);
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(ProgrammeDO.ATTRS_SOURCE_ID, programmeSourceDO.getSourceId());
            attributes.put(ProgrammeDO.ATTRS_SOURCE_SITE, programmeSourceDO.getSite());
            programmeDO.setAttributes(JSON.toJSONString(attributes));
            Set<Integer> tags = new HashSet<>();
            tags.add(ProgrammeTag.FROM_SOURCE.getType());
            programmeDO.setTags(Joiner.on(",").join(tags));
            setProperties(programmeDO);
            programmeDO.setCategories(programmeSourceDO.getCategory() == null ? null : programmeSourceDO.getCategory().toString());
            programmeMapper.copyFromSource(programmeDO);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            tracer.trace("Error copyFromSource:" + programmeSourceDO, e);
            return ResultDTO.buildError(e.getMessage());
        }
    }

    public void setProperties(ProgrammeDO programmeDO) {
        String formatTitle = CommonUtils.clean(programmeDO.getTitle()) + "_" + programmeDO.getReleaseYear();
        programmeDO.setUniqueKey(Md5.getInstance().getMD5String(formatTitle));
    }
}
