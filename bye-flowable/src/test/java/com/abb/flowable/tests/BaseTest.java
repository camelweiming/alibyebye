package com.abb.flowable.tests;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.junit.BeforeClass;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class BaseTest {
    private static String RESOURCES = "/flowable/*.xml";

    @BeforeClass
    public static void init() throws IOException {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
            .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
            .setJdbcUsername("sa")
            .setJdbcPassword("")
            .setJdbcDriver("org.h2.Driver")
            .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
            .setAsyncExecutorActivate(true);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        Resource[] resources = findAllClassPathResources(RESOURCES);
        for (Resource resource : resources) {
            System.out.println("load process file:" + resource.getFile());
            builder.addInputStream(resource.getFilename(), resource.getInputStream());
        }
        builder.deploy();
    }

    private static Resource[] findAllClassPathResources(String location) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources(location);
    }
}
