package com.abb.bye.utils.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
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
        HttpContext context = new HttpClientContext();
        if (reqConfig.getProxy() != null && reqConfig.getProxyUserName() != null) {
            AuthState authState = new AuthState();
            authState.update(new BasicScheme(ChallengeState.PROXY), new UsernamePasswordCredentials(reqConfig.getProxyUserName(), reqConfig.getProxyPassword()));
            context.setAttribute(HttpClientContext.PROXY_AUTH_STATE, authState);
        }
        if (httpAsyncClient instanceof CloseableHttpClient) {
            CloseableHttpClient client = (CloseableHttpClient)httpAsyncClient;
            return client.execute(request, context);
        } else if (httpAsyncClient instanceof CloseableHttpAsyncClient) {
            CloseableHttpAsyncClient client = (CloseableHttpAsyncClient)httpAsyncClient;
            Future<HttpResponse> future = client.execute(request, context, null);
            return future.get();
        }
        throw new UnsupportedOperationException();
    }

    static class ProxyAuthenticator extends Authenticator {
        private String user, password;

        public ProxyAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password.toCharArray());
        }
    }

    public static void main(String[] args) throws Exception {
        HttpHost httpHost = new HttpHost("http-proxy-t1.dobel.cn", 9180);
        Closeable closeableHttpClient = new SimpleHttpBuilder().setAsync(false).setConnectionTimeout(20000)
            .setConnectionRequestTimeout(20000).setSocketTimeout(20000).buildSyncHttpClient();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 7 Build/MOB30X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        long t = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            HttpResponse content = HttpHelper.getResponse(closeableHttpClient, "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,10&tags=%E7%94%B5%E8%A7%86%E5%89%A7&start=1840",
                new ReqConfig().setHeaders(headers).setProxy(httpHost).setProxyUserName("MRCAMELFCF3LO8P0").setProxyPassword("wPfm8o9d"));
            System.out.println(content.getStatusLine().getStatusCode());
            System.out.println(EntityUtils.toString(content.getEntity(), "UTF-8"));
            System.out.println(System.currentTimeMillis() - t);
        }
        closeableHttpClient.close();
    }
}
