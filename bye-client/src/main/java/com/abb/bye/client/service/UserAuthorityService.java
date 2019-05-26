package com.abb.bye.client.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserAuthorityDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
public interface UserAuthorityService {
    /**
     * 注册
     *
     * @param userAuthorityDTO
     * @return
     */
    ResultDTO<Long> register(UserAuthorityDTO userAuthorityDTO);

    /**
     * 验证
     *
     * @param userAuthorityDTO
     * @return
     */
    ResultDTO<Long> verify(UserAuthorityDTO userAuthorityDTO);
}
