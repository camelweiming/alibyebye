package com.abb.bye.test.dao;

import com.abb.bye.client.domain.UserRelationDO;
import com.abb.bye.mapper.UserRelationMapper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public class UserRelationDAOTest extends BaseDAOTest {
    @Resource
    private UserRelationMapper userRelationMapper;

    @Test
    public void testInsert() {
        UserRelationDO userRelationDO = new UserRelationDO();
        userRelationDO.setUserId(1l);
        userRelationDO.setRefId(2l);
        userRelationDO.setRefType(0);
        userRelationDO.setStatus(UserRelationDO.STATUS_ENABLE);
        userRelationDO.setAttributes("{}");
        userRelationMapper.insertOrUpdate(userRelationDO);
    }

    @Test
    public void testList() {
        List<UserRelationDO> relationDOS = userRelationMapper.getByRelationId((byte)0, 2l);
        System.out.println(relationDOS);
        relationDOS = userRelationMapper.getByUserId((byte)0,1);
        System.out.println(relationDOS);
    }
}
