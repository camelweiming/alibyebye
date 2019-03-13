package com.abb.bye.client.domain;

import java.util.Objects;

/**
 * @author cenpeng.lwm
 * @since 2019/3/13
 */
public class Proxy {
    private String host;
    private Integer port;
    private boolean success;
    private long cost;

    @Override
    public String toString() {
        return port == null ? host : host + ":" + port;
    }

    public boolean isSuccess() {
        return success;
    }

    public Proxy setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public long getCost() {
        return cost;
    }

    public Proxy setCost(long cost) {
        this.cost = cost;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Proxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Proxy proxy = (Proxy)o;
        return Objects.equals(host, proxy.host) &&
            Objects.equals(port, proxy.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
