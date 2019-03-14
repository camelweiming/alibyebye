package com.abb.bye.spider;

import com.abb.bye.client.domain.ProxyDO;
import com.abb.bye.client.service.ProxyService;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.LocalLimiter;
import com.abb.bye.utils.SpiderHelper;
import com.abb.bye.utils.UserAgents;
import com.abb.bye.utils.http.HttpHelper;
import com.abb.bye.utils.http.ReqConfig;
import com.abb.bye.utils.http.SimpleHttpBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.CharsetUtils;
import us.codecraft.webmagic.utils.HttpClientUtils;

import javax.annotation.Resource;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
@Service("customDownloader")
public class CustomDownloader extends AbstractDownloader {
    private static final Logger logger = LoggerFactory.getLogger(CustomDownloader.class);
    private Closeable httpClient = new SimpleHttpBuilder().setConnectionKeepAliveStrategy(new SimpleHttpBuilder.DisableConnectionKeepAliveStrategy()).build();
    @Resource
    private ProxyService proxyService;
    private int thread;
    private LocalLimiter localLimiter = new LocalLimiter();
    private boolean responseHeader = true;
    private Map<String, AtomicInteger> proxyMapping = new ConcurrentHashMap<>();
    private AtomicInteger error403 = new AtomicInteger();

    @Override
    public Page download(Request request, Task task) {
        Page page = Page.fail();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", UserAgents.get().getOne());
        headers.put("Connection", "close");
        headers.put("Referer", "https://baidu.com");
        Map<String, String> cookies = new HashMap<>();
        cookies.put("__utmt", "1");
        cookies.put("__utma", "30149281.495950855.1552547558.1552547558.1552547558.1");
        cookies.put("__utmb", "30149281.4.10.1552547558");
        cookies.put("__utmc", "30149281");
        cookies.put("__utmz", "30149281.1552547558.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
        ProxyDO proxy = proxyService.get();
        switchIP(proxy);
        HttpHelper.Res res = null;
        try {
            localLimiter.add(thread);
            ReqConfig reqConfig = new ReqConfig()
                .setHeaders(headers)
                .setCookies(cookies)
                .setCharset(request.getCharset())
                .setConnectionTimeout(task.getSite().getTimeOut())
                .setConnectionRequestTimeout(task.getSite().getTimeOut())
                .setSocketTimeout(task.getSite().getTimeOut());
            if (proxy != null) {
                reqConfig
                    .setProxy(SpiderHelper.create(proxy))
                    .setProxyUserName(proxy.getUserName())
                    .setProxyPassword(proxy.getPassword());
            }
            HttpHelper.Callback<HttpHelper.Res> callback = (response, httpRequestBase) -> new HttpHelper.Res(response, httpRequestBase);
            res = HttpHelper.execute(httpClient, request.getUrl(), reqConfig, callback);
            if (res.getResponse().getStatusLine().getStatusCode() == 403) {
                if (error403.incrementAndGet() > 10) {
                    switchHttpClient();
                }
            }
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), res.getResponse(), task);
            return page;
        } catch (Throwable e) {
            logger.warn("Error download (" + proxy + ") url:" + request.getUrl(), e);
        } finally {
            localLimiter.release();
            if (res != null) {
                if (res.getResponse() != null) {
                    EntityUtils.consumeQuietly(res.getResponse().getEntity());
                }
                if (res.getHttpRequestBase() != null && proxy != null) {
                    res.getHttpRequestBase().releaseConnection();
                }
            }
        }
        return page;
    }

    private void switchIP(ProxyDO proxy) {
        AtomicInteger c = proxyMapping.get(proxy.getHost());
        if (c == null) {
            c = new AtomicInteger(1);
            proxyMapping.put(proxy.getHost(), c);
        }
        Map<String, Object> config = CommonUtils.asMap(proxy.getAttributes());
        Integer reqCount = (Integer)config.get(ProxyDO.ATTR_SWITCH_IP_REQ_COUNT);
        String switchUrl = (String)config.get(ProxyDO.ATTR_SWITCH_IP_URL);
        if (reqCount != null && switchUrl != null && c.incrementAndGet() % reqCount == 0) {
            ReqConfig reqConfig = new ReqConfig().setProxy(SpiderHelper.create(proxy)).setProxyUserName(proxy.getUserName()).setProxyPassword(proxy.getPassword());
            try {
                String content = HttpHelper.get(httpClient, switchUrl, reqConfig);
                logger.info("switch-ip:" + content);
            } catch (Exception e) {
                logger.warn("switch-ip failed", e);
            }
        }
    }

    private synchronized void switchHttpClient() {
        Closeable _httpClient = new SimpleHttpBuilder().setConnectionKeepAliveStrategy(new SimpleHttpBuilder.DisableConnectionKeepAliveStrategy()).build();
        Closeable old = httpClient;
        httpClient = _httpClient;
        logger.warn("httpClientSwitch.....................");
        try {
            old.close();
        } catch (IOException e) {
            logger.warn("client-close-error");
        }
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        String contentType = httpResponse.getEntity().getContentType() == null ? "" : httpResponse.getEntity().getContentType().getValue();
        Page page = new Page();
        page.setBytes(bytes);
        if (!request.isBinaryContent()) {
            if (charset == null) {
                charset = getHtmlCharset(contentType, bytes);
            }
            page.setCharset(charset);
            page.setRawText(new String(bytes, charset));
        }
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        page.setDownloadSuccess(true);
        if (responseHeader) {
            page.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        if (page.getStatusCode() != HttpStatus.SC_OK) {
            logger.info("page status code error:" + page.getUrl() + "-->" + page.getRawText());
        }
        return page;
    }

    private String getHtmlCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset = CharsetUtils.detectCharset(contentType, contentBytes);
        if (charset == null) {
            charset = Charset.defaultCharset().name();
            logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
        }
        return charset;
    }

    @Override
    public void setThread(int threadNum) {
        this.thread = threadNum;
    }
}
