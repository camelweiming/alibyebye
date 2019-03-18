package com.abb.bye.utils.http;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import us.codecraft.webmagic.utils.UrlUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author cenpeng.lwm
 * @since 2019/3/18
 */
public class SimpleHttp {
    private Closeable httpClient;

    public SimpleHttp(Closeable httpClient) {
        this.httpClient = httpClient;
    }

    public String get(String url) throws Exception {
        return get(url, null);
    }

    public int touch(String url, ReqConfig reqConfig) throws Exception {
        HttpResponse response = null;
        try {
            response = getResponse(url, reqConfig);
            return response.getStatusLine().getStatusCode();
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
    }

    public String get(String url, ReqConfig reqConfig) throws Exception {
        HttpResponse response = null;
        try {
            response = getResponse(url, reqConfig);
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

    public HttpResponse getResponse(String url, ReqConfig reqConfig) throws Exception {
        Callback<HttpResponse> callback = (response, httpRequestBase) -> response;
        return execute(url, reqConfig, callback);
    }

    public <T> T execute(String url, ReqConfig reqConfig, Callback<T> callback) throws Exception {
        final HttpGet request = new HttpGet(url);
        HttpClientContext context = new HttpClientContext();
        if (reqConfig != null) {
            RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectionRequestTimeout(reqConfig.getConnectionRequestTimeout())
                .setConnectTimeout(reqConfig.getConnectionTimeout())
                .setSocketTimeout(reqConfig.getSocketTimeout());
            if (reqConfig.getProxy() != null) {
                builder.setProxy(reqConfig.getProxy());
                if (reqConfig.getProxyUserName() != null) {
                    AuthState authState = new AuthState();
                    authState.update(new BasicScheme(ChallengeState.PROXY), new UsernamePasswordCredentials(reqConfig.getProxyUserName(), reqConfig.getProxyPassword()));
                    context.setAttribute(HttpClientContext.PROXY_AUTH_STATE, authState);
                }
            }
            if (reqConfig.getHeaders() != null) {
                reqConfig.getHeaders().forEach(request::setHeader);
            }
            if (reqConfig.getCookies() != null) {
                CookieStore cookieStore = new BasicCookieStore();
                for (Map.Entry<String, String> cookieEntry : reqConfig.getCookies().entrySet()) {
                    BasicClientCookie cookie1 = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                    cookie1.setDomain(UrlUtils.removePort(UrlUtils.getDomain(url)));
                    cookieStore.addCookie(cookie1);
                }
                context.setCookieStore(cookieStore);
            }
            request.setConfig(builder.build());
        }
        if (httpClient instanceof CloseableHttpClient) {
            CloseableHttpClient client = (CloseableHttpClient)httpClient;
            return callback.callback(client.execute(request, context), request);
        } else if (httpClient instanceof CloseableHttpAsyncClient) {
            CloseableHttpAsyncClient client = (CloseableHttpAsyncClient)httpClient;
            Future<HttpResponse> future = client.execute(request, context, null);
            return callback.callback(future.get(), request);
        }
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        httpClient.close();
    }

    public static void main(String[] args) throws Exception {
        SimpleHttp simpleHttp = new SimpleHttpBuilder().buildSimple();
        String content = simpleHttp.get("https://baidu.com");
        System.out.println(content);
    }
}
