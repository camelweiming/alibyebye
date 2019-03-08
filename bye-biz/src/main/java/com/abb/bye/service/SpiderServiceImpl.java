package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.Switcher;
import com.abb.bye.client.domain.*;
import com.abb.bye.client.domain.enums.SiteTag;
import com.abb.bye.client.service.*;
import com.abb.bye.client.spider.SpiderProcessor;
import com.abb.bye.utils.CommonThreadPool;
import com.abb.bye.utils.Tracer;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/3/6
 */
@Service("spiderService")
public class SpiderServiceImpl implements SpiderService, ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    @Resource
    private SiteService siteService;
    @Resource
    private SiteConfigsService siteConfigsService;
    @Resource
    private ProgrammeSourceService programmeSourceService;
    @Resource
    private SchedulerService schedulerService;
    @Resource
    private RejectStrategy rejectStrategy;

    public ResultDTO<Void> start(int site) {
        Tracer tracer = new Tracer("SPIDER").setEntityId(site);
        String schedule = siteConfigsService.getFromDB(site, Constants.SYSTEM_CONFIG_SPIDER_SCHEDULE);
        if (schedule == null) {
            tracer.trace("miss schedule");
            return ResultDTO.buildError("miss schedule");
        }
        ResultDTO<Runnable> resultDTO = createJob(site);
        if (!resultDTO.isSuccess()) {
            return ResultDTO.buildError(resultDTO.getErrCode(), resultDTO.getErrMsg());
        }
        schedulerService.register(Constants.SCHEDULE_SPIDER_PREFIX + site, resultDTO.getData(), schedule);
        tracer.trace("register spider-job site:" + site + " schedule:" + schedule);
        return ResultDTO.buildSuccess(null);
    }

    @Override
    public ResultDTO<Runnable> createJob(int site) {
        Tracer tracer = new Tracer("SPIDER").setEntityId(site);
        tracer.trace("begin start...");
        SiteDO siteDO = siteService.getBySiteFromDB(site);
        if (!SiteMapping.match(siteDO, SiteTag.ENABLE_SPIDER)) {
            tracer.trace("spider is unable");
            return ResultDTO.buildError("spider is unable");
        }
        String urls = siteConfigsService.getFromDB(site, Constants.SYSTEM_CONFIG_SPIDER_URLS);
        String config = siteConfigsService.getFromDB(site, Constants.SYSTEM_CONFIG_SPIDER_CONFIG);
        String spiderProcessor = siteConfigsService.getFromDB(site, Constants.SYSTEM_CONFIG_SPIDER_PROCESSOR);
        if (StringUtils.isBlank(urls)) {
            tracer.trace("empty spider urls");
            return ResultDTO.buildError("empty spider urls");
        }
        if (spiderProcessor == null) {
            tracer.trace("miss processor");
            return ResultDTO.buildError("miss processor");
        }
        List<String> urlList = Splitter.on("\r\n").splitToList(urls);
        SpiderConfig spiderConfig = StringUtils.isBlank(config) ? new SpiderConfig() : JSON.parseObject(config, SpiderConfig.class);
        tracer.trace("spiderConfig:" + spiderConfig);
        try {
            Class<SpiderProcessor> clazz = (Class<SpiderProcessor>)Class.forName(spiderProcessor);
            SpiderProcessor processor = applicationContext.getBean(clazz);
            PageProcessorProxy pageProcessor = new PageProcessorProxy(site, spiderConfig, processor);
            Runnable spider = new SpiderRunner(Spider.create(pageProcessor)
                .setExecutorService(CommonThreadPool.getCommonExecutor())
                .addUrl(urlList.toArray(new String[urlList.size()]))
                .addPipeline(new ProgrammePipeline())
                .thread(spiderConfig.getThreadCount()), pageProcessor.processor, spiderConfig, site);
            return ResultDTO.buildSuccess(spider);
        } catch (Throwable e) {
            tracer.trace("Error startSpider", true, e);
            return ResultDTO.buildError(e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() {
        List<SiteDO> sites = siteService.filter(siteService.listFromDB(), Lists.newArrayList(SiteTag.ENABLE_SPIDER), SiteDO.STATUS_ENABLE);
        sites.forEach(siteDO -> start(siteDO.getSite()));
    }

    public class SpiderRunner implements Runnable {
        private Spider spider;
        private SpiderProcessor spiderProcessor;
        private RejectStrategyConfig rejectStrategyConfig;
        private Integer site;

        public SpiderRunner(Spider spider, SpiderProcessor spiderProcessor, SpiderConfig spiderConfig, Integer site) {
            this.spider = spider;
            this.spiderProcessor = spiderProcessor;
            this.site = site;
            this.rejectStrategyConfig = new RejectStrategyConfig(spiderConfig.isOnlyInsert(), spiderConfig.getUpdateIntervalSeconds());
        }

        @Override
        public void run() {
            spiderProcessor.init();
            rejectStrategy.init(site, rejectStrategyConfig);
            spider.run();
            spiderProcessor.destroy();
            rejectStrategy.destroy(site);
        }
    }

    public class PageProcessorProxy implements PageProcessor {
        private SpiderProcessor processor;
        private Site spiderSite;
        private int site;
        private RejectStrategyConfig rejectStrategyConfig;
        private Tracer tracer = new Tracer("PAGE_PROCESSOR");

        private PageProcessorProxy(int site, SpiderConfig spiderConfig, SpiderProcessor processor) {
            this.processor = processor;
            this.site = site;
            this.rejectStrategyConfig = new RejectStrategyConfig(spiderConfig.isOnlyInsert(), spiderConfig.getUpdateIntervalSeconds());
            spiderSite = Site.me()
                .setTimeOut(spiderConfig.getTimeOut())
                .setCycleRetryTimes(spiderConfig.getCycleRetryTimes())
                .setRetryTimes(spiderConfig.getRetryTimes())
                .setSleepTime(spiderConfig.getSleepTime())
                .setUserAgent(spiderConfig.getUserAgent() == null ? Constants.SPIDER_DEFAULT_USER_AGENT : spiderConfig.getUserAgent());
            tracer.setEntityId(site);
        }

        @Override
        public void process(Page page) {
            PageDTO pageDTO = new PageDTO();
            pageDTO.setUrl(page.getUrl().get());
            pageDTO.setHtml(page.getRawText());
            pageDTO.setCharset(page.getCharset());
            pageDTO.setDownloadSuccess(page.isDownloadSuccess());
            pageDTO.setStatusCode(page.getStatusCode());
            processor.process(pageDTO);
            page.setSkip(pageDTO.isSkip());
            if (pageDTO.getTargetRequests() != null) {
                page.addTargetRequests(pageDTO.getTargetRequests());
            }
            if (pageDTO.getFields() != null) {
                pageDTO.getFields().forEach((k, v) -> {
                    if (rejectStrategy.reject(site, processor.parseSourceId(k), rejectStrategyConfig)) {
                        if (Switcher.isDebug()) {
                            tracer.trace("reject:" + k);
                        }
                    } else {
                        page.putField(k, v);
                    }
                });
            }
        }

        @Override
        public Site getSite() {
            return spiderSite;
        }
    }

    public class ProgrammePipeline implements Pipeline {

        @Override
        public void process(ResultItems resultItems, Task task) {
            ProgrammeSourceDO programmeSourceDO = resultItems.get(Constants.SPIDER_PROGRAMME_FIELD_NAME);
            programmeSourceService.insertOrUpdate(programmeSourceDO);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
