package com.abb.bye.utils.http;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
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
    private String proxyUserName;
    private String proxyPassword;
    private boolean disableKeepAlive = false;
    private boolean enableCompress = true;
    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy;
    private ConnectionReuseStrategy connectionReuseStrategy;
    private static ConnectionReuseStrategy DISABLE_KEEP_ALIVE_STRATEGY = (response, context) -> false;

    public Closeable build() {
        return async ? buildAsyncHttpClient() : buildSyncHttpClient();
    }

    public CloseableHttpAsyncClient buildAsyncHttpClient() {
        try {
            CredentialsProvider credentialsProvider = null;
            if (proxyUserName != null) {
                credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUserName, proxyPassword));
            }
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustAllStrategy()).build();
            SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(sslcontext, supportedProtocols, null, SSLIOSessionStrategy.getDefaultHostnameVerifier());
            HttpAsyncClientBuilder builder = HttpAsyncClients.custom()
                .setRedirectStrategy(redirectStrategy)
                .setDefaultCredentialsProvider(credentialsProvider)
                .setConnectionReuseStrategy(disableKeepAlive ? DISABLE_KEEP_ALIVE_STRATEGY : connectionReuseStrategy)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .build())
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(totalConnPerRoute)
                .setSSLStrategy(sslSessionStrategy);
            if (enableCompress) {
                builder.addInterceptorFirst(new RequestAcceptEncoding());
                builder.addInterceptorFirst(new ResponseContentEncoding());
            }
            CloseableHttpAsyncClient closeableHttpAsyncClient = builder.build();
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
            CredentialsProvider credentialsProvider = null;
            if (proxyUserName != null) {
                credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUserName, proxyPassword));
            }
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustAllStrategy()).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, supportedProtocols, null, NoopHostnameVerifier.INSTANCE);
            HttpClientBuilder builder = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setRedirectStrategy(redirectStrategy)
                .setConnectionReuseStrategy(disableKeepAlive ? DISABLE_KEEP_ALIVE_STRATEGY : connectionReuseStrategy)
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .build())
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(totalConnPerRoute)
                .setSSLSocketFactory(sslConnectionSocketFactory);
            if (enableCompress) {
                builder.addInterceptorFirst(new RequestAcceptEncoding());
                builder.addInterceptorLast(new ResponseContentEncoding());
            }
            CloseableHttpClient closeableHttpAsyncClient = builder.build();
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

    public String getProxyUserName() {
        return proxyUserName;
    }

    public SimpleHttpBuilder setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
        return this;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public SimpleHttpBuilder setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
        return this;
    }

    public ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
        return connectionKeepAliveStrategy;
    }

    public SimpleHttpBuilder setConnectionKeepAliveStrategy(ConnectionKeepAliveStrategy connectionKeepAliveStrategy) {
        this.connectionKeepAliveStrategy = connectionKeepAliveStrategy;
        return this;
    }

    public ConnectionReuseStrategy getConnectionReuseStrategy() {
        return connectionReuseStrategy;
    }

    public SimpleHttpBuilder setConnectionReuseStrategy(ConnectionReuseStrategy connectionReuseStrategy) {
        this.connectionReuseStrategy = connectionReuseStrategy;
        return this;
    }

    public boolean isDisableKeepAlive() {
        return disableKeepAlive;
    }

    public SimpleHttpBuilder setDisableKeepAlive(boolean disableKeepAlive) {
        this.disableKeepAlive = disableKeepAlive;
        return this;
    }

    public boolean isEnableCompress() {
        return enableCompress;
    }

    public SimpleHttpBuilder setEnableCompress(boolean enableCompress) {
        this.enableCompress = enableCompress;
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
