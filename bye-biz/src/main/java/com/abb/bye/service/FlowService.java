package com.abb.bye.service;

import com.abb.flowable.ProcessEngineRunner;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
@Service
public class FlowService implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(ProcessEngineRunner.class);
    @Resource
    private DataSource dataSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setAsyncExecutorActivate(true);
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
            .addClasspathResource("flowable/holiday-request.bpmn20.xml")
            .deploy();

        logger.info("processEngine init");
    }
}
