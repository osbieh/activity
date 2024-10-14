package com.express.activity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import java.io.Serializable;

/**
 * A WorkflowAction.
 */
@Entity
@Table(name = "workflow_action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkflowAction extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String action;

    private String description;

    private int order;

}
