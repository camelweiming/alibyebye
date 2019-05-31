package com.abb.bye.utils;

import com.abb.bye.client.domain.UserDO;
import com.abb.bye.client.domain.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
public class Converter {

    public static List<UserDTO> convert(List<UserDO> list) {
        List<UserDTO> userDTOS = new ArrayList<>(list.size());
        list.forEach(l -> userDTOS.add(convert(l)));
        return userDTOS;
    }

    public static UserDTO convert(UserDO userDO) {
        UserDTO u = new UserDTO();
        u.setUserId(userDO.getId());
        u.setUserName(userDO.getName());
        return u;
    }
}
