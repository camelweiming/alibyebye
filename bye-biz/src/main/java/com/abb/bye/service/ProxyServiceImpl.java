package com.abb.bye.service;

import com.abb.bye.client.domain.Proxy;
import com.abb.bye.client.domain.ProxyDO;
import com.abb.bye.client.service.ProxyService;
import com.abb.bye.mapper.ProxyMapper;
import com.abb.bye.spider.ProxyQueue;
import com.abb.bye.utils.CommonThreadPool;
import com.abb.bye.utils.http.HttpHelper;
import com.abb.bye.utils.http.ReqConfig;
import com.abb.bye.utils.http.SimpleHttpBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
@Service("proxyService")
public class ProxyServiceImpl implements ProxyService, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(ProxyServiceImpl.class);
    private static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(0, 10, 30, TimeUnit.SECONDS, new ArrayBlockingQueue(10), r -> {
        Thread t = new Thread(r, "PROXY_THREAD_POOL");
        t.setDaemon(true);
        return t;
    }, (r, executor) -> {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            logger.error("Error put queue", e);
        }
    });
    private Closeable httpClient = new SimpleHttpBuilder().setSocketTimeout(5000).setConnectionTimeout(5000).setConnectionRequestTimeout(5000).build();
    @Resource
    private ProxyMapper proxyMapper;
    private String checkUrl = "https://baidu.com";
    private static int STEP = 1000;
    private ProxyQueue proxyQueue;

    @Override
    public List<String> list(int count, int maxFailedCount) {
        return proxyMapper.list(count, maxFailedCount);
    }

    public void reload() {
        if (proxyQueue != null) {
            proxyQueue.getReport().forEach((proxy, report) -> {
                ProxyDO p = new ProxyDO();
                p.setHost(proxy.toString());
                p.setAvgCost(report.getAvgCost());
                p.setSuccessRate(report.getSuccessRate());
                p.setFailedCount(report.getTotalFailed() == 0 ? -1 : report.getTotalFailed());
                logger.info("write proxy report:" + p);
                proxyMapper.insert(p);
            });
        }
        List<String> proxies = list(1000, 3);
        List<Proxy> list = new ArrayList<>();
        for (String p : proxies) {
            String[] line = StringUtils.split(p, ":");
            list.add(new Proxy(line[0], Integer.valueOf(line[1])));
        }
        ProxyQueue _proxyQueue = new ProxyQueue(list);
        proxyQueue = _proxyQueue;
        logger.info("reload proxy:" + list.size());
    }

    @Override
    public void check() {
        long id = 0;
        List<String> hosts;
        while (true) {
            hosts = proxyMapper.listAll(id, STEP, 3);
            if (hosts.isEmpty()) {
                break;
            }
            id += STEP;
            for (String host : hosts) {
                EXECUTOR.submit(() -> {
                    String[] array = StringUtils.split(host, ":");
                    try {
                        long t = System.currentTimeMillis();
                        int status = HttpHelper.touch(httpClient, checkUrl, new ReqConfig().setProxy(new HttpHost(array[0], Integer.valueOf(array[1]))));
                        if (status == HttpStatus.SC_OK) {
                            ProxyDO p = new ProxyDO();
                            p.setHost(host);
                            p.setSuccessRate(1d);
                            p.setAvgCost((int)((System.currentTimeMillis()) - t));
                            p.setFailedCount(-1);
                            makeSuccess(p);
                            logger.info("check-success:" + host + " cost:" + p.getAvgCost());
                        } else {
                            makeFailed(host);
                            logger.info("check-failed:" + host + " status:" + status);
                        }
                    } catch (Exception e) {
                        makeFailed(host);
                        logger.info("check-failed:" + host + " status:" + e.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void makeSuccess(ProxyDO proxyDO) {
        proxyMapper.insert(proxyDO);
    }

    @Override
    public void makeFailed(String host) {
        ProxyDO p = new ProxyDO();
        p.setHost(host);
        p.setAvgCost(0);
        p.setSuccessRate(0d);
        p.setFailedCount(1);
        proxyMapper.insert(p);
    }

    @Override
    public Proxy getProxy() {
        return proxyQueue.get();
    }

    @Override
    public List<Proxy> getProxy(int n) {
        List<Proxy> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(getProxy());
        }
        return list;
    }

    @Override
    public void report(Proxy proxy) {
        proxyQueue.report(proxy);
    }

    @Override
    public void afterPropertiesSet() {
        reload();
        CommonThreadPool.getScheduledExecutor().scheduleAtFixedRate(() -> reload(), 0, 2, TimeUnit.MINUTES);
    }
}
