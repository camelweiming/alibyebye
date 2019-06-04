package com.abb.bye.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserDO;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;
import com.abb.bye.client.domain.enums.UserRelationType;
import com.abb.bye.client.service.SimpleCache;
import com.abb.bye.client.service.UserRelationService;
import com.abb.bye.client.service.UserService;
import com.abb.bye.mapper.UserMapper;
import com.abb.bye.utils.Converter;
import com.abb.bye.utils.Switcher;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRelationService userRelationService;
    @Resource
    private SimpleCache simpleCache;

    @Override
    public ResultDTO<UserDTO> getById(long id, UserOptions options) {
        try {
            UserDTO userDTO = (UserDTO)simpleCache.get(Switcher.getUserCacheKeyPrefix() + id);
            if (userDTO == null) {
                UserDO userDO = userMapper.getById(id);
                if (userDO == null) {
                    return ResultDTO.buildSuccess(null);
                }
                userDTO = Converter.convert(userDO);
                logger.trace("getById from db");
                simpleCache.put(Switcher.getUserCacheKeyPrefix() + id, userDTO, Switcher.getUserCacheExpiredSeconds());
            }
            withExtends(userDTO, options);
            return ResultDTO.buildSuccess(userDTO);
        } catch (Throwable e) {
            logger.error("Error getById:" + id, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<List<UserDTO>> mGet(List<Long> ids, UserOptions options) {
        List<UserDTO> userDTOS = new ArrayList<>();
        ids.forEach(id -> userDTOS.add(getById(id, options).getData()));
        return ResultDTO.buildSuccess(userDTOS);
    }

    @Override
    public ResultDTO<UserDTO> getByName(String name, UserOptions options) {
        try {
            Long userId = (Long)simpleCache.get(Switcher.getUserNameCacheKeyPrefix() + name);
            if (userId == null) {
                userId = userMapper.getByName(name);
                if (userId == null) {
                    return ResultDTO.buildSuccess(null);
                }
                logger.trace("getByName from db:" + name);
                simpleCache.put(Switcher.getUserNameCacheKeyPrefix() + name, userId, Switcher.getUserNameCacheExpiredSeconds());
            }
            return getById(userId, options);
        } catch (Throwable e) {
            logger.error("Error getByName:" + name, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<List<UserDTO>> list(int start, int limit, boolean needTotal) {
        try {
            int total = 0;
            List<UserDO> userDOS = userMapper.list(start, limit);
            if (needTotal) {
                total = userMapper.count();
            }
            return ResultDTO.buildSuccess(Converter.convert(userDOS), total);
        } catch (Throwable e) {
            logger.error("Error list", e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    private void withExtends(UserDTO userDTO, UserOptions options) {
        if (options.isWithBoss() || options.isWithAllBosses()) {
            withBoss(userDTO, options.isWithAllBosses());
        } else {
            userDTO.setBosses(null);
        }
    }

    private void withBoss(UserDTO userDTO, boolean all) {
        List<Long> bossIds = userRelationService.getByUserId(UserRelationType.PARENT, userDTO.getUserId()).getData().stream().map(userRelationDO -> userRelationDO.getRefId()).collect(
            Collectors.toList());
        if (CollectionUtils.isNotEmpty(bossIds)) {
            List<UserDTO> userDTOS = mGet(bossIds, new UserOptions().setWithBoss(all)).getData();
            userDTO.setBosses(userDTOS);
        }
    }
}
