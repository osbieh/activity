package com.express.activity.service.dto;

import com.express.activity.domain.enumuration.ActionStatus;
import com.express.activity.domain.enumuration.ApprovalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;



import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link com.express.activity.domain.WorkflowTask} entity.
 */
@Data
@EqualsAndHashCode(exclude = {"workflowProcess", "comment"}, callSuper = false)
@ToString(exclude = {"workflowProcess", "comment"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTaskDTO extends AbstractAuditingEntityDTO implements Serializable {

    private Long id;

    private Instant initTime;

    private String taskId;

    private String taskInstanceId;

    private String taskName;

    private Integer taskSequence;

    private Integer index;

    private Long assignee;

    private Boolean actionRequired;

    private ApprovalType actionType;

    private Instant actionTime;

    private ActionStatus actionStatus;

    private Boolean taskComplete;

    @JsonIgnoreProperties(value = { "workflowTasks" }, allowSetters = true)
    private WorkflowProcessDTO workflowProcess;

    @JsonIgnoreProperties(value = { "workflowTask" }, allowSetters = true)
    private CommentDTO comment;

    private Boolean reassigned;

    private String parentTaskInstanceId;

    private Integer reassignIndex;

//    @Builder.Default
//    private Set<WorkflowTaskDTO> reassignedTasks = new HashSet<>();

    private String onBehalfUser;

    private String actExecutionId;

    private String actAssigneeVariableName;

    private String category;

}
