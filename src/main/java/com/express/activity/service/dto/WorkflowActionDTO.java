package com.express.activity.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * A DTO for the {@link net.ccc.apps.core.domain.WorkflowAction} entity.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkflowActionDTO extends AbstractAuditingEntityDTO implements Serializable {

    private String action;

    private String description;

    private int order;

}
