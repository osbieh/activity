package com.express.activity.service.workflow_engine;

import com.express.activity.service.WorkflowTaskService;
import lombok.RequiredArgsConstructor;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.task.Task;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class ActivitiAspect {

    private final WorkflowTaskService workflowTaskService;
    private final TaskService taskService;

    @Before("execution(public void org.activiti.engine.TaskService.complete(..))")
    public void beforeTaskComplete(JoinPoint theJoinPoint) {
        Object[] args = theJoinPoint.getArgs();
        String taskInstanceId = (String) args[0];
        Task task = taskService.createTaskQuery().taskId(taskInstanceId).singleResult();
        Long checkTaskIfExist = workflowTaskService.countByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(taskInstanceId);
        if(checkTaskIfExist == 0 || task == null)
            throw new BpmnError("taskNotFound", "Task not found");
    }
}
