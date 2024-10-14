package com.express.activity.repository;


import com.express.activity.domain.WorkflowTask;


import com.express.activity.domain.enumuration.ApprovalType;
import com.express.activity.service.dto.custom.LatestTaskData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the WorkflowTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long>, JpaSpecificationExecutor<WorkflowTask> {

    @Query("SELECT COUNT(*) FROM WorkflowTask wt " +
        " WHERE wt.taskId=:taskId " +
        " AND wt.taskSequence=:taskSequence " +
        " AND wt.workflowProcess.processInstanceId=:processInstanceId " +
        " AND wt.actionStatus IS NULL")
    Long countUncompletedInstanceForSpecificTask(@Param("taskId") String taskId,
                                                 @Param("taskSequence") Integer taskSequence,
                                                 @Param("processInstanceId") String processInstanceId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE workflow_task SET task_complete=true " +
        " FROM workflow_process " +
        " WHERE workflow_process.id=workflow_task.workflow_process_id " +
        " AND workflow_task.task_id=:taskId " +
        " AND workflow_task.task_sequence=:taskSequence " +
        " AND workflow_process.process_instance_id=:processInstanceId", nativeQuery = true)
    void completeProcessTask(@Param("taskId") String taskId,
                             @Param("taskSequence") Integer taskSequence,
                             @Param("processInstanceId") String processInstanceId);

    Optional<WorkflowTask> findByTaskInstanceId(String taskInstanceId);

    Optional<WorkflowTask> findByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(String taskInstanceId);

    Long countByTaskInstanceIdAndActionTimeNullAndTaskCompleteFalse(String taskInstanceId);


    List<WorkflowTask> findByWorkflowProcessProcessInstanceIdAndTaskIdAndActionTimeNullAndTaskCompleteFalse(String processInstanceId, String taskId);

    List<WorkflowTask> findByWorkflowProcessProcessInstanceIdAndTaskIdAndActionTypeAndActionTimeNullAndTaskCompleteFalse(String processInstanceId, String taskId, ApprovalType actionType);

    @Query("SELECT COALESCE(MAX(wt.reassignIndex), 0) FROM WorkflowTask wt WHERE wt.parentTaskInstanceId = :parentTaskInstanceId")
    Integer getMaxIndexForReassignedTask(@Param("parentTaskInstanceId") String parentTaskInstanceId);

    @Query("SELECT wt FROM WorkflowTask wt " +
        "WHERE wt.workflowProcess.processInstanceId = :processInstanceId " +
        "AND wt.taskId = :taskId " +
        "AND wt.taskSequence = :taskSequence " +
        "AND (wt.actionStatus != 'REASSIGNED' OR wt.actionStatus IS NULL)")
    List<WorkflowTask> findByWorkflowProcessProcessInstanceIdAndTaskIdAndTaskSequenceAndActionStatusNotReassigned(@Param("processInstanceId") String processInstanceId,
                                                                                                                  @Param("taskId") String taskId,
                                                                                                                  @Param("taskSequence") Integer taskSequence);

    @Query("SELECT MAX(wt.taskSequence) FROM WorkflowTask wt " +
        " WHERE wt.workflowProcess.processInstanceId = :processInstanceId " +
        " AND wt.taskId = :taskId " +
        " AND wt.actionStatus != 'REASSIGNED'")
    Integer getCurrentTaskSequenceOfTaskInProcessInstance(@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId);

    @Query(value = "SELECT COALESCE(wt.task_sequence, 0) AS taskSequence, COALESCE(wt.index, 0) AS index, wt.category, wt.task_id AS taskId FROM workflow_task wt " +
        "INNER JOIN workflow_process wp ON wt.workflow_process_id = wp.id " +
        "WHERE wp.process_instance_id = :processInstanceId " +
        "GROUP BY wt.category, wt.task_sequence, wt.index, wt.task_id " +
        "ORDER BY wt.task_sequence DESC, wt.index DESC " +
        "LIMIT 1", nativeQuery = true)
    LatestTaskData getLatestTaskData(@Param("processInstanceId") String processInstanceId);

    @Query("SELECT DISTINCT wt.assignee, au.name, au.email FROM WorkflowTask wt " +
        " INNER JOIN AppUser au ON au.id = wt.assignee " +
        " WHERE wt.workflowProcess.processInstanceId = :workflowProcessInstanceId")
    List<Object[]> getCurrentInvolvedUsersInWorkflow(@Param("workflowProcessInstanceId") String workflowProcessInstanceId);

    @Query("SELECT DISTINCT wt.assignee, au.name, au.email FROM WorkflowTask wt " +
        " INNER JOIN AppUser au ON au.id = wt.assignee " +
        " WHERE wt.workflowProcess.processInstanceId = :workflowProcessInstanceId" +
        " AND wt.assignee != :currentApprover")
    List<Object[]> getCurrentInvolvedUsersInWorkflowExceptCurrentApprover(@Param("workflowProcessInstanceId") String workflowProcessInstanceId,
                                                                          @Param("currentApprover") String currentApprover);
}
