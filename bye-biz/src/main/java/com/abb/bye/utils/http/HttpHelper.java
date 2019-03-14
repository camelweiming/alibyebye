package com.abb.bye.utils.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import us.codecraft.webmagic.utils.UrlUtils;

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
        Callback<HttpResponse> callback = (response, httpRequestBase) -> response;
        return execute(httpAsyncClient, url, reqConfig, callback);
    }

    public static <T> T execute(Closeable httpAsyncClient, String url, ReqConfig reqConfig, Callback<T> callback) throws Exception {
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
        HttpClientContext context = new HttpClientContext();
        if (reqConfig.getCookies() != null) {
            CookieStore cookieStore = new BasicCookieStore();
            for (Map.Entry<String, String> cookieEntry : reqConfig.getCookies().entrySet()) {
                BasicClientCookie cookie1 = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie1.setDomain(UrlUtils.removePort(UrlUtils.getDomain(url)));
                cookieStore.addCookie(cookie1);
            }
            context.setCookieStore(cookieStore);
        }
        if (reqConfig.getProxy() != null && reqConfig.getProxyUserName() != null) {
            AuthState authState = new AuthState();
            authState.update(new BasicScheme(ChallengeState.PROXY), new UsernamePasswordCredentials(reqConfig.getProxyUserName(), reqConfig.getProxyPassword()));
            context.setAttribute(HttpClientContext.PROXY_AUTH_STATE, authState);
        }
        if (httpAsyncClient instanceof CloseableHttpClient) {
            CloseableHttpClient client = (CloseableHttpClient)httpAsyncClient;
            return callback.callback(client.execute(request, context), request);
        } else if (httpAsyncClient instanceof CloseableHttpAsyncClient) {
            CloseableHttpAsyncClient client = (CloseableHttpAsyncClient)httpAsyncClient;
            Future<HttpResponse> future = client.execute(request, context, null);
            return callback.callback(future.get(), request);
        }
        throw new UnsupportedOperationException();
    }

    public interface Callback<T> {
        /**
         * 回调
         *
         * @param response
         * @param httpRequestBase
         */
        T callback(HttpResponse response, HttpRequestBase httpRequestBase);
    }

    public static class Res {
        private final HttpResponse response;
        private final HttpRequestBase httpRequestBase;

        public Res(HttpResponse response, HttpRequestBase httpRequestBase) {
            this.response = response;
            this.httpRequestBase = httpRequestBase;
        }

        public HttpResponse getResponse() {
            return response;
        }

        public HttpRequestBase getHttpRequestBase() {
            return httpRequestBase;
        }
    }

    public static void main(String[] args) throws Exception {
        HttpHost httpHost = null;//new HttpHost("http-proxy-t1.dobel.cn", 9180);
        Closeable closeableHttpClient = new SimpleHttpBuilder().setAsync(true).setConnectionTimeout(20000)
            .setConnectionRequestTimeout(20000).setSocketTimeout(20000).buildSyncHttpClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 7 Build/MOB30X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        long t = System.currentTimeMillis();
        while (true) {
            Callback<Res> callback = (response, httpRequestBase) -> new Res(response, httpRequestBase);
            Res res = HttpHelper.execute(closeableHttpClient, "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,10&tags=%E7%94%B5%E5%BD%B1&start=720", new ReqConfig().setHeaders(headers)
                .setProxy(httpHost).setProxyUserName("MRCAMELFCF3LO8P0").setProxyPassword("wPfm8o9d"), callback);
            System.out.println(res.getResponse().getStatusLine().getStatusCode());
            System.out.println(EntityUtils.toString(res.getResponse().getEntity(), "UTF-8"));
            break;
            //System.out.println(System.currentTimeMillis() - t);
            //res.getHttpRequestBase().releaseConnection();
            //Thread.sleep(10);
        }
        closeableHttpClient.close();
    }
}
