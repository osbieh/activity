package com.express.activity.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for the {@link com.express.activity.domain.WorkflowProcess} entity.
 */
@Data
@EqualsAndHashCode(exclude = {"workflowTasks"}, callSuper = false)
@ToString(exclude = {"workflowTasks"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowProcessDTO extends AbstractAuditingEntityDTO implements Serializable {

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

    @Builder.Default
    @JsonIgnoreProperties(value = { "workflowProcess" }, allowSetters = true)
    private List<WorkflowTaskDTO> workflowTasks = new ArrayList<>();

}
