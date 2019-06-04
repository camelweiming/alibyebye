package com.abb.bye.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserAuthorityDO;
import com.abb.bye.client.domain.UserAuthorityDTO;
import com.abb.bye.client.domain.UserDO;
import com.abb.bye.client.service.UserAuthorityService;
import com.abb.bye.mapper.UserAuthorityMapper;
import com.abb.bye.mapper.UserMapper;
import com.abb.bye.utils.Md5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
@Service("userAuthorityService")
public class UserAuthorityServiceImpl implements UserAuthorityService {
    private static Logger logger = LoggerFactory.getLogger(UserAuthorityServiceImpl.class);
    @Resource
    private Sequence sequence;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAuthorityMapper userAuthorityMapper;
    @Resource
    private PlatformTransactionManager transactionManager;

    @Override
    public ResultDTO<Long> register(UserAuthorityDTO userAuthorityDTO) {
        try {
            Long userId = userMapper.getByName(userAuthorityDTO.getName());
            if (userId != null) {
                return ResultDTO.buildError(ResultDTO.ERROR_CODE_DUP_USER_ERROR, "dup user");
            }
            UserDO user = new UserDO();
            user.setName(userAuthorityDTO.getName());
            user.setId(sequence.next("user"));
            UserAuthorityDO userAuthority = new UserAuthorityDO();
            userAuthority.setUserId(user.getId());
            userAuthority.setSalt(getSalt());
            userAuthority.setPassword(Md5.getInstance().getMD5String(userAuthorityDTO.getPassword() + userAuthority.getSalt()));
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            ResultDTO<Long> result = transactionTemplate.execute(transactionStatus -> {
                try {
                    userMapper.insert(user);
                    userAuthorityMapper.insert(userAuthority);
                    return ResultDTO.buildSuccess(user.getId());
                } catch (Throwable e) {
                    transactionStatus.setRollbackOnly();
                    logger.error("Error register user:" + user.getName());
                    return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
                }
            });
            return result;
        } catch (Throwable e) {
            logger.error("Error register user:" + userAuthorityDTO.getName(), e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<Long> verify(UserAuthorityDTO userAuthorityDTO) {
        try {
            Long userId = userMapper.getByName(userAuthorityDTO.getName());
            if (userId == null) {
                return ResultDTO.buildError(ResultDTO.ERROR_CODE_USER_NOT_FOUND, "user not found");
            }
            String salt = userAuthorityMapper.getSalt(userId);
            String password = Md5.getInstance().getMD5String(userAuthorityDTO.getPassword() + salt);
            userId = userAuthorityMapper.verify(userId, password);
            if (userId == null) {
                return ResultDTO.buildError(ResultDTO.ERROR_CODE_USER_VALIDATE, "password error");
            }
            return ResultDTO.buildSuccess(userId);
        } catch (Throwable e) {
            logger.error("Error verify user:" + userAuthorityDTO.getName(), e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    private String getSalt() {
        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return new BASE64Encoder().encode(salt);
    }
}
