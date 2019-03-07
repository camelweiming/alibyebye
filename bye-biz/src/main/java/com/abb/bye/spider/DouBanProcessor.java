package com.abb.bye.spider;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.PersonDO;
import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.utils.CommonUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
@Component
public class DouBanProcessor extends AbstractProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DouBanProcessor.class);
    private static Pattern pattern = Pattern.compile("douban.com/subject/(\\d+)");
    private static Pattern personPattern = Pattern.compile("/celebrity/(\\d+)");

    @Override
    public void process(Page page) {
        logger.info("begin process-page:" + page.getUrl());
        String id = getId(page);
        if (id == null) {
            processList(page);
        } else {
            processDetail(id, page);
        }
    }

    public void processDetail(String sourceId, Page page) {
        ProgrammeSourceDO programme = new ProgrammeSourceDO();
        programme.setSourceId(sourceId);
        programme.setSite(1);
        programme.setUrl(page.getUrl().get());
        Document doc = page.getHtml().getDocument();
        Elements content = doc.select("div#content");
        programme.setTitle(content.select("h1 span[property=v:itemreviewed]").text());
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
        Map<String, String> properties = toProperties(elements);
        programme.setLanguages(Joiner.on(",").join(toMultiValue(properties.get("语言"))));
        programme.setAlias(Joiner.on(",").join(toMultiValue(properties.get("又名"))));
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
        programme.setImdb(properties.get("IMDb链接"));
        String score = doc.select("div.rating_self").select("strong.rating_num").text();
        if (StringUtils.isNotBlank(score)) {
            programme.setScore(Double.parseDouble(score));
        }
        String summary = doc.select("div.related-info").select("[property=v:summary]").text();
        programme.setStatus(ProgrammeSourceDO.STATUS_ENABLE);
        programme.setSummary(StringUtils.substring(summary, 0, 500));
        programme.setAttributes(JSON.toJSONString(attributes));
        page.putField(Constants.SPIDER_PROGRAMME_FIELD_NAME, programme);
        if (logger.isDebugEnabled()) {
            logger.debug("process:" + programme);
        }
    }

    public void processList(Page page) {
        JSONObject json = JSON.parseObject(page.getRawText());
        JSONArray jsonArray = json.getJSONArray("data");
        JSONObject obj;
        List<String> links = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            obj = jsonArray.getJSONObject(i);
            links.add(obj.getString("url"));
        }
        page.addTargetRequests(links);
        page.setSkip(true);
    }

    private String getId(Page page) {
        Matcher matcher = pattern.matcher(page.getUrl().get());
        boolean isDetail = matcher.find();
        if (isDetail) {
            return matcher.group(1);
        }
        return null;
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
        int MAX_PAGE = 1000;
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
}
