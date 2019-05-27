package com.abb.bye.web;

import com.abb.bye.client.domain.UserDTO;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cenpeng.lwm
 * @since 2019/5/27
 */
@Controller
public class TaskController extends BaseController {

    @RequestMapping(value = "task_list.htm", method = RequestMethod.GET)
    String taskList(HttpServletRequest request, Model model) {
        String vm = "task_list";
        UserDTO userDTO = getLoginUser(request);
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(String.valueOf(userDTO.getUserId())).list();
        model.addAttribute("tasks", tasks);
        return vm;
    }
}
