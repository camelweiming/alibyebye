package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.*;
import com.abb.bye.client.service.FlowService;
import com.abb.bye.client.service.UserService;
import com.abb.bye.flowable.form.CustomFormTypes;
import com.abb.bye.utils.Converter;
import org.flowable.engine.*;
import org.flowable.engine.common.impl.identity.Authentication;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cenpeng.lwm
 * @since 2019/5/25
 */
@Service
public class FlowServiceImpl implements FlowService, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(FlowServiceImpl.class);
    @Resource
    private DataSource dataSource;
    @Resource
    private UserService userService;

    private RuntimeService runtimeService;
    private ProcessEngine processEngine;
    private ProcessEngineConfigurationImpl processEngineConfiguration;
    private HistoryService historyService;
    private TaskService taskService;
    private RepositoryService repositoryService;
    private static String RESOURCES = "/flowable/*.xml";

    @Override
    public void afterPropertiesSet() throws Exception {
        processEngineConfiguration = new StandaloneProcessEngineConfiguration();
        processEngineConfiguration.setDataSource(dataSource);
        processEngineConfiguration.setAsyncExecutorActivate(true);
        processEngineConfiguration.setFormTypes(new CustomFormTypes());
        processEngine = processEngineConfiguration.buildProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        DeploymentBuilder builder = repositoryService.createDeployment();
        org.springframework.core.io.Resource[] resources = findAllClassPathResources(RESOURCES);
        for (org.springframework.core.io.Resource resource : resources) {
            logger.info("load process file:" + resource.getFile());
            builder.addInputStream(resource.getFilename(), resource.getInputStream());
        }
        builder.deploy();
        logger.info("processEngine init finished");
    }

    private static org.springframework.core.io.Resource[] findAllClassPathResources(String location) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources(location);
    }

    @Override
    public ResultDTO<FlowTaskDTO> getTask(String taskId, FlowOptions options) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return ResultDTO.buildSuccess(null);
            }
            FlowTaskDTO flowTaskDTO = new FlowTaskDTO();
            flowTaskDTO.setAssignee(task.getAssignee());
            if (options.isWithVariables()) {
                Map<String, Object> variables = taskService.getVariables(taskId);
                Converter.setVariables(flowTaskDTO, variables);
            }
            if (options.isWithAssigneeInfo()) {
                flowTaskDTO.setAssigneeInfo(userService.getById(Long.valueOf(flowTaskDTO.getAssignee()), new UserOptions()).getData());
            }
            return ResultDTO.buildSuccess(flowTaskDTO);
        } catch (Throwable e) {
            logger.error("Error getTask:" + taskId, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<ProcessInstanceDTO> submitProcessor(String processDefinitionKey, FlowSubmitDTO flowSubmitDTO) {
        if (flowSubmitDTO.getUserId() == null || flowSubmitDTO.getAssignee() == null || processDefinitionKey == null) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "param error");
        }
        try {
            Map<String, Object> variables = new HashMap<>(8);
            variables.put(Constants.TASK_USER_ID, flowSubmitDTO.getUserId());
            variables.put(Constants.TASK_ASSIGNEE, flowSubmitDTO.getAssignee());
            variables.put(Constants.TASK_DESCRIPTION, flowSubmitDTO.getDescription());
            if (flowSubmitDTO.getUserName() == null) {
                UserDTO user = userService.getById(flowSubmitDTO.getUserId(), new UserOptions()).getData();
                if (user == null) {
                    return ResultDTO.buildError(ResultDTO.ERROR_CODE_USER_NOT_FOUND, "user not found");
                }
                variables.put(Constants.TASK_USER_NAME, user.getUserName());
            } else {
                variables.put(Constants.TASK_USER_NAME, flowSubmitDTO.getUserName());
            }
            Authentication.setAuthenticatedUserId("" + flowSubmitDTO.getUserId());
            if (flowSubmitDTO.getSkip() != null && flowSubmitDTO.getSkip()) {
                variables.put(Constants.TASK_SKIP, true);
                variables.put(Constants.TASK_SKIP_ENABLE, true);
            }
            if (flowSubmitDTO.getPass() != null) {
                variables.put(Constants.TASK_PASS, true);
            }
            if (flowSubmitDTO.getVariables() != null) {
                variables.putAll(flowSubmitDTO.getVariables());
            }
            variables.put(Constants.TASK_TITLE, flowSubmitDTO.getTitle());
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
            return ResultDTO.buildSuccess(Converter.convert(processInstance));
        } catch (Throwable e) {
            logger.error("Error submitProcessor processDefinitionKey:" + processDefinitionKey + " " + flowSubmitDTO, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<Void> complete(String taskId, FlowSubmitDTO flowSubmitDTO) {
        try {
            Map<String, Object> variables = new HashMap<>(8);
            variables.put(Constants.TASK_ASSIGNEE, flowSubmitDTO.getAssignee());
            if (flowSubmitDTO.getSkip() != null && flowSubmitDTO.getSkip()) {
                variables.put(Constants.TASK_SKIP, true);
                variables.put(Constants.TASK_SKIP_ENABLE, true);
            }
            if (flowSubmitDTO.getPass() != null) {
                variables.put(Constants.TASK_PASS, true);
            }
            if (flowSubmitDTO.getVariables() != null) {
                variables.putAll(flowSubmitDTO.getVariables());
            }
            taskService.complete(taskId, variables);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            logger.error("Error complete taskId:" + taskId + " " + flowSubmitDTO, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * select RES.* from ACT_HI_ACTINST RES WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     * <p>
     * select RES.* from ACT_HI_VARINST RES WHERE RES.PROC_INST_ID_ = ? order by RES.ID_ asc
     *
     * @param processInstanceId
     * @param options
     * @return
     */
    @Override
    public ResultDTO<List<ProcessNodeDTO>> getByInstanceId(String processInstanceId, FlowOptions options) {
        try {
            List<HistoricActivityInstance> tasks = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            List<ProcessNodeDTO> list = new ArrayList<>(tasks.size());
            int i = 0;
            for (HistoricActivityInstance t : tasks) {
                ProcessNodeDTO node = Converter.convert(t);
                node.setStartEvent(i++ == 0);
                if (options.isWithVariables()) {
                    List<HistoricVariableInstance> histories = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
                    Map<String, Object> variables = new HashMap<>(16);
                    histories.forEach(h -> variables.put(h.getVariableName(), h.getValue()));
                    Converter.setVariables(node, variables);
                }
                if (options.isWithAssigneeInfo() && node.getAssignee() != null) {
                    node.setAssigneeInfo(userService.getById(Long.valueOf(node.getAssignee()), new UserOptions()).getData());
                }
                list.add(node);
            }
            return ResultDTO.buildSuccess(list);
        } catch (Throwable e) {
            logger.error("Error getByInstanceId:" + processInstanceId, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }
}
