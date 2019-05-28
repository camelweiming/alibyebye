package com.abb.bye.service;

import com.abb.flowable.ProcessEngineRunner;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
@Service
public class FlowService implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(ProcessEngineRunner.class);
    @Resource
    private DataSource dataSource;
    private static String RESOURCES = "/flowable/*.xml";

    @Override
    public void afterPropertiesSet() throws Exception {
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setAsyncExecutorActivate(true);
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        org.springframework.core.io.Resource[] resources = findAllClassPathResources(RESOURCES);
        for (org.springframework.core.io.Resource resource : resources) {
            logger.info("load process file:" + resource.getFile());
            builder.addInputStream(resource.getFilename(), resource.getInputStream());
        }
        builder.deploy();
        logger.info("processEngine init finished");
    }

    private static org.springframework.core.io.Resource[] findAllClassPathResources(String location) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources(location);
    }
}
