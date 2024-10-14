package com.express.activity.service.mapper;


import com.express.activity.domain.WorkflowTask;
import com.express.activity.service.dto.WorkflowTaskDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link WorkflowTask} and its DTO {@link WorkflowTaskDTO}.
 */
@Mapper(componentModel = "spring", uses = { WorkflowProcessMapper.class, CommentMapper.class })
public interface WorkflowTaskMapper extends EntityMapper<WorkflowTaskDTO, WorkflowTask> {
    @Mapping(target = "workflowProcess", source = "workflowProcess", qualifiedByName = "id")
    @Mapping(target = "comment", source = "comment", qualifiedByName = "id")
    WorkflowTaskDTO toDto(WorkflowTask s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WorkflowTaskDTO toDtoId(WorkflowTask workflowTask);
}
