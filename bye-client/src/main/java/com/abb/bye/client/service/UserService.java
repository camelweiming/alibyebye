package com.abb.bye.client.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserDTO;

import java.util.List;

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
     * 批量取
     *
     * @param ids
     * @return
     */
    ResultDTO<List<UserDTO>> mGet(List<Long> ids);

    /**
     * 通过用户查询
     *
     * @param name
     * @return
     */
    ResultDTO<UserDTO> getByName(String name);

    /**
     * 列表
     *
     * @param start
     * @param limit
     * @param needTotal
     * @return
     */
    ResultDTO<List<UserDTO>> list(int start, int limit, boolean needTotal);
}
