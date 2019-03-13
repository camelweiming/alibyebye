package com.abb.bye.spider;

import com.abb.bye.utils.LocalLimiter;
import com.abb.bye.utils.http.HttpHelper;
import com.abb.bye.utils.http.PriorityProxyProvider;
import com.abb.bye.utils.http.ReqConfig;
import com.abb.bye.utils.http.SimpleHttpBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
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
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
@Service("customDownloader")
public class CustomDownloader extends AbstractDownloader {
    private static final Logger logger = LoggerFactory.getLogger(CustomDownloader.class);
    private CloseableHttpAsyncClient closeableHttpAsyncClient = new SimpleHttpBuilder().setRedirectStrategy(new SimpleHttpBuilder.CustomRedirectStrategy()).build();
    @Resource
    private PriorityProxyProvider priorityProxyProvider;
    private int thread;
    private LocalLimiter localLimiter = new LocalLimiter();
    private boolean responseHeader = true;

    @Override
    public Page download(Request request, Task task) {
        HttpHost httpHost = priorityProxyProvider.getProxy();
        Page page = Page.fail();
        HttpResponse response = null;
        try {
            localLimiter.add(thread);
            long t = System.currentTimeMillis();
            response = HttpHelper.getResponse(closeableHttpAsyncClient, request.getUrl(), new ReqConfig()
                .setCharset(request.getCharset())
                .setConnectionTimeout(task.getSite().getTimeOut())
                .setConnectionRequestTimeout(task.getSite().getTimeOut())
                .setSocketTimeout(task.getSite().getTimeOut())
                .setProxy(httpHost)
            );
            page = handleResponse(request, request.getCharset() != null ? request.getCharset() : task.getSite().getCharset(), response, task);
            onSuccess(request);
            priorityProxyProvider.success(httpHost, (System.currentTimeMillis() - t));
            return page;
        } catch (Throwable e) {
            logger.warn("Error download:" + request.getUrl(), e);
            priorityProxyProvider.failed(httpHost);
            onError(request);
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

    public void setPriorityProxyProvider(PriorityProxyProvider priorityProxyProvider) {
        this.priorityProxyProvider = priorityProxyProvider;
    }
}
