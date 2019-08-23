package com.abb.bye.test.service;

import com.abb.flowable.api.domain.TaskDTO;
import com.abb.flowable.api.domain.TaskQuery;
import com.abb.flowable.api.service.FlowService;
import org.flowable.engine.ProcessEngines;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:/application-context-flowable.xml"})
public class FlowTest {
    @Resource
    private FlowService flowService;

    @Test
    public void test() {
        List<TaskDTO> flows = flowService.query(new TaskQuery().setWithVariables(false).setInitiatorId(75001l).setType(TaskQuery.TYPE.WAITING_PROCESS).setLimit(10).setUserId("270001"))
            .getData();
        flows.forEach(f -> {
            System.out.println(f);
        });
    }

    @Test
    public void test2() {
        HistoricTaskInstanceQuery q = ProcessEngines.getDefaultProcessEngine()
            .getHistoryService()
            .createHistoricTaskInstanceQuery().processVariableValueEqualsIgnoreCase("user_name","camel").taskAssignee("39001").orderByTaskCreateTime().desc();
        List<HistoricTaskInstance> tasks = q.list();
        tasks.forEach(t -> {
            System.out.println(t);
        });

    }
}
