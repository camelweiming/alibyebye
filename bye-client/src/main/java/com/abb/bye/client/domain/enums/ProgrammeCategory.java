package com.abb.bye.client.domain.enums;

/**
 * @author cenpeng.lwm
 * @since 2019/3/11
 */
public enum ProgrammeCategory {
    SHOW(1, "电视剧"),
    MOVIE(2, "电影"),
    ZY(3, "综艺"),
    COMIC(4, "动漫"),
    EDU(5, "教育"),
    DOC(6, "纪录片"),
    SPORT(7, "体育"),
    CHILDREN(8, "少儿");
    int type;
    String name;

    ProgrammeCategory(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
