package com.abb.bye.web.interceptors;

import com.abb.bye.utils.CommonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cenpeng.lwm
 * @since 2019/6/8
 */
@Component("toolsInterceptor")
public class ToolsInterceptor extends HandlerInterceptorAdapter {
    private static CommonUtils commonUtils = new CommonUtils();

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView view) {
        if (view != null) {
            view.addObject("common", commonUtils);
        }
    }
}
