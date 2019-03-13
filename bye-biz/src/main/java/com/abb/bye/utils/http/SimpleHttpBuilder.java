package com.abb.bye.utils.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.net.URI;

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
public class SimpleHttpBuilder {
    private int socketTimeout = 5000;
    private int connectionTimeout = 5000;
    private int connectionRequestTimeout = 5000;
    private int maxConnPerRoute = 50;
    private int totalConnPerRoute = 100;
    private boolean autoStart = true;
    private RedirectStrategy redirectStrategy;
    private String[] supportedProtocols = new String[] {"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};
    private boolean async = true;

    public Closeable build() {
        return async ? buildAsyncHttpClient() : buildSyncHttpClient();
    }

    public CloseableHttpAsyncClient buildAsyncHttpClient() {
        try {
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustAllStrategy()).build();
            SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(sslcontext, supportedProtocols, null, SSLIOSessionStrategy.getDefaultHostnameVerifier());
            CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
                .setRedirectStrategy(redirectStrategy)
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .build())
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(totalConnPerRoute)
                .setSSLStrategy(sslSessionStrategy)
                .build();
            if (autoStart) {
                closeableHttpAsyncClient.start();
            }
            return closeableHttpAsyncClient;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CloseableHttpClient buildSyncHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustAllStrategy()).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, supportedProtocols, null, NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient closeableHttpAsyncClient = HttpClients.custom()
                .setRedirectStrategy(redirectStrategy)
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .build())
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(totalConnPerRoute)
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
            return closeableHttpAsyncClient;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleHttpBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public SimpleHttpBuilder setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public SimpleHttpBuilder setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public SimpleHttpBuilder setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    public SimpleHttpBuilder setTotalConnPerRoute(int totalConnPerRoute) {
        this.totalConnPerRoute = totalConnPerRoute;
        return this;
    }

    public SimpleHttpBuilder setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
        return this;
    }

    public SimpleHttpBuilder setSupportedCipherSuites(String[] supportedCipherSuites) {
        this.supportedProtocols = supportedProtocols;
        return this;
    }

    public SimpleHttpBuilder setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public SimpleHttpBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
        return this;
    }

    public static class CustomRedirectStrategy extends LaxRedirectStrategy {
        private static Logger logger = LoggerFactory.getLogger(CustomRedirectStrategy.class);

        @Override
        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            URI uri = getLocationURI(request, response, context);
            String method = request.getRequestLine().getMethod();
            if ("post".equalsIgnoreCase(method)) {
                try {
                    HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper)request;
                    httpRequestWrapper.setURI(uri);
                    httpRequestWrapper.removeHeaders("Content-Length");
                    return httpRequestWrapper;
                } catch (Exception e) {
                    logger.error("", e);
                }
                return new HttpPost(uri);
            } else {
                return new HttpGet(uri);
            }
        }
    }

}
