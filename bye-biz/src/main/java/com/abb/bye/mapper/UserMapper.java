package com.abb.bye.mapper;

import com.abb.bye.client.domain.UserDO;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface UserMapper {
    /**
     * 插入用户
     *
     * @param userDO
     */
    void insert(UserDO userDO);

    /**
     * 通过id查询
     * @param id
     * @return
     */
    UserDO getById(long id);

    /**
     * 通过用户查询
     * @param name
     * @return
     */
    UserDO getByName(String name);
}
