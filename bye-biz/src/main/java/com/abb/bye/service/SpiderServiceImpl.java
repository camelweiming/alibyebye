package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.ProgrammeSourceDO;
import com.abb.bye.client.domain.ResultDTO;
import com.abb.bye.client.domain.SiteDO;
import com.abb.bye.client.domain.SpiderConfig;
import com.abb.bye.client.domain.enums.SiteTag;
import com.abb.bye.client.service.*;
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
            Spider spider = Spider.create(buildProcessor(spiderConfig, spiderProcessor))
                .setExecutorService(CommonThreadPool.getCommonExecutor())
                .addUrl(urlList.toArray(new String[urlList.size()]))
                .addPipeline(new ProgrammePipeline())
                .thread(spiderConfig.getThreadCount());
            return ResultDTO.buildSuccess(spider);
        } catch (Throwable e) {
            tracer.trace("Error startSpider", true, e);
            return ResultDTO.buildError(e.getMessage());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public PageProcessor buildProcessor(SpiderConfig spiderConfig, String spiderProcessor) throws ClassNotFoundException {
        Site spiderSite = Site.me()
            .setTimeOut(spiderConfig.getTimeOut())
            .setCycleRetryTimes(spiderConfig.getCycleRetryTimes())
            .setRetryTimes(spiderConfig.getRetryTimes())
            .setSleepTime(spiderConfig.getSleepTime())
            .setUserAgent(spiderConfig.getUserAgent() == null ? Constants.SPIDER_DEFAULT_USER_AGENT : spiderConfig.getUserAgent());

        Class<PageProcessor> clazz = (Class<PageProcessor>)Class.forName(spiderProcessor);
        PageProcessor processor = applicationContext.getBean(clazz);
        PageProcessor proxy = new PageProcessor() {
            @Override
            public void process(Page page) {
                processor.process(page);
            }

            @Override
            public Site getSite() {
                return spiderSite;
            }
        };
        return proxy;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<SiteDO> sites = siteService.filter(siteService.listFromDB(), Lists.newArrayList(SiteTag.ENABLE_SPIDER), SiteDO.STATUS_ENABLE);
        sites.forEach(siteDO -> start(siteDO.getSite()));
    }

    public class ProgrammePipeline implements Pipeline {

        @Override
        public void process(ResultItems resultItems, Task task) {
            ProgrammeSourceDO programmeSourceDO = resultItems.get(Constants.SPIDER_PROGRAMME_FIELD_NAME);
            programmeSourceService.insertOrUpdate(programmeSourceDO);
        }
    }
}
