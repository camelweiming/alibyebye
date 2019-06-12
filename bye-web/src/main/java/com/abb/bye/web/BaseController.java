package com.abb.bye.web;

import com.abb.bye.client.exception.AuthException;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.LoginUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
public abstract class BaseController {

    protected Long getLoginUser(HttpServletRequest request) {
        try {
            return CommonUtils.toLong(LoginUtil.getLoginUser(request));
        } catch (Throwable e) {
            throw new AuthException(e);
        }
    }
}
