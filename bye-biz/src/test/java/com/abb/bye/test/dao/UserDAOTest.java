package com.abb.bye.test.dao;

import com.abb.bye.client.domain.UserDO;
import com.abb.bye.mapper.UserMapper;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
public class UserDAOTest extends BaseDAOTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        UserDO userDO = new UserDO();
        userDO.setName("camel2");
        userMapper.insert(userDO);
    }

    @Test
    public void testGetById() {
        UserDO userDO = userMapper.getById(1);
        System.out.println(userDO);
    }

    @Test
    public void testGetByName() {
        Long userDO = userMapper.getByName("camel");
        System.out.println(userDO);
    }
}
