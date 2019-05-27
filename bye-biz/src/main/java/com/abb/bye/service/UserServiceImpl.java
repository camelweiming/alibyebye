package com.abb.bye.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserDO;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.service.UserService;
import com.abb.bye.mapper.UserMapper;
import com.abb.bye.utils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Resource
    private UserMapper userMapper;

    @Override
    public ResultDTO<UserDTO> getById(long id) {
        try {
            UserDO userDO = userMapper.getById(id);
            return ResultDTO.buildSuccess(Converter.convert(userDO));
        } catch (Throwable e) {
            logger.error("Error getById:" + id, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<UserDTO> getByName(String name) {
        try {
            UserDO userDO = userMapper.getByName(name);
            return ResultDTO.buildSuccess(Converter.convert(userDO));
        } catch (Throwable e) {
            logger.error("Error getByName:" + name, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }
}