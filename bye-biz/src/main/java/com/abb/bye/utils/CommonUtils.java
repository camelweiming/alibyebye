package com.abb.bye.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //public static String full2Half(String string) {
    //    if (string == null) {
    //        return string;
    //    }
    //    char[] charArray = string.toCharArray();
    //    for (int i = 0; i < charArray.length; i++) {
    //        char c = charArray[i];
    //        if (Character.isLetterOrDigit(c)) {
    //            continue;
    //        }
    //        if (c == 12288) {
    //            charArray[i] = ' ';
    //        } else if (c >= ' ' &&
    //            c <= 65374) {
    //            charArray[i] = (char)(c - 65248);
    //        } else {
    //        }
    //    }
    //    return new String(charArray);
    //}

    public static void main(String[] args) {
        String title = formatTitle("无耻之徒(美版)第九季");
        formatTitle(title);
        System.out.println(title);
        String alias = formatTitle("阿丽塔：战斗天使 Alita: Battle Angel");
        System.out.println(alias);
    }
}
