package com.abb.bye.spider;

import com.abb.bye.client.domain.Proxy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
public class ProxyQueue {
    private final List<Proxy> hosts;
    private AtomicInteger c = new AtomicInteger(0);
    private Map<Proxy, Report> proxies = new ConcurrentHashMap<>();

    public ProxyQueue(List<Proxy> hosts) {
        this.hosts = hosts;
    }

    public Proxy get() {
        int current = c.get();
        if (current >= hosts.size()) {
            current = 0;
            c.set(0);
        }
        c.incrementAndGet();
        return hosts.get(current);
    }

    public void report(Proxy proxy) {
        Report report = proxies.get(proxy);
        if (report == null) {
            //miss lock
            report = new Report();
            proxies.put(proxy, report);
        }
        if (proxy.isSuccess()) {
            report.plusSuccess(proxy.getCost());
        } else {
            report.plusFailed();
        }
    }

    public Map<Proxy, Report> getReport() {
        return proxies;
    }

    public static class Report {
        private long totalCost;
        private int totalSuccess;
        private int totalFailed;

        public void plusSuccess(long cost) {
            totalSuccess++;
            totalCost += cost;
        }

        public void plusFailed() {
            totalFailed++;
        }

        public int getTotalFailed() {
            return totalFailed;
        }

        public int getAvgCost() {
            return totalSuccess == 0 ? 0 : (int)(totalCost / totalSuccess);
        }

        public double getSuccessRate() {
            int total = totalSuccess + totalFailed;
            return total == 0 ? 0 : Math.round(((double)totalSuccess / (double)total) * 10d) / 10d;
        }
    }
}
