package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author cenpeng.lwm
 * @since 2019/6/4
 */
public class UserOptions implements Serializable {
    private static final long serialVersionUID = 7021782933274931573L;
    private boolean useCache = true;
    private boolean withBoss = false;
    private boolean withAllBosses = false;

    public boolean isUseCache() {
        return useCache;
    }

    public UserOptions setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    public boolean isWithBoss() {
        return withBoss;
    }

    public UserOptions setWithBoss(boolean withBoss) {
        this.withBoss = withBoss;
        return this;
    }

    public boolean isWithAllBosses() {
        return withAllBosses;
    }

    public UserOptions setWithAllBosses(boolean withAllBosses) {
        this.withAllBosses = withAllBosses;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
