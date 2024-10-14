package com.express.activity.service;

import com.express.activity.domain.WorkflowTask;
import com.express.activity.domain.enumuration.ApprovalType;
import com.express.activity.repository.WorkflowTaskRepository;
import com.express.activity.service.dto.WorkflowTaskDTO;
import com.express.activity.service.dto.custom.LatestTaskData;
import com.express.activity.service.mapper.WorkflowTaskMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link WorkflowTask}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkflowTaskService {

    private final WorkflowTaskRepository workflowTaskRepository;
    private final WorkflowTaskMapper workflowTaskMapper;
    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper;

    /**
     * Save a workflowTask.
     *
     * @param workflowTaskDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkflowTaskDTO save(WorkflowTaskDTO workflowTaskDTO) {
        log.debug("Request to save WorkflowTask : {}", workflowTaskDTO);
        WorkflowTask workflowTask = workflowTaskMapper.toEntity(workflowTaskDTO);
        workflowTask = workflowTaskRepository.save(workflowTask);
        return workflowTaskMapper.toDto(workflowTask);
    }

    public void saveAll(List<WorkflowTaskDTO> workflowTaskDTOList) {
        log.debug("Request to save List of WorkflowTask : {}", workflowTaskDTOList);
        List<WorkflowTask> workflowTask = workflowTaskMapper.toEntity(workflowTaskDTOList);
        workflowTaskRepository.saveAll(workflowTask);
    }

    /**
     * Partially update a workflowTask.
     *
     * @param workflowTaskDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkflowTaskDTO> partialUpdate(WorkflowTaskDTO workflowTaskDTO) {
        log.debug("Request to partially update WorkflowTask : {}", workflowTaskDTO);

        return workflowTaskRepository
            .findById(workflowTaskDTO.getId())
            .map(existingWorkflowTask -> {
                workflowTaskMapper.partialUpdate(existingWorkflowTask, workflowTaskDTO);

                return existingWorkflowTask;
            })
            .map(workflowTaskRepository::save)
            .map(workflowTaskMapper::toDto);
    }

    /**
     * Get all the workflowTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkflowTaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WorkflowTasks");
        return workflowTaskRepository.findAll(pageable).map(workflowTaskMapper::toDto);
    }

    /**
     * Get one workflowTask by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkflowTaskDTO> findOne(Long id) {
        log.debug("Request to get WorkflowTask : {}", id);
        return workflowTaskRepository.findById(id).map(workflowTaskMapper::toDto);
    }

    /**
     * Delete the workflowTask by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete WorkflowTask : {}", id);
        workflowTaskRepository.deleteById(id);
    }

    public HashMap<String, Integer> getSequenceAndIndexForNewTask(String processInstanceId, String taskId, String taskCategory) {
        LatestTaskData latestTaskData = workflowTaskRepository.getLatestTaskData(processInstanceId);
        int taskSequence = 1, index = 1;

        if (latestTaskData != null) {
            Integer previousTaskSequence = latestTaskData.getTaskSequence(), previousIndex = latestTaskData.getIndex();
            String previousCategory = latestTaskData.getCategory(), previousTaskId = latestTaskData.getTaskId();

            if (!(previousTaskSequence.equals(0) && previousIndex.equals(0))) {
                if(taskCategory != null) {
                    if(taskCategory.equals(previousCategory) && taskId.equals(previousTaskId)) {
                        taskSequence = previousTaskSequence;
                        index = previousIndex + 1;
                    }
                    else {
                        taskSequence = previousTaskSequence + 1;
                    }
                }
                else if(taskId.equals(previousTaskId)) {
                    taskSequence = previousTaskSequence;
                    index = previousIndex + 1;
                }
                else {
                    taskSequence = previousTaskSequence + 1;
                }
            }
        }
        HashMap<String, Integer> sequenceAndIndexMap = new HashMap<>();
        sequenceAndIndexMap.put("taskSequence", taskSequence);
        sequenceAndIndexMap.put("index", index);

        return sequenceAndIndexMap;
    }

    public void updateTaskStatusIfCompleted(String taskId, Integer taskSequence, String processInstanceId, Long formId) {
        Long workflowTaskCount = workflowTaskRepository.countUncompletedInstanceForSpecificTask(taskId, taskSequence, processInstanceId);
        if (workflowTaskCount == 0)
            workflowTaskRepository.completeProcessTask(taskId, taskSequence, processInstanceId);
    }

    public WorkflowTaskDTO findByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(String taskInstanceId) {
        return  workflowTaskRepository.findByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(taskInstanceId).map(workflowTaskMapper::toDto).orElseThrow();
    }

    public Long countByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(String taskInstanceId) {
        return  workflowTaskRepository.countByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(taskInstanceId);
    }

    public List<WorkflowTaskDTO> findByWorkflowProcessProcessInstanceIdAndTaskIdAndActionTimeNullAndTaskCompleteFalse(String processInstanceId, String taskId) {
        return workflowTaskRepository.findByWorkflowProcessProcessInstanceIdAndTaskIdAndActionTimeNullAndTaskCompleteFalse(processInstanceId, taskId).stream().map(workflowTaskMapper::toDto).collect(Collectors.toList());
    }

    public List<WorkflowTaskDTO> findByWorkflowProcessProcessInstanceIdAndTaskIdAndActionTypeAndActionTimeNullAndTaskCompleteFalse(String processInstanceId, String taskId, ApprovalType actionType) {
        return workflowTaskRepository.findByWorkflowProcessProcessInstanceIdAndTaskIdAndActionTypeAndActionTimeNullAndTaskCompleteFalse(processInstanceId, taskId, actionType).stream().map(workflowTaskMapper::toDto).collect(Collectors.toList());
    }

    public WorkflowTaskDTO findByTaskInstanceId(String taskInstanceId) {
        return workflowTaskRepository.findByTaskInstanceId(taskInstanceId).map(workflowTaskMapper::toDto).orElseThrow();
    }


    public List<WorkflowTaskDTO> findByWorkflowProcessProcessInstanceIdAndTaskIdAndTaskSequenceAndActionStatusNotReassigned(String processInstanceId, String taskId, Integer taskSequence) {
        List<WorkflowTask> workflowTasks = workflowTaskRepository.findByWorkflowProcessProcessInstanceIdAndTaskIdAndTaskSequenceAndActionStatusNotReassigned(processInstanceId, taskId, taskSequence);
        return workflowTaskMapper.toDto(workflowTasks);
    }

    public Integer getCurrentTaskSequenceOfTaskInProcessInstance(String processInstanceId, String taskId) {
        return workflowTaskRepository.getCurrentTaskSequenceOfTaskInProcessInstance(processInstanceId, taskId);
    }

}
