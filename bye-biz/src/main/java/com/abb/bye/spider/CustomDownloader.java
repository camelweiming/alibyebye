package com.abb.bye.spider;

import com.abb.bye.client.domain.ProxyDO;
import com.abb.bye.client.service.ProxyService;
import com.abb.bye.utils.LocalLimiter;
import com.abb.bye.utils.SpiderHelper;
import com.abb.bye.utils.http.HttpHelper;
import com.abb.bye.utils.http.ReqConfig;
import com.abb.bye.utils.http.SimpleHttpBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
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

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
@Service("customDownloader")
public class CustomDownloader extends AbstractDownloader {
    private static final Logger logger = LoggerFactory.getLogger(CustomDownloader.class);
    private Closeable httpClient = new SimpleHttpBuilder()
        .setProxyUserName("MRCAMELFCF3LO8P0")
        .setProxyPassword("wPfm8o9d")
        .setConnectionKeepAliveStrategy(new SimpleHttpBuilder.DisableConnectionKeepAliveStrategy())
        .build();
    @Resource
    private ProxyService proxyService;
    private int thread;
    private LocalLimiter localLimiter = new LocalLimiter();
    private boolean responseHeader = true;

    @Override
    public Page download(Request request, Task task) {
        Page page = Page.fail();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", task.getSite().getUserAgent());
        ProxyDO proxy = proxyService.get();
        HttpResponse response = null;
        try {
            localLimiter.add(thread);
            ReqConfig reqConfig = new ReqConfig()
                .setHeaders(headers)
                .setCharset(request.getCharset())
                .setConnectionTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000);
            if (proxy != null) {
                reqConfig.setProxy(SpiderHelper.create(proxy)).setProxyUserName(proxy.getUserName()).setProxyPassword(proxy.getPassword());
            }
            response = HttpHelper.getResponse(httpClient, request.getUrl(), reqConfig);
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), response, task);
            return page;
        } catch (Throwable e) {
            logger.warn("Error download (" + proxy + ") url:" + request.getUrl(), e);
        } finally {
            localLimiter.release();
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
        return page;
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
