package com.abb.flowable.tests;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/28
 */
public class BankTest extends BaseTest {

    @Test
    public void test() throws InterruptedException {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("result", false);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("cbProcess", vars);
        System.out.println(processInstance.getId());
        List<Task> tasks = taskService.createTaskQuery().list();
        tasks.forEach(t -> {
            System.out.println(t);
        });
        Thread.sleep(10000);
    }
}
