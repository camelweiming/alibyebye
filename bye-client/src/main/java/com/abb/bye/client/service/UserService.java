package com.abb.bye.client.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public interface UserService {
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    ResultDTO<UserDTO> getById(long id);

    /**
     * 通过用户查询
     *
     * @param name
     * @return
     */
    ResultDTO<UserDTO> getByName(String name);
}
