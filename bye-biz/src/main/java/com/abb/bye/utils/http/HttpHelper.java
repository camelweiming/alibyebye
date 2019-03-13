package com.abb.bye.utils.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
public class HttpHelper {

    public static String get(Closeable httpAsyncClient, String url) throws Exception {
        return get(httpAsyncClient, url, null);
    }

    public static int touch(Closeable httpAsyncClient, String url, ReqConfig reqConfig) throws Exception {
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

    public static String get(Closeable httpAsyncClient, String url, ReqConfig reqConfig) throws Exception {
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

    public static HttpResponse getResponse(Closeable httpAsyncClient, String url, ReqConfig reqConfig) throws Exception {
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
        if (httpAsyncClient instanceof CloseableHttpClient) {
            CloseableHttpClient client = (CloseableHttpClient)httpAsyncClient;
            return client.execute(request);
        } else if (httpAsyncClient instanceof CloseableHttpAsyncClient) {
            CloseableHttpAsyncClient client = (CloseableHttpAsyncClient)httpAsyncClient;
            Future<HttpResponse> future = client.execute(request, null);
            return future.get();
        }
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws Exception {
        HttpHost httpHost = new HttpHost("112.16.154.190", 808);
        Closeable closeableHttpClient = new SimpleHttpBuilder().setAsync(true).setConnectionTimeout(20000).setConnectionRequestTimeout(20000).setSocketTimeout(20000).buildSyncHttpClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 7 Build/MOB30X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        HttpResponse content = HttpHelper.getResponse(closeableHttpClient, "https://douban.com", new ReqConfig().setHeaders(headers).setProxy(httpHost));
        System.out.println(content.getStatusLine().getStatusCode());
        System.out.println(EntityUtils.toString(content.getEntity(), "UTF-8"));
        closeableHttpClient.close();
    }
}
