package com.abb.bye.client.flow;

import com.abb.bye.client.domain.ResultDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public interface FlowForm {
    /**
     * 准备表单元素，在准备提交表单时使用
     *
     * @param request
     * @return
     */
    ResultDTO<FormObject> render(HttpServletRequest request);

    /**
     * 在处理之后预览时使用
     *
     * @param variables
     * @return
     */
    ResultDTO<FormObject> render(Map<String, Object> variables);

    /**
     * 处理表单请求
     *
     * @param request
     * @return
     */
    ResultDTO<Object> post(HttpServletRequest request);

}
