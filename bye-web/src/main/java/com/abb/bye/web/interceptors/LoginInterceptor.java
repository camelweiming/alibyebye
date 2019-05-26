package com.abb.bye.web.interceptors;

import com.abb.bye.client.domain.UserDTO;
import com.abb.bye.client.service.UserService;
import com.abb.bye.utils.LoginUtil;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String loginUserName = LoginUtil.getLoginUserName(request);
        if (loginUserName == null) {
            response.sendRedirect("/login.htm");
            return false;
        }
        UserDTO userDTO = userService.getByName(loginUserName).getData();
        if (userDTO == null) {
            response.sendRedirect("/login.htm");
            return false;
        }
        request.setAttribute("loginUser", userDTO);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        UserDTO userDTO = (UserDTO)request.getAttribute("loginUser");
        if (userDTO == null) {
            response.sendRedirect("/login.htm");
            return;
        }
        if (modelAndView != null) {
            modelAndView.addObject("loginUser", userDTO);
        }
    }
}
