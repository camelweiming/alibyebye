package com.abb.bye.client.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public class RejectStrategyConfig {
    /**
     * 只处理新增
     */
    final private boolean onlyInsert;
    /**
     * 更新间隔
     */
    final private int updateIntervalSeconds;

    public RejectStrategyConfig(boolean onlyInsert, int updateIntervalSeconds) {
        this.onlyInsert = onlyInsert;
        this.updateIntervalSeconds = updateIntervalSeconds;
    }

    public boolean isOnlyInsert() {
        return onlyInsert;
    }

    public int getUpdateIntervalSeconds() {
        return updateIntervalSeconds;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
