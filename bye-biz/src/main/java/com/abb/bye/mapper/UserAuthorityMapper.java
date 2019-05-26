package com.abb.bye.mapper;

import com.abb.bye.client.domain.UserAuthorityDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public interface UserAuthorityMapper {
    /**
     * 插入用户
     *
     * @param userAuthorityDO
     */
    void insert(UserAuthorityDO userAuthorityDO);

    /**
     * 查盐
     *
     * @param userId
     * @return
     */
    String getSalt(long userId);

    /**
     * 校验，返回null校验不通过
     *
     * @param userId
     * @param password
     * @return
     */
    Long verify(@Param("userId") long userId, @Param("password") String password);
}
