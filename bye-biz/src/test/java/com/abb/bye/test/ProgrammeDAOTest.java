package com.abb.bye.test;

import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.mapper.ProgrammeSourceMapper;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class ProgrammeDAOTest extends BaseDAOTest {
    @Resource
    private ProgrammeSourceMapper programmeSourceMapper;

    @Test
    public void testInsert() {
        ProgrammeSourceDO programmeSourceDO = new ProgrammeSourceDO();
        programmeSourceDO.setSite(0);
        programmeSourceDO.setScore(0d);
        programmeSourceDO.setReleaseYear(2018);
        programmeSourceDO.setStatus((byte)0);
        programmeSourceDO.setTitle("test");
        programmeSourceDO.setSeconds(0);
        programmeSourceDO.setSourceId("xxx");
        programmeSourceDO.setUrl("xxxx");
        programmeSourceMapper.insert(programmeSourceDO);
    }

    @Test
    public void testGet() {
        ProgrammeSourceDO programmeSourceDO = programmeSourceMapper.get(1l);
        System.out.println(programmeSourceDO);
    }
}
