package com.abb.bye.spider;

import com.abb.bye.Constants;
import com.abb.bye.client.service.SiteConfigsService;
import com.abb.bye.utils.CommonThreadPool;
import com.abb.bye.utils.Md5;
import com.abb.bye.utils.http.HttpHelper;
import com.abb.bye.utils.http.PriorityProxyProvider;
import com.abb.bye.utils.http.ReqConfig;
import com.abb.bye.utils.http.SimpleHttpBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 比较好的方式是以中立的网站作为降权依据
 *
 * @author cenpeng.lwm
 * @since 2019/3/12
 */
@Component
public class PriorityProxyProviderImpl implements PriorityProxyProvider, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(PriorityProxyProviderImpl.class);
    @Resource
    private SiteConfigsService siteConfigsService;
    private ProxyQueue proxyQueue = new ProxyQueue(Lists.newArrayList());
    private String sign;
    private String preloadUrl = "https://baidu.com";

    @Override
    public void afterPropertiesSet() throws Exception {
        reload();
        CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> reload(), 0, 2, TimeUnit.MINUTES);
    }

    public void reload() {
        String proxies = siteConfigsService.getFromDB(0, Constants.SYSTEM_CONFIG_SPIDER_PROXY);
        if (StringUtils.isBlank(proxies)) {
            return;
        }
        String currentSign = Md5.getInstance().getMD5String(proxies);
        if (StringUtils.equals(sign, currentSign)) {
            logger.info("proxy-config not diff");
            proxyQueue.buildRecommendLists();
            return;
        }
        String[] array = StringUtils.split(proxies, "\r\n");
        List<HttpHost> list = new ArrayList<>();
        for (String p : array) {
            String[] line = StringUtils.split(p, ":");
            list.add(new HttpHost(line[0], Integer.valueOf(line[1])));
        }
        proxyQueue = new ProxyQueue(list);
        CommonThreadPool.getCommonExecutor().submit(() -> preload());
        sign = currentSign;
        logger.info("reload proxy:" + proxyQueue.getHosts());
    }

    public void preload() {
        CloseableHttpAsyncClient closeableHttpAsyncClient = new SimpleHttpBuilder().setSocketTimeout(1000).setConnectionTimeout(1000).setConnectionRequestTimeout(1000).build();
        try {
            proxyQueue.getHosts().parallelStream().forEach(h -> {
                try {
                    long t = System.currentTimeMillis();
                    String content = HttpHelper.get(closeableHttpAsyncClient, preloadUrl, new ReqConfig().setProxy(h));
                    long cost = System.currentTimeMillis() - t;
                    logger.info("preload-success:" + h + " cost:" + cost + " content:" + (content == null ? "" : content.substring(0, 20)));
                    proxyQueue.success(h, cost);
                } catch (Exception e) {
                    logger.info("preload-failed:" + h);
                    proxyQueue.failed(h);
                }
            });
            proxyQueue.buildRecommendLists();
        } finally {
            try {
                closeableHttpAsyncClient.close();
            } catch (Throwable e) {
                logger.error("Error close", e);
            }
        }

    }

    @Override
    public HttpHost getProxy() {
        return proxyQueue.getHost();
    }

    @Override
    public void success(HttpHost httpHost, long cost) {
        proxyQueue.success(httpHost, cost);
    }

    @Override
    public void failed(HttpHost httpHost) {
        proxyQueue.failed(httpHost);
    }
}
