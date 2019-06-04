package com.abb.bye.mapper;

import com.abb.bye.client.domain.UserRelationDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public interface UserRelationMapper {
    /**
     * 插入
     *
     * @param userRelationDO
     */
    void insertOrUpdate(UserRelationDO userRelationDO);

    /**
     * 通过关联id查询
     *
     * @param userRelationType
     * @param refId
     * @return
     */
    List<UserRelationDO> getByRelationId(@Param("refType") int userRelationType, @Param("refId") long refId);

    /**
     * 通过用户id查询
     *
     * @param userRelationType
     * @param userId
     * @return
     */
    List<UserRelationDO> getByUserId(@Param("refType") int userRelationType, @Param("userId") long userId);

    /**
     * 通过关联id删除关系
     *
     * @param userRelationType
     * @param refId
     * @return
     */
    int removeByRelationId(@Param("refType") int userRelationType, @Param("refId") long refId);

    /**
     * 通过用户id删除关系
     *
     * @param userRelationType
     * @param userId
     * @return
     */
    int removeByUserId(@Param("refType") int userRelationType, @Param("userId") long userId);

    /**
     * 删除
     *
     * @param userRelationType
     * @param userId
     * @param refId
     * @return
     */
    int remove(@Param("refType") int userRelationType, @Param("userId") long userId, @Param("refId") long refId);

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    int removeByIds(@Param("ids") List<Long> ids);
}
