package com.abb.bye.utils.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.Future;

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
public class HttpHelper {

    public static String get(HttpAsyncClient httpAsyncClient, String url) throws Exception {
        return get(httpAsyncClient, url, null);
    }

    public static int touch(HttpAsyncClient httpAsyncClient, String url, ReqConfig reqConfig) throws Exception {
        HttpResponse response = null;
        try {
            response = getResponse(httpAsyncClient, url, reqConfig);
            return response.getStatusLine().getStatusCode();
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
    }

    public static String get(HttpAsyncClient httpAsyncClient, String url, ReqConfig reqConfig) throws Exception {
        HttpResponse response = null;
        try {
            response = getResponse(httpAsyncClient, url, reqConfig);
            if (response == null) {
                return null;
            }
            String charset = (reqConfig == null || reqConfig.getCharset() == null) ? "UTF-8" : reqConfig.getCharset();
            return EntityUtils.toString(response.getEntity(), charset);
        } catch (Exception e) {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            throw e;
        }
    }

    public static HttpResponse getResponse(HttpAsyncClient httpAsyncClient, String url, ReqConfig reqConfig) throws Exception {
        final HttpGet request = new HttpGet(url);
        if (reqConfig != null) {
            RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectionRequestTimeout(reqConfig.getConnectionRequestTimeout())
                .setConnectTimeout(reqConfig.getConnectionTimeout())
                .setSocketTimeout(reqConfig.getSocketTimeout());
            if (reqConfig.getProxy() != null) {
                builder.setProxy(reqConfig.getProxy());
            }
            request.setConfig(builder.build());
            if (reqConfig.getHeaders() != null) {
                reqConfig.getHeaders().forEach(request::setHeader);
            }
        }
        Future<HttpResponse> future = httpAsyncClient.execute(request, null);
        return future.get();
    }

    public static void main(String[] args) throws Exception {
        HttpHost httpHost = new HttpHost("http://113.200.214.164",9999);
        CloseableHttpAsyncClient closeableHttpAsyncClient = new SimpleHttpBuilder().build();
        String content = HttpHelper.get(closeableHttpAsyncClient, "https://blog.csdn.net/taozhexuan123/article/details/73739960",new ReqConfig().setProxy(httpHost));
        System.out.println(content);
        closeableHttpAsyncClient.close();
    }
}
