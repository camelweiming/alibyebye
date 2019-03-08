package com.abb.bye.utils;

import com.abb.bye.client.domain.PersonDO;
import com.abb.bye.client.domain.ProgrammeDO;
import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public class ProgrammeHelper {
    public static ProgrammeDO convert(ProgrammeSourceDO programmeSourceDO) {
        ProgrammeDO programme = new ProgrammeDO();
        CommonUtils.copyPropertiesQuietly(programmeSourceDO, programme);
        programme.setPerformers(toPersonString(programme.getPerformers()));
        programme.setDirectors(toPersonString(programme.getDirectors()));
        return programme;
    }

    public static String toPersonString(String personString) {
        if (personString == null) {
            return null;
        }
        List<PersonDO> personDOList = JSON.parseArray(personString, PersonDO.class);
        StringBuilder sb = new StringBuilder(64);
        int i = 0;
        for (PersonDO p : personDOList) {
            if (i++ > 0) {
                sb.append(";");
            }
            sb.append(p.getName());
        }
        return sb.toString();
    }
}
