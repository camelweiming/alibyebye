package com.abb.bye.utils;

import com.abb.bye.client.domain.UserDO;
import com.abb.bye.client.domain.UserDTO;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class Converter {

    public static UserDTO convert(UserDO userDO) {
        UserDTO u = new UserDTO();
        u.setUserId(userDO.getId());
        u.setUserName(userDO.getName());
        return u;
    }
}
