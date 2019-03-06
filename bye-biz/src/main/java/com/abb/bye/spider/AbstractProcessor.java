package com.abb.bye.spider;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
public abstract class AbstractProcessor implements PageProcessor {
    protected String DEFAULT_UA = "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 7 Build/MOB30X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    private Site DEFAULT_SITE = Site.me().setRetryTimes(3).setSleepTime(100).setUserAgent(DEFAULT_UA);

    @Override
    public Site getSite() {
        return DEFAULT_SITE;
    }

    protected String[] toMultiValue(String content) {
        if (content == null) {
            return null;
        }
        String[] values = StringUtils.split(content, "/");
        String[] vs = new String[values.length];
        int i = 0;
        for (String v : values) {
            vs[i++] = v.trim();
        }
        return vs;
    }

    protected Map<String, String> toProperties(Elements elements) {
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
