package com.abb.flowable.tests;

import org.flowable.engine.*;
import org.flowable.engine.common.impl.history.HistoryLevel;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.ProcessEngineImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.impl.test.HistoryTestHelper;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.BeforeClass;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class BaseTest {
    private static String RESOURCES = "/flowable/*.xml";
    protected static RuntimeService runtimeService;
    protected static ProcessEngine processEngine;
    protected static ProcessEngineConfigurationImpl processEngineConfiguration;
    protected static HistoryService historyService;
    protected static TaskService taskService;
    protected static RepositoryService repositoryService;

    @BeforeClass
    public static void init() throws IOException {
        StandaloneProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration();
        cfg.setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1");
        cfg.setJdbcUsername("sa");
        cfg.setJdbcPassword("");
        cfg.setJdbcDriver("org.h2.Driver");
        cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        cfg.setAsyncExecutorActivate(true);

        processEngine = cfg.buildProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        Resource[] resources = findAllClassPathResources(RESOURCES);
        for (Resource resource : resources) {
            System.out.println("load process file:" + resource.getFile());
            builder.addInputStream(resource.getFilename(), resource.getInputStream());
        }
        builder.deploy();
        runtimeService = processEngine.getRuntimeService();
        processEngineConfiguration = ((ProcessEngineImpl)processEngine).getProcessEngineConfiguration();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
    }

    private static Resource[] findAllClassPathResources(String location) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources(location);
    }

    public void assertProcessEnded(final String processInstanceId) {
        assertProcessEnded(processInstanceId, 10000);
    }

    public void assertProcessEnded(final String processInstanceId, long timeout) {
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (processInstance != null) {
            throw new AssertionError("Expected finished process instance '" + processInstanceId + "' but it was still in the db");
        }

        // Verify historical data if end times are correctly set
        if (HistoryTestHelper.isHistoryLevelAtLeast(HistoryLevel.AUDIT, processEngineConfiguration, timeout)) {

            // process instance
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
            assertEquals(processInstanceId, historicProcessInstance.getId());
            assertNotNull("Historic process instance has no start time", historicProcessInstance.getStartTime());
            assertNotNull("Historic process instance has no end time", historicProcessInstance.getEndTime());

            // tasks
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).list();
            if (historicTaskInstances != null && historicTaskInstances.size() > 0) {
                for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
                    assertEquals(processInstanceId, historicTaskInstance.getProcessInstanceId());
                    assertNotNull("Historic task " + historicTaskInstance.getTaskDefinitionKey() + " has no start time", historicTaskInstance.getStartTime());
                    assertNotNull("Historic task " + historicTaskInstance.getTaskDefinitionKey() + " has no end time", historicTaskInstance.getEndTime());
                }
            }

            // activities
            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).list();
            if (historicActivityInstances != null && historicActivityInstances.size() > 0) {
                for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                    assertEquals(processInstanceId, historicActivityInstance.getProcessInstanceId());
                    assertNotNull(historicActivityInstance.getId() + " Historic activity instance '" + historicActivityInstance.getActivityId() + "' has no start time",
                        historicActivityInstance.getStartTime());
                    assertNotNull(historicActivityInstance.getId() + " Historic activity instance '" + historicActivityInstance.getActivityId() + "' has no end time",
                        historicActivityInstance.getEndTime());
                }
            }
        }

        // runtime activities
        //assertEquals(0L, runtimeService.createActivityInstanceQuery().count());
    }
}
