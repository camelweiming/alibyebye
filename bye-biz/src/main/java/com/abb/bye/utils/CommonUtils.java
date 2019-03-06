package com.abb.bye.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class CommonUtils {
    public static boolean bitSet(long target, long v) {
        return (target & v) == v;
    }

    public static long bitSetValue(Collection<Long> values) {
        long t = 0;
        if (values == null) {
            return t;
        }
        for (long _tag : values) {
            t |= _tag;
        }
        return t;
    }

    public static List<Long> toLongList(String text, String split) {
        if (text == null) {
            return new ArrayList<>(0);
        }
        String[] strings = StringUtils.split(text, split);
        List<Long> ids = new ArrayList<Long>(strings.length);
        for (String str : strings) {
            ids.add(Long.valueOf(str));
        }
        return ids;
    }

    public static long[] toLongArray(String text, String split) {
        if (text == null) {
            return new long[0];
        }
        String[] strings = StringUtils.split(text, split);
        long[] ids = new long[strings.length];
        int i = 0;
        for (String str : strings) {
            ids[i++] = Long.parseLong(str);
        }
        return ids;
    }

    public static <T> T copyPropertiesQuietly(T source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static String setNullWhenBlank(String v) {
        return StringUtils.isBlank(v) ? null : v;
    }
}
