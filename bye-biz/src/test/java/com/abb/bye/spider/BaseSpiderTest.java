package com.abb.bye.spider;

import com.abb.bye.client.domain.PageDTO;
import com.abb.bye.client.spider.SpiderProcessor;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author cenpeng.lwm
 * @since 2019/3/11
 */
public abstract class BaseSpiderTest {
    protected Site site = Site.me().setRetryTimes(3).setSleepTime(100).setUserAgent(
        "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 7 Build/MOB30X) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
    PageProcessor pageProcessor = new PageProcessor() {
        @Override
        public void process(Page page) {
            PageDTO pageDTO = new PageDTO();
            pageDTO.setUrl(page.getUrl().get());
            pageDTO.setHtml(page.getRawText());
            pageDTO.setCharset(page.getCharset());
            pageDTO.setDownloadSuccess(page.isDownloadSuccess());
            pageDTO.setStatusCode(page.getStatusCode());
            getProcessor().process(pageDTO);
            page.setSkip(pageDTO.isSkip());
            if (pageDTO.getTargetRequests() != null) {
                page.addTargetRequests(pageDTO.getTargetRequests());
            }
            if (pageDTO.getFields() != null) {
                pageDTO.getFields().forEach((k, v) -> page.putField(k, v));
            }
        }

        @Override
        public Site getSite() {
            return site;
        }
    };

    public void run(String url) {
        Spider.create(pageProcessor).addUrl(url).addPipeline(new ConsolePipeline()).thread(1).run();
    }

    abstract SpiderProcessor getProcessor();
}
