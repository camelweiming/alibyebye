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

    @Override
    public ResultDTO<Void> insertOrUpdate(UserRelationDO userRelationDO) {
        try {
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
}
