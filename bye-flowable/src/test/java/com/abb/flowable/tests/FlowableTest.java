package com.abb.flowable.tests;

import com.abb.flowable.domain.Options;
import com.abb.flowable.domain.ProcessNodeDTO;
import com.abb.flowable.service.FlowService;
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
    public void getGetTask() {
        flowService.getTask("1127532", new Options().setWithVariables(true));
    }

    @Test
    public void testGet() {
        List<ProcessNodeDTO> list = flowService.getByInstanceId("762505", new Options().setWithVariables(true)).getData();
        list.forEach(l -> {
            System.out.println(l);
        });
    }

    @Test
    public void testLoadKey() {
        String key = flowService.getFormKey("holidayRequest:374:965004", "usertask2").getData();
        System.out.println("startKey:" + key);
        key = flowService.getStartFormKey("holidayRequest:374:965004").getData();
        System.out.println("taskKey:" + key);
    }

    @Test
    public void testLoadKey2() {
        String key = flowService.getStartFormKey("holidayRequest:374:965004").getData();
        System.out.println(key);
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
