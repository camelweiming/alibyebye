package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public enum Env {
    DAILY("daily"),
    PRE("pre"),
    ONLINE("online");
    String name;

    Env(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
