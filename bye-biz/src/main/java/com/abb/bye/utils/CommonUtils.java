package com.abb.bye.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public class CommonUtils {
    public static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        return Long.valueOf(value.toString());
    }

    public static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return Integer.valueOf(value.toString());
    }

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

    public static String clean(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : content.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static final <T> List<T> subList(List<T> list, int start, int limit) {
        int index = Math.min(limit + start, list.size());
        List<T> sub = new ArrayList<>(limit);
        for (int i = start; i < index; i++) {
            sub.add(list.get(i));
        }
        return sub;
    }

    private static Pattern SERIES_PATTERN = Pattern.compile("第(.*)季");

    public static final String formatTitle(String title) {
        if (title == null) {
            return null;
        }
        title = StringUtils.replaceEach(title, new String[] {"：", "（", "）", "“", "”", "！"}, new String[] {":", "(", ")", "\"", "\"", "!"});
        Matcher m = SERIES_PATTERN.matcher(title);
        if (m.find()) {
            StringBuffer sb = new StringBuffer();
            m.appendReplacement(sb, " 第" + m.group(1) + "季");
            m.appendTail(sb);
            title = sb.toString();
        }
        title = title.replaceAll("\\[.*?\\]", "");
        title = title.replaceAll("\\(.*?\\)", "");
        title = title.replaceAll("\\s{1,}", " ").trim();
        return title;
    }

    /**
     * 从属性文件设置属性，性能差
     *
     * @param obj
     * @param propertiesContent
     */
    public static final void copyFromProperties(Object obj, String propertiesContent) {
        try {
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(propertiesContent.getBytes("UTF-8")));
            Enumeration<String> enumeration = (Enumeration<String>)properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String name = enumeration.nextElement();
                String value = properties.getProperty(name);
                if (StringUtils.isNotBlank(value)) {
                    org.apache.commons.beanutils.BeanUtils.copyProperty(obj, name, value);

                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> asMap(String json) {
        Map<String, Object> attributeMap = StringUtils.isBlank(json) ? new HashMap<String, Object>() : (Map<String, Object>)JSON.parseObject(json, Map.class);
        return attributeMap;
    }

    public String formatDate(Date date) {
        return new DateTime(date).toString("yyyy-MM-dd HH:mm:ss");
    }

    public static String formatText(String tpl, Map<String, String> params) {
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        return strSubstitutor.replace(tpl);
    }

    public static void main(String[] args) {
        Map<String, String> params = new HashMap<>();
        params.put("taskId", "22222");
        System.out.println(formatText("approve_holiday.htm?taskId=${taskId}", params));
    }
}
