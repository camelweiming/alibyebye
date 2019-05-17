package com.abb.bye.utils.http;

import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * @author cenpeng.lwm
 * @since 2019/5/17
 */
public interface IHttpClient {
    /**
     * get请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    String get(String url) throws Exception;

    /**
     * 获取status
     *
     * @param url
     * @param reqConfig
     * @return
     * @throws Exception
     */
    int touch(String url, ReqConfig reqConfig) throws Exception;

    /**
     * get请求
     *
     * @param url
     * @param reqConfig
     * @return
     * @throws Exception
     */
    String get(String url, ReqConfig reqConfig) throws Exception;

    /**
     * 取response，需要手动关闭！！！
     *
     * @param url
     * @param reqConfig
     * @return
     * @throws Exception
     */
    HttpResponse getResponse(String url, ReqConfig reqConfig) throws Exception;

    /**
     * 执行
     *
     * @param url
     * @param reqConfig
     * @param callback
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T execute(String url, ReqConfig reqConfig, Callback<T> callback) throws Exception;

    /**
     * 关闭
     *
     * @throws IOException
     */
    void close() throws IOException;
}
