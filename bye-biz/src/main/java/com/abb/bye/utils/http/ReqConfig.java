package com.abb.bye.utils.http;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.http.HttpHost;

import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
public class ReqConfig {
    private int socketTimeout = 5000;
    private int connectionTimeout = 5000;
    private int connectionRequestTimeout = 5000;
    private String charset;
    private Map<String, String> headers;
    private HttpHost proxy;

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public ReqConfig setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public ReqConfig setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public ReqConfig setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public ReqConfig setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ReqConfig setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public ReqConfig setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
