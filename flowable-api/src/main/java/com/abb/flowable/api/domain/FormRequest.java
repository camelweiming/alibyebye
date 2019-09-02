package com.abb.flowable.api.domain;

import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/6/13
 */
public interface FormRequest {
    /**
     * 获得参数
     *
     * @param name
     * @return
     */
    String getParameter(String name);

    /**
     * 获得参数
     *
     * @param name
     * @return
     */
    String[] getParameterValues(String name);

    /**
     * 获得所有参数
     *
     * @return
     */
    Map<String, String[]> getParameterMap();

    /**
     * 获得上下文
     *
     * @return
     */
    Map<String, Object> getContext();

    /**
     * 设置上下文
     *
     * @param context
     */
    void setContext(Map<String, Object> context);

    /**
     * 添加变量
     *
     * @param name
     * @param value
     */
    void addContext(String name, Object value);

    /**
     * 获得变量
     *
     * @param contextName
     * @return
     */
    Object getContextValue(String contextName);

}
