package com.abb.flowable.service;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.flowable.domain.ComponentForm;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public interface Form {
    /**
     * 准备表单元素，在准备提交表单时使用
     *
     * @param request
     * @return
     */
    ResultDTO<ComponentForm> render(HttpServletRequest request);

    /**
     * 在处理之后预览时使用
     *
     * @param variables
     * @return
     */
    ResultDTO<ComponentForm> render(Map<String, Object> variables);

    /**
     * 处理表单请求
     *
     * @param request
     * @return
     */
    ResultDTO<Object> post(HttpServletRequest request);

}
