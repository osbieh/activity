package com.express.activity.service.workflow_engine;

import com.express.activity.config.ZoneConfiguration;
import com.express.activity.domain.enumuration.ActionStatus;
import com.express.activity.domain.enumuration.ApprovalType;
import com.express.activity.service.WorkflowProcessService;
import com.express.activity.service.WorkflowTaskService;
import com.express.activity.service.dto.WorkflowProcessDTO;
import com.express.activity.service.dto.WorkflowTaskDTO;
import com.express.activity.service.workflow_engine.custom_events.ActivitiCustomEventRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ActivitiEventService {
    private final WorkflowTaskService workflowTaskService;
    private final WorkflowProcessService workflowProcessService;
    private final ProcessRuntime processRuntime;
    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final ProcessEngine processEngine;
    private final ActivitiCustomEventRegistry activitiCustomEventRegistry;

    public void processCreatedListener(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        String processInstanceId = execution.getRootProcessInstanceId();
        String processDefinitionName = processRuntime.processDefinition(execution.getProcessDefinitionId()).getName();

        WorkflowProcessDTO workflowProcessDTO = WorkflowProcessDTO.builder()
            .processInstanceId(processInstanceId)
            .processName(processDefinitionName)
            .formType((String) variables.get("formType"))
            .formId(((Number) variables.get("formId")).longValue())
            .initiatedBy(((Number) variables.get("initiatorId")).longValue())
            .initTime(Instant.now())
            .complete(false)
            .completeTime(null)
            .revision(null)
            .category(null)
            .build();

        workflowProcessService.save(workflowProcessDTO);

        // user custom event handler
        activitiCustomEventRegistry.getCustomProcessCreatedEvents().forEach(event -> event.execute(workflowProcessDTO));
    }

    public void processCompletedListener(DelegateExecution execution) {
        String processInstanceId = execution.getRootProcessInstanceId();
        workflowProcessService.completeWorkflowProcess(processInstanceId);

        WorkflowProcessDTO workflowProcessDTO = workflowProcessService.findByProcessInstanceId(processInstanceId).orElseThrow();

        // user custom event handler
        activitiCustomEventRegistry.getCustomProcessCompletedEvents().forEach(event -> event.execute(workflowProcessDTO));
    }

    public void taskCreatedListener(DelegateTask task) {
        String assigneeVariableName = "";
        BpmnModel model = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId());
        List<UserTask> userTasks = new ArrayList<>();
        model.getProcesses().forEach(p -> userTasks.addAll(p.findFlowElementsOfType(UserTask.class)));
        for(UserTask userTask: userTasks) {
            if(userTask.getId().equals(task.getTaskDefinitionKey())) {
                if(userTask.getAssignee().startsWith("${") && userTask.getAssignee().endsWith("}"))
                    assigneeVariableName = userTask.getAssignee().substring(2, userTask.getAssignee().length() - 1);
                else
                    assigneeVariableName = userTask.getAssignee();
                break;
            }
        }
        Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
        String taskCategory = variables.get("taskCategory") != null && !((String) variables.get("taskCategory")).isEmpty() ? (String) variables.get("taskCategory") : null;
        HashMap<String, Integer> sequenceAndIndexForNewTask = workflowTaskService.getSequenceAndIndexForNewTask(task.getProcessInstanceId(), task.getTaskDefinitionKey(), taskCategory);
        Integer taskSequence = sequenceAndIndexForNewTask.get("taskSequence"), index = sequenceAndIndexForNewTask.get("index");

        WorkflowProcessDTO workflowProcessDTO = workflowProcessService.findByProcessInstanceId(task.getProcessInstanceId()).orElseThrow();

        String formKey = task.getFormKey();
        if(formKey != null && (formKey.equals("ANYONE") || formKey.equals("JOINTLY") || formKey.equals("MAJORITY"))) {
            ApprovalType approvalType = Enum.valueOf(ApprovalType.class, formKey);

            WorkflowTaskDTO workflowTaskDTO = WorkflowTaskDTO.builder()
                .initTime(Instant.now())
                .taskId(task.getTaskDefinitionKey())
                .taskInstanceId(task.getId())
                .taskName(task.getName())
                .assignee(Long.valueOf(task.getAssignee()))
                .taskSequence(taskSequence)
                .index(index)
                .actionRequired(true)
                .actionType(approvalType)
                .actionTime(null)
                .actionStatus(null)
                .taskComplete(false)
                .reassigned(false)
                .actExecutionId(task.getExecutionId())
                .actAssigneeVariableName(assigneeVariableName)
                .category(taskCategory)
                .workflowProcess(workflowProcessDTO)
                .build();

            workflowTaskService.save(workflowTaskDTO);

            // user custom event handler
            activitiCustomEventRegistry.getCustomTaskCreatedEvents().forEach(event -> event.execute(workflowTaskDTO));
        }
        else
            throw new RuntimeException("formKey should be ANYONE or JOINTLY or MAJORITY");
    };

    public void taskCompletedListener(DelegateTask task) {
        Map<String, Object> variables = taskService.getVariables(task.getId());

        WorkflowTaskDTO workflowTaskDTO = workflowTaskService.findByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(task.getId());
        workflowTaskDTO.setActionStatus(Enum.valueOf(ActionStatus.class, variables.get("userResponse").toString()));
        workflowTaskDTO.setActionTime(LocalDateTime.now().toInstant(ZoneConfiguration.zoneOffset));
        workflowTaskService.save(workflowTaskDTO);

        workflowTaskService.updateTaskStatusIfCompleted(task.getTaskDefinitionKey(), workflowTaskDTO.getTaskSequence(), workflowTaskDTO.getWorkflowProcess().getProcessInstanceId(), workflowTaskDTO.getWorkflowProcess().getFormId());

        // user custom event handler
        activitiCustomEventRegistry.getCustomTaskCompletedEvents().forEach(event -> event.execute(workflowTaskDTO));
    }

    public void notificationTaskListener(DelegateExecution execution, Long userId) {
        if(userId != 0) {
            String processInstanceId = execution.getParent().getProcessInstanceId();
            ServiceTask serviceTask = (ServiceTask) execution.getCurrentFlowElement();
            Map<String,Object> variables = execution.getVariables();
            String taskCategory = variables.get("taskCategory") != null && !((String) variables.get("taskCategory")).isEmpty() ? (String) variables.get("taskCategory") : null;

            HashMap<String, Integer> sequenceAndIndexForNewTask = workflowTaskService.getSequenceAndIndexForNewTask(processInstanceId, "", null);
            Integer taskSequence = sequenceAndIndexForNewTask.get("taskSequence");
            Integer index = sequenceAndIndexForNewTask.get("index");

            WorkflowProcessDTO workflowProcessDTO = workflowProcessService.findByProcessInstanceId(processInstanceId).orElseThrow();

            WorkflowTaskDTO workflowTaskDTO = WorkflowTaskDTO.builder()
                .initTime(Instant.now())
                .taskId(serviceTask.getId())
                .taskInstanceId(null)
                .taskName(serviceTask.getName())
                .assignee(userId)
                .taskSequence(taskSequence)
                .index(index)
                .actionRequired(false)
                .actionType(null)
                .actionTime(LocalDateTime.now().toInstant(ZoneConfiguration.zoneOffset))
                .actionStatus(ActionStatus.NOTIFIED)
                .taskComplete(true)
                .category(taskCategory)
                .workflowProcess(workflowProcessDTO)
                .build();
            workflowTaskService.save(workflowTaskDTO);

            // user custom event handler
            activitiCustomEventRegistry.getCustomMailTaskEvents().forEach(event -> event.execute(workflowTaskDTO));
        }
    }
}
