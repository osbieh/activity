package com.express.activity.domain;

import com.express.activity.domain.enumuration.ActionStatus;
import com.express.activity.domain.enumuration.ApprovalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import java.io.Serializable;
import java.time.Instant;

/**
 * A WorkflowTask.
 */
@Entity
@Table(name = "workflow_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(exclude = {"workflowProcess", "comment"}, callSuper = false)
@ToString(exclude = {"workflowProcess", "comment"})
public class WorkflowTask extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant initTime;

    private String taskId;

    private String taskInstanceId;

    private String taskName;

    private Integer taskSequence;

    private Integer index;

    private Long assignee;

    private Boolean actionRequired;

    @Enumerated(EnumType.STRING)
    private ApprovalType actionType;

    private Instant actionTime;

    @Enumerated(EnumType.STRING)
    private ActionStatus actionStatus;

    private Boolean taskComplete;

    @ManyToOne
    @JsonIgnoreProperties(value = { "workflowTasks" }, allowSetters = true)
    private WorkflowProcess workflowProcess;

    @OneToOne(mappedBy = "workflowTask", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "workflowTask", "expenditure" }, allowSetters = true)
    private Comment comment;

    private Boolean reassigned;

    private String parentTaskInstanceId;

    private Integer reassignIndex;

    private String actExecutionId;

    private String actAssigneeVariableName;

    private String category;

}
