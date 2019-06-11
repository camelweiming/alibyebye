package com.abb.bye.test.service;

import com.abb.bye.client.domain.FlowOptions;
import com.abb.bye.client.domain.ProcessNodeDTO;
import com.abb.bye.client.service.FlowService;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/6/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class FlowableTest {
    @Resource
    FlowService flowService;

    @Test
    public void getMyTask() {
        String processInstanceId = "695021";
        String definitionId = "holidayRequest:266:695004";
        Long loginUserId = 75001L;
        List<HistoricProcessInstance> tasks = ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().startedBy("" + loginUserId).list();
        tasks.forEach(t -> {
            System.out.println(t);
        });
    }

    /**
     * select distinct RES.* from ACT_HI_TASKINST RES WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     */
    @Test
    public void getHist() {
        List<HistoricTaskInstance> tasks = ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().processInstanceId("" + 695021).list();
        for (HistoricTaskInstance task : tasks) {
            System.out.println(task);
        }
    }

    @Test
    public void getFinished() {
        List<HistoricActivityInstance> tasks = ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricActivityInstanceQuery().processInstanceId("735005").list();
        for (HistoricActivityInstance task : tasks) {
            List<HistoricVariableInstance> histories = ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricVariableInstanceQuery().processInstanceId("735005").list();
            System.out.println(task);
        }
    }

    @Test
    public void testGet() {
        List<ProcessNodeDTO> list = flowService.getByInstanceId("762505", new FlowOptions().setWithVariables(true)).getData();
        list.forEach(l -> {
            System.out.println(l);
        });
    }

    @Test
    public void clear() {
        List<HistoricTaskInstance> task = ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().list();
        task.forEach(t -> {
            try {
                ProcessEngines.getDefaultProcessEngine().getHistoryService().deleteHistoricProcessInstance(t.getProcessInstanceId());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

        List<HistoricProcessInstance> instances = ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().list();
        instances.forEach(instance -> {
            try {
                ProcessEngines.getDefaultProcessEngine().getHistoryService().deleteHistoricProcessInstance(instance.getId());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
