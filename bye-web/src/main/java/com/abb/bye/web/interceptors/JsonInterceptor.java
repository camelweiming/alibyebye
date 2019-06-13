package com.abb.bye.web.interceptors;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 线上不要用这个东西，无用属性太多
 *
 * @author cenpeng.lwm
 * @since 2019/5/26
 */

@Component
public class JsonInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (StringUtils.equals("json", request.getParameter("_format_"))) {
            modelAndView.setView(new JsonView());
        }

    }

    public static class JsonView extends AbstractView {

        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.setHeader("Content-Type", "application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(model));
        }
    }
}
