package com.express.activity.repository;


import com.express.activity.domain.WorkflowAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the WorkflowAction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, String>, JpaSpecificationExecutor<WorkflowAction> {}
