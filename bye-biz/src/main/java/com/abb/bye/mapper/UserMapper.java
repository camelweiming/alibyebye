package com.abb.bye.mapper;

import com.abb.bye.client.domain.UserDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     *
     * @param id
     * @return
     */
    UserDO getById(long id);

    /**
     * 通过用户查询
     *
     * @param name
     * @return
     */
    Long getByName(String name);

    /**
     * 列表
     *
     * @param start
     * @param limit
     * @return
     */
    List<UserDO> list(@Param("start") int start, @Param("limit") int limit);

    /**
     * 总数
     *
     * @return
     */
    int count();
}
