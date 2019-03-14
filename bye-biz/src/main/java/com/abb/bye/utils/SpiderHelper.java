package com.abb.bye.utils;

import com.abb.bye.client.domain.ProxyDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.assertj.core.util.Lists;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public class SpiderHelper {
    /**
     * @page-split[0:20->100] 分页[起始页:步长->总页数]
     */
    public static final String EXP_PAGE_SPLIT = "@page-split";
    private static Pattern LIST_PATTERN = Pattern.compile(EXP_PAGE_SPLIT + "\\[(\\d+)\\:(\\d+)->(\\d+)\\]");

    public static Set<String> toMultiValue(String content) {
        Set<String> vs = new HashSet<>(8);
        if (content == null) {
            return vs;
        }
        String[] values = StringUtils.split(content, "/");
        for (String v : values) {
            vs.add(v.trim());
        }
        return vs;
    }

    public static Map<String, String> toProperties(Elements elements) {
        Map<String, String> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        elements.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int i) {
                if (node instanceof TextNode) {
                    TextNode textNode = (TextNode)node;
                    sb.append(textNode.getWholeText());
                } else if (node instanceof Element) {
                    boolean br = ((Element)node).tag().getName().equals("br");
                    if (br) {
                        String value = sb.toString().trim();
                        list.add(value);
                        if (sb.length() > 0) {
                            sb.delete(0, sb.length());
                        }
                    }
                }
            }

            @Override
            public void tail(Node node, int i) {

            }
        });
        list.forEach(line -> {
            String[] values = line.split(": ");
            if (values.length >= 2) {
                map.put(values[0].trim(), values[1].trim());
            }
        });
        return map;
    }

    public static boolean isSplitPages(String url) {
        return url.contains(EXP_PAGE_SPLIT);
    }

    public static List<String> splitPages(String url) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = LIST_PATTERN.matcher(url);
        if (!matcher.find()) {
            return Lists.newArrayList(url);
        }
        int start = Integer.parseInt(matcher.group(1));
        int length = Integer.parseInt(matcher.group(2));
        int pages = Integer.parseInt(matcher.group(3));
        String _url = matcher.replaceAll(EXP_PAGE_SPLIT);
        for (int i = 0; i < pages; i++) {
            String real = StringUtils.replace(_url, EXP_PAGE_SPLIT, "" + start);
            urls.add(real);
            start += length;
        }
        return urls;
    }

    public static HttpHost create(ProxyDO proxy) {
        String[] array = StringUtils.split(proxy.getHost(), ":");
        if (array.length == 1) {
            return new HttpHost(array[0]);
        }
        return new HttpHost(array[0], Integer.valueOf(array[1]));
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<String> list = splitPages("https://movie.douban.com/j/new_search_subjects?sort=R&range=1,10&tags=%E7%94%B5%E5%BD%B1&start=@page-split[99:20->100]");
        list.forEach(l->{System.out.println(l);});
    }
}
