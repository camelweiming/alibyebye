package com.abb.bye.client.flow;

import com.abb.bye.client.domain.ResultDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/12
 */
public interface Form {
    /**
     * 通过页面参数渲染
     *
     * @param request
     * @return
     */
    ResultDTO<Object> render(HttpServletRequest request);

    /**
     * 通过变量渲染
     *
     * @param variables
     * @return
     */
    ResultDTO<Object> render(Map<String, Object> variables);

    /**
     * 处理表单请求
     *
     * @param request
     * @return
     */
    ResultDTO<Object> post(HttpServletRequest request);
}
