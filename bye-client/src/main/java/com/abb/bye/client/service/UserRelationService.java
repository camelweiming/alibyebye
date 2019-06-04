package com.abb.bye.client.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserRelationDO;
import com.abb.bye.client.domain.enums.UserRelationType;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
public interface UserRelationService {
    /**
     * 插入
     *
     * @param userRelationDO
     * @return
     */
    ResultDTO<Void> insertOrUpdate(UserRelationDO userRelationDO);

    /**
     * 通过关联id查询
     *
     * @param userRelationType
     * @param refId
     * @return
     */
    ResultDTO<List<UserRelationDO>> getByRelationId(UserRelationType userRelationType, long refId);

    /**
     * 通过用户id查询
     *
     * @param userRelationType
     * @param userId
     * @return
     */
    ResultDTO<List<UserRelationDO>> getByUserId(UserRelationType userRelationType, long userId);

    /**
     * 批量获取
     *
     * @param userRelationType
     * @param userIds
     * @return
     */
    ResultDTO<List<UserRelationDO>> mGetByUserIds(UserRelationType userRelationType, List<Long> userIds);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    ResultDTO<Void> removeByIds(List<Long> ids);

    /**
     * 删除
     *
     * @param userRelationType
     * @param userId
     * @param refId
     * @return
     */
    ResultDTO<Void> remove(UserRelationType userRelationType, long userId, long refId);
}
