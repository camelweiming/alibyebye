package com.abb.bye.client.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.domain.UserOptions;

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
     * @param options
     * @return
     */
    ResultDTO<UserDTO> getById(long id, UserOptions options);

    /**
     * 批量取
     *
     * @param options
     * @param ids
     * @return
     */
    ResultDTO<List<UserDTO>> mGet(List<Long> ids, UserOptions options);

    /**
     * 通过用户查询
     *
     * @param name
     * @param name
     * @return
     */
    ResultDTO<UserDTO> getByName(String name, UserOptions options);

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
