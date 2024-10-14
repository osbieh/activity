package com.express.activity.domain;

import com.express.activity.config.ZoneConfiguration;
import com.express.activity.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created by,
 * last modified by attributes.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "created_by", length = 50, nullable = false, updatable = false)
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    @JsonIgnore
    private Instant createdDate = LocalDateTime.now().toInstant(ZoneOffset.UTC);

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    @JsonIgnore
    private Instant lastModifiedDate =  LocalDateTime.now().toInstant(ZoneConfiguration.zoneOffset);

    @PrePersist
    private void onInsert() {
        this.createdDate = this.lastModifiedDate = LocalDateTime.now().toInstant(ZoneConfiguration.zoneOffset);
        AppUser user = UserService.currentLoggedInUser();
        this.createdBy = this.lastModifiedBy = user.getUsername() != null ? String.valueOf(user.getEmail()) : "System";
    }

    @PreUpdate
    private void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        AppUser user = UserService.currentLoggedInUser();
        this.lastModifiedBy = user.getUsername() != null ? String.valueOf(user.getEmail()) : "System";
    }

}
