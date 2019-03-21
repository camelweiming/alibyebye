package com.abb.bye.client.domain;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author cenpeng.lwm
 * @since 2019/3/21
 */
public class SequenceRange {
    private final long min;
    private final long max;

    private final AtomicLong value;

    private volatile boolean over = false;

    public SequenceRange(long min, long max) {
        this.min = min;
        this.max = max;
        this.value = new AtomicLong(min);
    }

    public long getBatch(int size) {
        if (over) {
            return -1;
        }

        long currentValue = value.getAndAdd(size) + size - 1;
        if (currentValue > max) {
            over = true;
            return -1;
        }

        return currentValue;
    }

    public long getAndIncrement() {
        if (over) {
            return -1;
        }

        long currentValue = value.getAndIncrement();
        if (currentValue > max) {
            over = true;
            return -1;
        }

        return currentValue;
    }

    public long[] getCurrentAndMax() {
        if (over) {
            return null;
        }

        long[] currentAndMax = new long[2];

        long currentValue = value.get();
        if (currentValue > max) {
            over = true;
            return null;
        }

        currentAndMax[0] = currentValue;
        currentAndMax[1] = max;

        return currentAndMax;
    }

    public boolean updateValue(long expect, long update) {
        if (over) {
            return true;
        }
        return value.compareAndSet(expect, update);
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("max: ").append(max).append(", min: ").append(min).append(", value: ").append(value);
        return sb.toString();
    }
}
