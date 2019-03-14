package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.Switcher;
import com.abb.bye.client.domain.*;
import com.abb.bye.client.domain.enums.SiteTag;
import com.abb.bye.client.service.*;
import com.abb.bye.client.spider.SpiderProcessor;
import com.abb.bye.spider.CustomScheduler;
import com.abb.bye.utils.CommonThreadPool;
import com.abb.bye.utils.CommonUtils;
import com.abb.bye.utils.Tracer;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.Downloader;
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
    @Resource
    private Downloader customDownloader;

    @Override
    public ResultDTO<Void> doJob(int site) {
        Tracer tracer = new Tracer("SCHEDULE_TASK").setEntityId(site);
        try {
            ResultDTO<Runnable> resultDTO = createJob(site);
            if (!resultDTO.isSuccess()) {
                tracer.trace("Create job failed:" + resultDTO);
            }
            tracer.trace("begin do job");
            resultDTO.getData().run();
            tracer.trace("job finished");
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            tracer.trace("Create job error:", e);
            return ResultDTO.buildError(e.getMessage());
        }
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
        SpiderConfig spiderConfig = new SpiderConfig();
        CommonUtils.copyFromProperties(spiderConfig, config);
        if (StringUtils.isBlank(config)) {
            tracer.trace("empty config");
            return ResultDTO.buildError("empty spider config");
        }
        if (StringUtils.isBlank(urls)) {
            tracer.trace("empty spider urls");
            return ResultDTO.buildError("empty spider urls");
        }
        if (spiderConfig.getProcessor() == null) {
            tracer.trace("miss processor");
            return ResultDTO.buildError("miss processor");
        }
        tracer.trace("spiderConfig:" + spiderConfig);
        try {
            Class<SpiderProcessor> clazz = (Class<SpiderProcessor>)Class.forName(spiderConfig.getProcessor());
            SpiderProcessor processor = applicationContext.getBean(clazz);
            PageProcessorProxy pageProcessor = new PageProcessorProxy(site, spiderConfig, processor);
            Runnable spider = new SpiderRunner(Spider.create(pageProcessor)
                .setExecutorService(CommonThreadPool.getCommonExecutor())
                .setDownloader(customDownloader)
                .addUrl(StringUtils.split(urls, "\r\n"))
                .setScheduler(new CustomScheduler())
                .addPipeline(new ProgrammePipeline(site))
                .thread(spiderConfig.getThreadCount()), pageProcessor.processor, spiderConfig, site);
            return ResultDTO.buildSuccess(spider);
        } catch (Throwable e) {
            tracer.trace("Error startSpider", true, e);
            return ResultDTO.buildError(e.getMessage());
        }
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
                .setSleepTime(spiderConfig.getSleepTime());
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
                Iterators.removeIf(pageDTO.getTargetRequests().iterator(), url -> {
                    boolean reject = rejectStrategy.reject(site, processor.parseSourceId(url), rejectStrategyConfig);
                    if (reject && Switcher.isDebug()) {
                        tracer.trace("reject:" + url);
                    }
                    return reject;
                });
                page.addTargetRequests(pageDTO.getTargetRequests());
            }
            if (pageDTO.getFields() != null) {
                pageDTO.getFields().forEach((k, v) -> page.putField(k, v));
            }
        }

        @Override
        public Site getSite() {
            return spiderSite;
        }
    }

    public class ProgrammePipeline implements Pipeline {
        private int site;

        private ProgrammePipeline(int site) {
            this.site = site;
        }

        @Override
        public void process(ResultItems resultItems, Task task) {
            ProgrammeSourceDO programmeSourceDO = resultItems.get(Constants.SPIDER_PROGRAMME_FIELD_NAME);
            programmeSourceDO.setSite(site);
            programmeSourceDO.setStatus(ProgrammeBaseDO.STATUS_ENABLE);
            if (programmeSourceDO.getSeconds() == null) {
                programmeSourceDO.setSeconds(0);
            }
            if (programmeSourceDO.getScore() == null) {
                programmeSourceDO.setScore((double)0);
            }
            programmeSourceService.insertOrUpdate(programmeSourceDO);
        }
    }

    @Override
    public void afterPropertiesSet() {
        Tracer tracer = new Tracer("SPIDER_SCHEDULE_REGISTER");
        List<SiteDO> sites = siteService.filter(siteService.listFromDB(), Lists.newArrayList(SiteTag.ENABLE_SPIDER), SiteDO.STATUS_ENABLE);
        for (SiteDO site : sites) {
            String schedule = siteConfigsService.getFromDB(site.getSite(), Constants.SYSTEM_CONFIG_SPIDER_SCHEDULE);
            if (schedule == null) {
                tracer.trace("miss schedule");
                continue;
            }
            schedulerService.register(Constants.SCHEDULE_SPIDER_PREFIX + site.getSite(), () -> doJob(site.getSite()), schedule);
            tracer.trace("register spider-job site:" + site.getSite() + " schedule:" + schedule);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
