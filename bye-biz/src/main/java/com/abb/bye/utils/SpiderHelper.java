package com.abb.bye.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.util.*;

/**
 * @author cenpeng.lwm
 * @since 2019/3/8
 */
public class SpiderHelper {
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
}
