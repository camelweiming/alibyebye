package com.abb.bye.service;

import com.abb.bye.Constants;
import com.abb.bye.client.domain.*;
import com.abb.bye.client.service.FlowService;
import com.abb.bye.flowable.form.CustomFormTypes;
import com.abb.bye.utils.Converter;
import org.flowable.engine.*;
import org.flowable.engine.common.impl.identity.Authentication;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
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
            FlowTaskDTO flowTaskDTO = Converter.convert(task);
            if (options.isWithVariables()) {
                Map<String, Object> variables = taskService.getVariables(taskId);
                Converter.setVariables(flowTaskDTO, variables);
            }
            return ResultDTO.buildSuccess(flowTaskDTO);
        } catch (Throwable e) {
            logger.error("Error getTask:" + taskId, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<List<FlowTaskDTO>> query(FlowTaskQuery query) {
        if (null == query.getType()) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss type");
        }
        try {
            if (query.getType() == FlowTaskQuery.TYPE.WAITING_PROCESS) {
                return createTaskQuery(query);
            } else if (query.getType() == FlowTaskQuery.TYPE.INITIATE) {
                return createHistoricProcessInstanceQuery(query);
            } else if (query.getType() == FlowTaskQuery.TYPE.PROCESSED) {
                return createHistoricTaskInstanceQuery(query);
            }
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss type");
        } catch (Throwable e) {
            logger.error("Error query:" + query, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    public ResultDTO<List<FlowTaskDTO>> createHistoricTaskInstanceQuery(FlowTaskQuery query) {
        int total = 0;
        HistoricTaskInstanceQuery q = historyService.createHistoricTaskInstanceQuery().taskAssignee(String.valueOf(query.getUserId()));
        if (query.isNeedTotal()) {
            total = (int)q.count();
        }
        List<FlowTaskDTO> list = new ArrayList<>();
        List<HistoricTaskInstance> tasks = q.listPage(query.getStart(), query.getLimit());
        tasks.forEach(task -> {
            FlowTaskDTO taskDTO = Converter.convert(task);
            if (query.isWithVariables()) {
                List<HistoricVariableInstance> histories = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
                Map<String, Object> variables = new HashMap<>(8);
                histories.forEach(his -> variables.put(his.getVariableName(), his.getValue()));
                Converter.setVariables(taskDTO, variables);
            }
            list.add(taskDTO);
        });
        return ResultDTO.buildSuccess(list, total);
    }

    public ResultDTO<List<FlowTaskDTO>> createHistoricProcessInstanceQuery(FlowTaskQuery query) {
        int total = 0;
        HistoricProcessInstanceQuery q = historyService.createHistoricProcessInstanceQuery().startedBy(query.getUserId());
        if (query.isNeedTotal()) {
            total = (int)q.count();
        }
        List<HistoricProcessInstance> tasks = q.listPage(query.getStart(), query.getLimit());
        List<FlowTaskDTO> list = new ArrayList<>();
        tasks.forEach(task -> {
            FlowTaskDTO flowTaskDTO = Converter.convert(task);
            if (query.isWithVariables()) {
                List<HistoricVariableInstance> histories = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getId()).list();
                Map<String, Object> variables = new HashMap<>(8);
                histories.forEach(his -> variables.put(his.getVariableName(), his.getValue()));
                Converter.setVariables(flowTaskDTO, variables);
            }
            list.add(flowTaskDTO);
        });
        return ResultDTO.buildSuccess(list, total);
    }

    public ResultDTO<List<FlowTaskDTO>> createTaskQuery(FlowTaskQuery query) {
        int total = 0;
        TaskQuery q = taskService.createTaskQuery().taskCandidateOrAssigned(query.getUserId());
        if (query.isNeedTotal()) {
            total = (int)q.count();
        }
        List<Task> tasks = q.listPage(query.getStart(), query.getLimit());
        List<FlowTaskDTO> list = new ArrayList<>();
        tasks.forEach(task -> {
            FlowTaskDTO taskDTO = Converter.convert(task);
            if (query.isWithVariables()) {
                Converter.setVariables(taskDTO, taskService.getVariables(task.getId()));
            }
            list.add(taskDTO);
        });
        return ResultDTO.buildSuccess(list, total);
    }

    @Override
    public ResultDTO<ProcessInstanceDTO> submitProcessor(String processDefinitionKey, FlowSubmitDTO flowSubmitDTO) {
        if (flowSubmitDTO.getUserId() == null || flowSubmitDTO.getUserName() == null) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss user");
        }
        if (flowSubmitDTO.getAssignee() != null && flowSubmitDTO.getAssigneeName() == null) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss assignee");
        }
        try {
            Map<String, Object> variables = new HashMap<>(8);
            variables.put(Constants.TASK_USER_ID, flowSubmitDTO.getUserId());
            variables.put(Constants.TASK_ASSIGNEE, flowSubmitDTO.getAssignee());
            variables.put(Constants.TASK_DESCRIPTION, flowSubmitDTO.getDescription());
            variables.put(Constants.TASK_ASSIGNEE_NAME, flowSubmitDTO.getAssigneeName());
            variables.put(Constants.TASK_USER_NAME, flowSubmitDTO.getUserName());
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
    public ResultDTO<Void> complete(String taskId, FlowCompleteDTO flowCompleteDTO) {
        if (flowCompleteDTO.getUserId() == null || flowCompleteDTO.getUserName() == null) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss user");
        }
        if (flowCompleteDTO.getAssignee() != null && flowCompleteDTO.getAssigneeName() == null) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss assignee");
        }
        try {
            Map<String, Object> variables = flowCompleteDTO.getTaskVariables() == null ? new HashMap<>(8) : flowCompleteDTO.getVariables();
            Map<String, Object> taskVariables = flowCompleteDTO.getTaskVariables() == null ? new HashMap<>(8) : flowCompleteDTO.getTaskVariables();
            if (flowCompleteDTO.getAssignee() != null) {
                variables.put(Constants.TASK_ASSIGNEE, flowCompleteDTO.getAssignee());
                variables.put(Constants.TASK_ASSIGNEE_NAME, flowCompleteDTO.getAssigneeName());
            }
            if (flowCompleteDTO.getSkip() != null && flowCompleteDTO.getSkip()) {
                variables.put(Constants.TASK_SKIP, true);
                variables.put(Constants.TASK_SKIP_ENABLE, true);
            }
            if (flowCompleteDTO.getPass() != null) {
                variables.put(Constants.TASK_PASS, true);
            }
            if (flowCompleteDTO.getVariables() != null) {
                variables.putAll(flowCompleteDTO.getVariables());
            }
            taskVariables.putAll(variables);
            taskVariables.put(Constants.TASK_USER_ID, flowCompleteDTO.getUserId());
            taskVariables.put(Constants.TASK_USER_NAME, flowCompleteDTO.getUserName());
            taskVariables.put(Constants.TASK_ASSIGNEE, flowCompleteDTO.getUserId());
            taskVariables.put(Constants.TASK_ASSIGNEE_NAME, flowCompleteDTO.getUserName());
            taskService.setVariablesLocal(taskId, taskVariables);
            taskService.complete(taskId, variables);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            logger.error("Error complete taskId:" + taskId + " " + flowCompleteDTO, e);
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
            List<HistoricVariableInstance> histories = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
            Map<String, Map<String, Object>> mapping = new HashMap<>();
            histories.forEach(historicVariableInstance -> {
                String taskId = historicVariableInstance.getTaskId() == null ? "#" : historicVariableInstance.getTaskId();
                Map<String, Object> variables = mapping.get(taskId);
                if (variables == null) {
                    variables = new HashMap<>(8);
                    mapping.put(taskId, variables);
                }
                variables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
            });
            List<ProcessNodeDTO> list = new ArrayList<>(tasks.size());
            int i = 0;
            for (HistoricActivityInstance t : tasks) {
                ProcessNodeDTO node = Converter.convert(t);
                node.setStartEvent(i++ == 0);
                if (options.isWithVariables()) {
                    Map<String, Object> variables = new HashMap<>(16);
                    Map<String, Object> processVariables = mapping.get("#");
                    if (processVariables != null) {
                        variables.putAll(processVariables);
                    }
                    if (options.isReplaceLocalVariables() && t.getTaskId() != null) {
                        Map<String, Object> taskVariables = mapping.get(t.getTaskId());
                        if (taskVariables != null) {
                            variables.putAll(taskVariables);
                        }
                    }
                    Converter.setVariables(node, variables);
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
