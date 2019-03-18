package com.abb.bye.utils.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * @author cenpeng.lwm
 * @since 2019/3/18
 */
public interface Callback<T> {
    /**
     * 回调
     *
     * @param response
     * @param httpRequestBase
     * @return t
     */
    T callback(HttpResponse response, HttpRequestBase httpRequestBase);
}
