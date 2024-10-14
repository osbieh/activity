package com.express.activity.service.dto;

import jakarta.persistence.Lob;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDate;

/**
 * A DTO for the {@link com.express.activity.domain.Comment} entity.
 */
@Data
@Builder
@EqualsAndHashCode(exclude = {"workflowTask"}, callSuper = false)
@ToString(exclude = {"workflowTask"})
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO extends AbstractAuditingEntityDTO implements Serializable {

    private Long id;

    private LocalDate date;

    private Long userId;

    @Lob
    private String text;

    private WorkflowTaskDTO workflowTask;
}
