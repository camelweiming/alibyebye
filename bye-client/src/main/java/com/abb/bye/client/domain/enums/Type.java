package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2019/3/5
 */
public enum Type {
    MOVIE(1, "电影"),
    SERIES(2, "电视剧");
    private int id;
    private String name;

    Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
