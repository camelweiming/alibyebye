package com.abb.bye.spider;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.PageDTO;
import com.abb.bye.client.domain.PersonDO;
import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.spider.SpiderProcessor;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.Md5;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/11
 */
@Component
public class PiPiZhuProcessor implements SpiderProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PiPiZhuProcessor.class);

    @Override
    public void process(PageDTO page) {
        Document doc = Jsoup.parse(page.getHtml(), page.getUrl());
        Elements main = doc.select("div.vod-n-l");
        String title = main.select("h1").text();
        if (StringUtils.isBlank(title)) {
            processList(page, doc);
        } else {
            processDetail(page, doc, main, title);
        }
    }

    private void processList(PageDTO page, Document doc) {
        page.setSkip(true);
        Html html = new Html(doc);
        List<String> links = html.links().regex("(https://m\\.pipigui\\.tv/.*)").all();
        links.removeIf(s -> (s.contains("/player-") || s.contains("/vod-search-wd-")));
        page.setTargetRequests(links);
    }

    private void processDetail(PageDTO page, Document doc, Elements main, String title) {
        ProgrammeSourceDO programme = new ProgrammeSourceDO();
        programme.setImg(doc.select("div.vod-n-img").select("img").attr("data-original"));
        programme.setUrl(page.getUrl());
        programme.setTitle(CommonUtils.formatTitle(title));
        programme.setSourceId(parseSourceId(page.getUrl()));
        programme.setSeconds(0);
        Elements desc = main.select("p");
        for (Element element : desc) {
            String[] each = StringUtils.split(element.text(), ":：");
            if (each.length < 2) {
                logger.warn("BAD_LINE:" + element.text());
                continue;
            }
            String key = each[0];
            String value = each[1];
            if (key.equals("年份")) {
                programme.setReleaseYear(Integer.valueOf(value));
            } else if (key.equals("简介")) {
                programme.setSummary(value);
            } else if (key.equals("语言")) {
                programme.setLanguages(Joiner.on(",").skipNulls().join(StringUtils.split(value, "/")));
            } else if (key.equals("类型")) {
                programme.setTypes(Joiner.on(",").skipNulls().join(StringUtils.split(value, " ")));
            } else if (key.equals("地区")) {
                programme.setAreas(Joiner.on(",").skipNulls().join(StringUtils.split(value, " ")));
            } else if (key.equals("主演")) {
                String[] ps = StringUtils.split(value, " ");
                List<PersonDO> performers = new ArrayList<>();
                int i = 0;
                for (String p : ps) {
                    PersonDO performer = new PersonDO();
                    performer.setName(p);
                    performers.add(performer);
                }
                programme.setPerformers(JSON.toJSONString(performers));
            } else if (key.equals("导演")) {
                String[] ps = StringUtils.split(value, " ");
                List<PersonDO> directors = new ArrayList<>();
                int i = 0;
                for (String p : ps) {
                    PersonDO director = new PersonDO();
                    director.setName(p);
                    directors.add(director);
                }
                programme.setDirectors(JSON.toJSONString(directors));
            } else if (key.equals("状态")) {
                programme.setShowStatus(value);
            }
        }
        String summary = main.select("div.v-js").text();
        programme.setSummary(summary);
        page.addField(Constants.SPIDER_PROGRAMME_FIELD_NAME, programme);
        if (logger.isDebugEnabled()) {
            logger.debug("process:" + programme);
        }
    }

    @Override
    public String parseSourceId(String url) {
        String[] vs = StringUtils.split(url, "/");
        String id = vs[vs.length - 1];
        return (id.length() > Constants.MAX_SOURCE_ID) ? (Constants.MD5_SOURCE_ID_PREFIX + Md5.getInstance().getMD5String(id)) : id;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
