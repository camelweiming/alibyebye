package com.abb.flowable.service.impl;

import com.abb.bye.client.domain.ResultDTO;
import com.abb.flowable.domain.*;
import com.abb.flowable.service.Form;
import com.abb.flowable.service.FlowService;
import com.abb.flowable.utils.Constants;
import com.abb.flowable.utils.Converter;
import org.apache.commons.lang3.StringUtils;
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
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class FlowServiceImpl implements FlowService, InitializingBean, ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(FlowServiceImpl.class);
    private volatile ApplicationContext context;
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
    public Form getFrom(String formKey) {
        try {
            return (Form)context.getBean(formKey);
        } catch (Throwable e) {
            logger.error("Error load fromKey:" + formKey, e);
            return null;
        }
    }

    @Override
    public ResultDTO<TaskDTO> getTask(String taskId, Options options) {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return ResultDTO.buildSuccess(null);
            }
            TaskDTO flowTaskDTO = Converter.convert(task);
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
    public ResultDTO<List<TaskDTO>> query(TaskQuery query) {
        if (null == query.getType()) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss type");
        }
        try {
            if (query.getType() == TaskQuery.TYPE.WAITING_PROCESS) {
                return createTaskQuery(query);
            } else if (query.getType() == TaskQuery.TYPE.INITIATE) {
                return createHistoricProcessInstanceQuery(query);
            } else if (query.getType() == TaskQuery.TYPE.PROCESSED) {
                return createHistoricTaskInstanceQuery(query);
            }
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss type");
        } catch (Throwable e) {
            logger.error("Error query:" + query, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    public ResultDTO<List<TaskDTO>> createHistoricTaskInstanceQuery(TaskQuery query) {
        int total = 0;
        HistoricTaskInstanceQuery q = historyService.createHistoricTaskInstanceQuery().taskAssignee(String.valueOf(query.getUserId()));
        if (query.isNeedTotal()) {
            total = (int)q.count();
        }
        List<TaskDTO> list = new ArrayList<>();
        List<HistoricTaskInstance> tasks = q.listPage(query.getStart(), query.getLimit());
        tasks.forEach(task -> {
            TaskDTO taskDTO = Converter.convert(task);
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

    public ResultDTO<List<TaskDTO>> createHistoricProcessInstanceQuery(TaskQuery query) {
        int total = 0;
        HistoricProcessInstanceQuery q = historyService.createHistoricProcessInstanceQuery().startedBy(query.getUserId());
        if (query.isNeedTotal()) {
            total = (int)q.count();
        }
        List<HistoricProcessInstance> tasks = q.listPage(query.getStart(), query.getLimit());
        List<TaskDTO> list = new ArrayList<>();
        tasks.forEach(task -> {
            TaskDTO flowTaskDTO = Converter.convert(task);
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

    public ResultDTO<List<TaskDTO>> createTaskQuery(TaskQuery query) {
        int total = 0;
        org.flowable.task.api.TaskQuery q = taskService.createTaskQuery().taskCandidateOrAssigned(query.getUserId());
        if (query.isNeedTotal()) {
            total = (int)q.count();
        }
        List<Task> tasks = q.listPage(query.getStart(), query.getLimit());
        List<TaskDTO> list = new ArrayList<>();
        tasks.forEach(task -> {
            TaskDTO taskDTO = Converter.convert(task);
            if (query.isWithVariables()) {
                Converter.setVariables(taskDTO, taskService.getVariables(task.getId()));
            }
            list.add(taskDTO);
        });
        return ResultDTO.buildSuccess(list, total);
    }

    @Override
    public ResultDTO<ProcessInstanceDTO> submitProcessor(String processDefinitionKey, SubmitDTO flowSubmitDTO) {
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
    public ResultDTO<Void> complete(String taskId, CompleteDTO completeDTO) {
        if (completeDTO.getUserId() == null || completeDTO.getUserName() == null) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss user");
        }
        if (completeDTO.getAssignee() != null && completeDTO.getAssigneeName() == null) {
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, "miss assignee");
        }
        try {
            Map<String, Object> variables = completeDTO.getVariables() == null ? new HashMap<>(8) : completeDTO.getVariables();
            Map<String, Object> taskVariables = completeDTO.getTaskVariables() == null ? new HashMap<>(8) : completeDTO.getTaskVariables();
            if (completeDTO.getAssignee() != null) {
                variables.put(Constants.TASK_ASSIGNEE, completeDTO.getAssignee());
                variables.put(Constants.TASK_ASSIGNEE_NAME, completeDTO.getAssigneeName());
            }
            if (completeDTO.getSkip() != null && completeDTO.getSkip()) {
                variables.put(Constants.TASK_SKIP, true);
                variables.put(Constants.TASK_SKIP_ENABLE, true);
            }
            if (completeDTO.getPass() != null) {
                variables.put(Constants.TASK_PASS, true);
            }
            if (completeDTO.getVariables() != null) {
                variables.putAll(completeDTO.getVariables());
            }
            taskVariables.putAll(variables);
            taskVariables.put(Constants.TASK_USER_ID, completeDTO.getUserId());
            taskVariables.put(Constants.TASK_USER_NAME, completeDTO.getUserName());
            taskVariables.put(Constants.TASK_ASSIGNEE, completeDTO.getUserId());
            taskVariables.put(Constants.TASK_ASSIGNEE_NAME, completeDTO.getUserName());
            taskService.setVariablesLocal(taskId, taskVariables);
            taskService.complete(taskId, variables);
            return ResultDTO.buildSuccess(null);
        } catch (Throwable e) {
            logger.error("Error complete taskId:" + taskId + " " + completeDTO, e);
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
    public ResultDTO<List<ProcessNodeDTO>> getByInstanceId(String processInstanceId, Options options) {
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
                    if (options.isWithVariables() && (node.isStartEvent() || node.getActivityType().equals("userTask"))) {
                        String formKey = node.isStartEvent() ? getStartFormKey(node.getProcessDefinitionId()).getData() : getFormKey(node.getProcessDefinitionId(), node.getActivityId()).getData();
                        node.setFormKey(formKey);
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

    private String getTaskDefinitionKey(String processDefinitionId) {
        int flag = processDefinitionId.indexOf(":");
        if (flag > 0) {
            processDefinitionId = StringUtils.substring(processDefinitionId, 0, flag);
        }
        return processDefinitionId;
    }

    @Override
    public ResultDTO<String> getStartFormKey(String processKey) {
        try {
            processKey = getTaskDefinitionKey(processKey);
            String processDefinitionId = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult().getId();
            String formKey = processEngineConfiguration.getFormService().getStartFormKey(processDefinitionId);
            return ResultDTO.buildSuccess(formKey);
        } catch (Throwable e) {
            logger.error("Error getFormKey:" + processKey, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDTO<String> getFormKey(String processDefinitionId, String taskDefinitionKey) {
        try {
            if (taskDefinitionKey == null) {
                taskDefinitionKey = getTaskDefinitionKey(processDefinitionId);
            }
            String formKey = processEngineConfiguration.getFormService().getTaskFormKey(processDefinitionId, taskDefinitionKey);
            return ResultDTO.buildSuccess(formKey);
        } catch (Throwable e) {
            logger.error("Error getFormKey:" + processDefinitionId, e);
            return ResultDTO.buildError(ResultDTO.ERROR_CODE_SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
