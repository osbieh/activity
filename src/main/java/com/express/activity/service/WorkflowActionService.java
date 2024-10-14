package com.express.activity.service;

import com.express.activity.domain.WorkflowAction;
import com.express.activity.repository.WorkflowActionRepository;
import com.express.activity.service.dto.WorkflowActionDTO;
import com.express.activity.service.mapper.WorkflowActionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link WorkflowAction}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkflowActionService {

    @Autowired
    private WorkflowActionRepository workflowActionRepository;

    @Autowired
    private  WorkflowActionMapper workflowActionMapper;

    /**
     * Save a workflowAction.
     *
     * @param workflowActionDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkflowActionDTO save(WorkflowActionDTO workflowActionDTO) {
        log.debug("Request to save WorkflowAction : {}", workflowActionDTO);
        WorkflowAction workflowAction = workflowActionMapper.toEntity(workflowActionDTO);
        workflowAction = workflowActionRepository.save(workflowAction);
        return workflowActionMapper.toDto(workflowAction);
    }

    /**
     * Partially update a workflowAction.
     *
     * @param workflowActionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkflowActionDTO> partialUpdate(WorkflowActionDTO workflowActionDTO) {
        log.debug("Request to partially update WorkflowAction : {}", workflowActionDTO);

        return workflowActionRepository
            .findById(workflowActionDTO.getAction())
            .map(existingWorkflowAction -> {
                workflowActionMapper.partialUpdate(existingWorkflowAction, workflowActionDTO);

                return existingWorkflowAction;
            })
            .map(workflowActionRepository::save)
            .map(workflowActionMapper::toDto);
    }

    /**
     * Get all the workflowActions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkflowActionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WorkflowActions");
        return workflowActionRepository.findAll(pageable).map(workflowActionMapper::toDto);
    }

    /**
     * Get one workflowAction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkflowActionDTO> findOne(String id) {
        log.debug("Request to get WorkflowAction : {}", id);
        return workflowActionRepository.findById(id).map(workflowActionMapper::toDto);
    }

    /**
     * Delete the workflowAction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete WorkflowAction : {}", id);
        workflowActionRepository.deleteById(id);
    }
}
