package com.abb.bye.spider;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.PageDTO;
import com.abb.bye.client.domain.PersonDO;
import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.spider.SpiderProcessor;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.SpiderHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
@Component
public class DouBanProcessor implements SpiderProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DouBanProcessor.class);
    private static Pattern pattern = Pattern.compile("douban.com/subject/(\\d+)");
    private static Pattern personPattern = Pattern.compile("/celebrity/(\\d+)");

    @Override
    public void process(PageDTO page) {
        logger.info("begin process-page:" + page.getUrl());
        String id = parseSourceId(page.getUrl());
        if (id == null) {
            processList(page);
        } else {
            processDetail(id, page);
        }
    }

    @Override
    public String parseSourceId(String url) {
        Matcher matcher = pattern.matcher(url);
        boolean isDetail = matcher.find();
        if (isDetail) {
            return matcher.group(1);
        }
        return null;
    }

    public void processDetail(String sourceId, PageDTO page) {
        ProgrammeSourceDO programme = new ProgrammeSourceDO();
        programme.setSourceId(sourceId);
        programme.setSite(1);
        programme.setUrl(page.getUrl());
        Document doc = Jsoup.parse(page.getHtml(), page.getUrl());
        String title = parseTitle(doc);
        programme.setTitle(title);
        Elements content = doc.select("div#content");
        String titleAlias = CommonUtils.formatTitle(content.select("h1 span[property=v:itemreviewed]").text());
        titleAlias = StringUtils.replace(titleAlias, title, "").trim();

        String year = CommonUtils.clean(content.select("h1 span.year").text());
        if (year == null) {
            logger.warn("no-release-year:" + programme.getUrl());
            page.setSkip(true);
            return;
        }
        programme.setReleaseYear(Integer.valueOf(year));
        programme.setImg(content.select("div#mainpic").select("img").attr("src"));

        /**
         * 导演
         */
        List<PersonDO> directors = new ArrayList<>();
        content.select("#info").select("a[rel=v:directedBy]").forEach(link -> {
            String id = getPersonId(link.attr("href"));
            PersonDO director = new PersonDO();
            director.setId(id);
            director.setName(link.text());
            directors.add(director);
        });
        programme.setDirectors(JSON.toJSONString(CommonUtils.subList(directors, 0, 5)));
        /**
         * 演员
         */
        List<PersonDO> performers = new ArrayList<>();
        content.select("#info").select("a[rel=v:starring]").forEach(link -> {
            String id = getPersonId(link.attr("href"));
            PersonDO director = new PersonDO();
            director.setId(id);
            director.setName(link.text());
            performers.add(director);
        });
        programme.setPerformers(JSON.toJSONString(CommonUtils.subList(performers, 0, 10)));
        /**
         * 季数
         */
        String season = content.select("select#season").select("option[selected]").text();
        if (StringUtils.isNotBlank(season)) {
            programme.setSeason(Integer.valueOf(season));
        }

        Elements info = content.select("#info").select("span");
        /**
         * 类型
         */
        List<String> tags = new ArrayList<>();
        info.select("[property=v:genre]").forEach(tag -> {
                tags.add(tag.text());
            }
        );
        programme.setTypes(Joiner.on(",").join(tags));
        Map<String, Object> attributes = new HashMap<>();
        /**
         * 上映日期
         */
        List<String> releaseDate = new ArrayList<>();
        info.select("[property=v:initialReleaseDate]").forEach(tag -> releaseDate.add(tag.text()));
        attributes.put("initialReleaseDate", Joiner.on(",").join(releaseDate));
        /**
         * 属性
         */
        Elements elements = content.select("#info").select("*");
        Map<String, String> properties = SpiderHelper.toProperties(elements);
        programme.setLanguages(Joiner.on(",").join(SpiderHelper.toMultiValue(properties.get("语言"))));
        Set<String> alias = SpiderHelper.toMultiValue(properties.get("又名"));
        if (StringUtils.isNotBlank(titleAlias)) {
            alias.add(titleAlias);
        }
        programme.setAlias(Joiner.on(",").join(alias));
        String totalEpisode = properties.get("集数");
        if (StringUtils.isNotBlank(totalEpisode)) {
            programme.setTotalEpisode(Integer.valueOf(totalEpisode));
        }
        /**
         * 时长
         */
        String min = info.select("[property=v:runtime]").attr("content");
        if (StringUtils.isNotBlank(min)) {
            programme.setSeconds(Integer.parseInt(min) * 60);
        } else {
            min = properties.get("单集片长");
            if (StringUtils.isNotBlank(min)) {
                if (min.endsWith("分钟")) {
                    min = min.replace("分钟", "");
                    programme.setSeconds(Integer.parseInt(min) * 60);
                }
            }
        }
        if (programme.getSeconds() == null) {
            programme.setSeconds(0);
        }
        programme.setImdb(properties.get("IMDb链接"));
        String score = doc.select("div.rating_self").select("strong.rating_num").text();
        if (StringUtils.isNotBlank(score)) {
            programme.setScore(Double.parseDouble(score));
        }
        String summary = doc.select("div.related-info").select("[property=v:summary]").text();
        programme.setStatus(ProgrammeSourceDO.STATUS_ENABLE);
        programme.setSummary(StringUtils.substring(summary, 0, 500));
        programme.setAttributes(JSON.toJSONString(attributes));
        page.addField(Constants.SPIDER_PROGRAMME_FIELD_NAME, programme);
        if (logger.isDebugEnabled()) {
            logger.debug("process:" + programme);
        }
    }

    private String parseTitle(Document doc) {
        String title = CommonUtils.formatTitle(doc.title());
        title = StringUtils.replace(title, "(豆瓣)", "").trim();
        return title;
    }

    public void processList(PageDTO page) {
        JSONObject json = JSON.parseObject(page.getHtml());
        JSONArray jsonArray = json.getJSONArray("data");
        JSONObject obj;
        List<String> links = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            obj = jsonArray.getJSONObject(i);
            links.add(obj.getString("url"));
        }
        page.setTargetRequests(links);
        page.setSkip(true);
    }

    private String getPersonId(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        Matcher matcher = personPattern.matcher(url);
        boolean isDetail = matcher.find();
        if (isDetail) {
            return matcher.group(1);
        }
        return null;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String[] TAGS = new String[] {"电影", "电视剧", "综艺", "动漫", "纪录片"};
        int PAGE_STEP = 20;
        int MAX_PAGE = 10;
        List<String> urls = new ArrayList<>(1024);
        for (String tag : TAGS) {
            String url = "https://movie.douban.com/j/new_search_subjects?sort=R&range=1,10&tags=" + URLEncoder.encode(tag, "UTF-8");
            int start = 0;
            for (int i = 0; i < MAX_PAGE; i++) {
                urls.add(url + "&start=" + start);
                start += PAGE_STEP;
            }
        }
        urls.forEach(url -> {
            System.out.println(url);
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

}
