package com.express.activity.service.mapper;


import com.express.activity.domain.WorkflowAction;
import com.express.activity.service.dto.WorkflowActionDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link WorkflowAction} and its DTO {@link WorkflowActionDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface WorkflowActionMapper extends EntityMapper<WorkflowActionDTO, WorkflowAction> {}
