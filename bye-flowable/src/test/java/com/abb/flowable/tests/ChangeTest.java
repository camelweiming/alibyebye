package com.abb.flowable.tests;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class ChangeTest extends BaseTest {

    @Test
    public void test() throws InterruptedException, IOException {
        // 启动2个流程实例
        ProcessInstance pi1 = runtimeService.startProcessInstanceByKey("sbProcess");
        ProcessInstance pi2 = runtimeService.startProcessInstanceByKey("sbProcess");
        // 查找第一个流程实例中签订合同的任务
        Task pi1Task = taskService.createTaskQuery().processInstanceId(pi1.getId()).singleResult();
        taskService.complete(pi1Task.getId());
        // 查找第二个流程实例中签订合同的任务
        Task pi2Task = taskService.createTaskQuery().processInstanceId(pi2.getId()).singleResult();
        taskService.complete(pi2Task.getId());
        // 此时执行流到达确认合同任务，发送一次信号
        runtimeService.signalEventReceived("contactChangeSignal");
        // 查询全部的任务
        List<Task> tasks = taskService.createTaskQuery().list();
        // 输出结果
        for (Task task : tasks) {
            System.out.println(task.getProcessInstanceId() + "---" + task.getName());
        }
    }
}
