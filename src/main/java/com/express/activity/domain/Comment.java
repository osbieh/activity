package com.express.activity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@EqualsAndHashCode(exclude = {"workflowTask"}, callSuper = false)
@ToString(exclude = {"workflowTask"})
public class Comment extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Long userId;

    @Lob
   // @Type(type = "org.hibernate.type.TextType")
    @Column(name = "text")
    private String text;

    @JsonIgnoreProperties(value = { "comment", "workflowProcess" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private WorkflowTask workflowTask;

}
