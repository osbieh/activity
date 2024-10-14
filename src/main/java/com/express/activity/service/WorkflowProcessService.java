package com.express.activity.service;

import com.express.activity.domain.WorkflowProcess;
import com.express.activity.repository.WorkflowProcessRepository;
import com.express.activity.service.dto.WorkflowProcessDTO;
import com.express.activity.service.mapper.WorkflowProcessMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link WorkflowProcess}.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WorkflowProcessService {

    private final WorkflowProcessRepository workflowProcessRepository;
    private final WorkflowProcessMapper workflowProcessMapper;

    /**
     * Save a workflowProcess.
     *
     * @param workflowProcessDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkflowProcessDTO save(WorkflowProcessDTO workflowProcessDTO) {
        log.debug("Request to save WorkflowProcess : {}", workflowProcessDTO);
        WorkflowProcess workflowProcess = workflowProcessMapper.toEntity(workflowProcessDTO);
        workflowProcess = workflowProcessRepository.save(workflowProcess);
        return workflowProcessMapper.toDto(workflowProcess);
    }

    /**
     * Partially update a workflowProcess.
     *
     * @param workflowProcessDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkflowProcessDTO> partialUpdate(WorkflowProcessDTO workflowProcessDTO) {
        log.debug("Request to partially update WorkflowProcess : {}", workflowProcessDTO);

        return workflowProcessRepository
            .findById(workflowProcessDTO.getId())
            .map(existingWorkflowProcess -> {
                workflowProcessMapper.partialUpdate(existingWorkflowProcess, workflowProcessDTO);

                return existingWorkflowProcess;
            })
            .map(workflowProcessRepository::save)
            .map(workflowProcessMapper::toDto);
    }

    /**
     * Get all the workflowProcesses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkflowProcessDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WorkflowProcesses");
        return workflowProcessRepository.findAll(pageable).map(workflowProcessMapper::toDto);
    }

    /**
     * Get one workflowProcess by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkflowProcessDTO> findOne(Long id) {
        log.debug("Request to get WorkflowProcess : {}", id);
        return workflowProcessRepository.findById(id).map(workflowProcessMapper::toDto);
    }

    /**
     * Delete the workflowProcess by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete WorkflowProcess : {}", id);
        workflowProcessRepository.deleteById(id);
    }

    public Optional<WorkflowProcessDTO> findByProcessInstanceId(String processInstanceId) {
        log.debug("Request to get WorkflowProcess by processInstanceId: {}", processInstanceId);
        return workflowProcessRepository.findByProcessInstanceId(processInstanceId).map(workflowProcessMapper::toDto);
    }
    public Optional<WorkflowProcessDTO> findByFormId(Long formId) {
        return workflowProcessRepository.findByFormId(formId).map(workflowProcessMapper::toDto);
    }

    public WorkflowProcessDTO findByFormIdAndFormType(Long formId, String formType) {
        return workflowProcessRepository.findByFormIdAndFormType(formId, formType).map(workflowProcessMapper::toDto).orElseThrow();
    }

    public List<WorkflowProcessDTO> findByFormIdInAndFormType(List<Long> formIds, String formType) {
        return workflowProcessRepository.findByFormIdInAndFormType(formIds, formType).stream().map(workflowProcessMapper::toDto).collect(Collectors.toList());
    }

    public Long findFormIdByProcessInstanceId(String processInstanceId) {
        return workflowProcessRepository.findFormIdByProcessInstanceId(processInstanceId);
    }

    public void completeWorkflowProcess(String processInstanceId) {
        workflowProcessRepository.completeWorkflowProcess(processInstanceId);
    }

    public void deleteByFormIdInAndFormType(List<Long> formIds, String formType) {
        workflowProcessRepository.deleteByFormIdInAndFormType(formIds, formType);
    }

}
