package com.abb.bye.spider;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 加权随机，取最优host
 *
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
public class ProxyQueue {
    private static Logger logger = LoggerFactory.getLogger(ProxyQueue.class);
    private final List<HttpHost> hosts;
    private List<HttpHost> recommendHosts;
    private Map<HttpHost, Counter> mapping = new ConcurrentHashMap<>(32);
    private int totalWeight;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public ProxyQueue(List<HttpHost> hosts) {
        this.hosts = hosts;
        this.recommendHosts = hosts;
        for (int i = 1; i <= recommendHosts.size(); i++) {
            totalWeight += i;
        }
    }

    public void success(HttpHost httpHost, long cost) {
        Counter c = mapping.get(httpHost);
        if (c == null) {
            c = new Counter();
            mapping.put(httpHost, c);
        }
        c.plusSuccess(cost);
    }

    public void failed(HttpHost httpHost) {
        Counter c = mapping.get(httpHost);
        if (c == null) {
            c = new Counter();
            mapping.put(httpHost, c);
        }
        c.plusFailed();
    }

    public void buildRecommendLists() {
        mapping.forEach((k, v) -> logger.info("proxy-rate:" + k + "--> successRate:" + v.successRate() + " costAvg:" + v.getAvgCost()));
        List<HttpHost> _hosts = hosts.stream().filter(httpHost -> {
            Counter c = mapping.get(httpHost);
            return c.success > 0;
        }).collect(Collectors.toList());
        if (_hosts.isEmpty()) {
            return;
        }

        Comparator<HttpHost> comparator = (o1, o2) -> {
            Counter score1 = mapping.get(o1);
            Counter score2 = mapping.get(o2);
            if (score1.successRate() == score2.successRate()) {
                return Double.compare(score1.getAvgCost(), score2.getAvgCost());
            }
            return Double.compare(score2.successRate(), score1.successRate());
        };
        Collections.sort(_hosts, comparator);
        Collections.reverse(_hosts);
        int _totalWeight = 0;
        for (int i = 1; i <= _hosts.size(); i++) {
            _totalWeight += i;
        }
        writeLock.lock();
        totalWeight = _totalWeight;
        recommendHosts = _hosts;
        writeLock.unlock();
        logger.info("proxy-recommend:" + recommendHosts);
    }

    public HttpHost getHost() {
        if (recommendHosts.isEmpty()) {
            return null;
        }
        readLock.lock();
        try {
            int random = new Random().nextInt(totalWeight);
            for (int i = 0; i < recommendHosts.size(); i++) {
                random -= (i + 1);
                HttpHost h = recommendHosts.get(i);
                if (random < 0) {
                    return h;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public List<HttpHost> getRecommendHosts() {
        return recommendHosts;
    }

    public static void main(String[] args) {
        List<HttpHost> list = new ArrayList<>();
        HttpHost a = new HttpHost("192.168.1.1", 8881);
        HttpHost b = new HttpHost("192.168.1.1", 8882);
        HttpHost c = new HttpHost("192.168.1.1", 8883);
        HttpHost d = new HttpHost("192.168.1.1", 8884);
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        ProxyQueue proxyQueue = new ProxyQueue(list);
        proxyQueue.success(a, 3000);
        proxyQueue.success(a, 1000);
        proxyQueue.failed(a);
        proxyQueue.success(b, 1000);
        proxyQueue.success(c, 5000);
        proxyQueue.failed(d);
        proxyQueue.buildRecommendLists();
        System.out.println(proxyQueue.recommendHosts);
        Map<HttpHost, AtomicInteger> m = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            HttpHost h = proxyQueue.getHost();
            AtomicInteger count = m.get(h);
            if (count == null) {
                count = new AtomicInteger(1);
                m.put(h, count);
            } else {
                count.incrementAndGet();
            }
        }
        m.forEach((k, v) -> {
            System.out.println(k + " count:" + v.get());
        });
    }

    public List<HttpHost> getHosts() {
        return hosts;
    }

    /**
     * 不需要很精确
     */
    public static class Counter {
        private long totalCost;
        private long maxCost;
        private int success = 0;
        private int failed = 0;

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }

        public synchronized void plusSuccess(long cost) {
            success++;
            maxCost = Math.max(cost, maxCost);
            totalCost += cost;
        }

        public synchronized void plusFailed() {
            failed++;
        }

        public long getMaxCost() {
            return maxCost;
        }

        public double successRate() {
            int total = success + failed;
            return total == 0 ? 0 : Math.round(((double)success / (double)total) * 10d) / 10d;
        }

        public long getAvgCost() {
            return success == 0 ? 0 : totalCost / success;
        }
    }
}
