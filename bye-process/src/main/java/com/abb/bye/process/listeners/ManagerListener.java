package com.abb.bye.process.listeners;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/23
 */
public class ManagerListener implements TaskListener {

    @Override
    public void notify(DelegateTask task) {
        System.out.println("Manager!!!!!!!!!!!!!!!");
        //ProcessEngines.getDefaultProcessEngine().getTaskService().complete();
        //ProcessRuntime processRuntime = SpringCtx.getBean(ProcessRuntime.class);
        //TaskRuntime taskRuntime = SpringCtx.getBean(TaskRuntime.class);

        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("admin").list();
        tasks.forEach(t -> {
            System.out.println(t);
        });

    }
}
