package com.express.activity.repository;


import com.express.activity.domain.WorkflowProcess;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the WorkflowProcess entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkflowProcessRepository extends JpaRepository<WorkflowProcess, Long>, JpaSpecificationExecutor<WorkflowProcess> {

    Optional<WorkflowProcess> findByProcessInstanceId(String processInstanceId);

    Optional<WorkflowProcess> findByFormId(Long formId);

    Optional<WorkflowProcess> findByFormIdAndFormType(Long formId, String formType);

    List<WorkflowProcess> findByFormIdInAndFormType(List<Long> formIds, String formType);

    @Query("SELECT wp.formId FROM WorkflowProcess wp WHERE wp.processInstanceId=:processInstanceId")
    Long findFormIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

    @Transactional
    @Modifying
    @Query("UPDATE WorkflowProcess wp SET wp.complete = true, wp.completeTime = CURRENT_TIMESTAMP, wp.status = 'COMPLETED' WHERE wp.processInstanceId = :processInstanceId")
    void completeWorkflowProcess(@Param("processInstanceId") String processInstanceId);
    @Modifying
    void deleteByFormIdInAndFormType(List<Long> formIds, String formType);
}
