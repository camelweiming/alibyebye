package com.abb.bye.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserRelationDO;
import com.abb.bye.client.domain.enums.UserRelationType;
import com.abb.bye.client.service.UserRelationService;
import com.abb.bye.mapper.UserRelationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/31
 */
@Service("userRelationService")
public class UserRelationServiceImpl implements UserRelationService {
    private static final Logger logger = LoggerFactory.getLogger(UserRelationServiceImpl.class);
    @Resource
    private UserRelationMapper userRelationMapper;
    @Resource
    private Sequence sequence;
    private static final String sequenceName = "user_relation";

    @Override
    public ResultDTO<Void> insertOrUpdate(UserRelationDO userRelationDO) {
        try {
            userRelationDO.setId(sequence.next(sequenceName));
            userRelationMapper.insertOrUpdate(userRelationDO);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            logger.error("Error insertOrUpdate:" + userRelationDO, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<List<UserRelationDO>> getByRelationId(UserRelationType userRelationType, long refId) {
        try {
            List<UserRelationDO> list = userRelationMapper.getByRelationId(userRelationType.getType(), refId);
            return ResultDTO.buildSuccess(list);
        } catch (Throwable e) {
            logger.error("Error getByRelationId:" + userRelationType, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<List<UserRelationDO>> getByUserId(UserRelationType userRelationType, long userId) {
        try {
            List<UserRelationDO> list = userRelationMapper.getByUserId(userRelationType.getType(), userId);
            return ResultDTO.buildSuccess(list);
        } catch (Throwable e) {
            logger.error("Error getByUserId:" + userRelationType, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<List<UserRelationDO>> mGetByUserIds(UserRelationType userRelationType, List<Long> userIds) {
        try {
            List<UserRelationDO> list = userRelationMapper.mGetByUserIds(userRelationType.getType(), userIds);
            return ResultDTO.buildSuccess(list);
        } catch (Throwable e) {
            logger.error("Error mGetByUserIds:" + userRelationType, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<Void> removeByIds(List<Long> ids) {
        try {
            userRelationMapper.removeByIds(ids);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            logger.error("Error removeByIds:" + ids, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<Void> remove(UserRelationType userRelationType, long userId, long refId) {
        try {
            userRelationMapper.remove(userRelationType.getType(), userId, refId);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            logger.error(String.format("Error remove type:%s userId:%s refId:%s", userRelationType, userId, refId), e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }
}
