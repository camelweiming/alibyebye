package com.abb.flowable;

import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author cenpeng.lwm
 * @since 2019/5/24
 */
public class ProcessEngineRunner implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(ProcessEngineRunner.class);
    @Resource
    private DataSource dataSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.buildProcessEngine();
        logger.info("processEngine init");
    }
}
