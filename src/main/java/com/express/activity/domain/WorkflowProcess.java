package com.express.activity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A WorkflowProcess.
 */
@Entity
@Table(name = "workflow_process")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(exclude = {"workflowTasks"}, callSuper = false)
@ToString(exclude = {"workflowTasks"})
public class WorkflowProcess extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String formType;

    private Long formId;

    private String revision;

    private String category;

    private Instant initTime;

    private Long initiatedBy;

    private String processInstanceId;

    private String processName;

    private String status;

    private Boolean complete;

    private Instant completeTime;

    @OneToMany(mappedBy = "workflowProcess", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "workflowProcess" }, allowSetters = true)
    @OrderBy("taskSequence, index, reassignIndex")
    private List<WorkflowTask> workflowTasks = new ArrayList<>();

}
