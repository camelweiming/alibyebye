package com.abb.bye.web;

import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.exception.AuthException;
import com.abb.bye.client.service.UserService;
import com.abb.bye.utils.LoginUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
public abstract class BaseController {
    @Resource
    private UserService userService;

    protected UserDTO getLoginUser(HttpServletRequest request) {
        try {
            String userName = LoginUtil.getLoginUserName(request);
            if (userName == null) {
                throw new AuthException("user not login");
            }
            UserDTO userDTO = userService.getByName(userName).getData();
            if (userDTO == null) {
                throw new AuthException("user not exist");
            }
            return userDTO;
        } catch (Throwable e) {
            throw new AuthException(e);
        }
    }
}
