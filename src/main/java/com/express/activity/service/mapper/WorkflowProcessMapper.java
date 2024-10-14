package com.express.activity.service.mapper;


import com.express.activity.domain.WorkflowProcess;
import com.express.activity.service.dto.WorkflowProcessDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link WorkflowProcess} and its DTO {@link WorkflowProcessDTO}.
 */
@Mapper(componentModel = "spring", uses = { WorkflowTaskMapper.class })
public interface WorkflowProcessMapper extends EntityMapper<WorkflowProcessDTO, WorkflowProcess> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "processInstanceId", source = "processInstanceId")
    @Mapping(target = "formId", source = "formId")
    @Mapping(target = "initiatedBy", source = "initiatedBy")
    WorkflowProcessDTO toDtoId(WorkflowProcess workflowProcess);
}
