package com.express.activity.service.mapper;


import com.express.activity.domain.Comment;
import com.express.activity.service.dto.CommentDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring", uses = { WorkflowTaskMapper.class })
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {

    @Mapping(target = "workflowTask", source = "workflowTask", qualifiedByName = "id")
    CommentDTO toDto(Comment s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "workflowTask", source = "workflowTask", qualifiedByName = "id")
    CommentDTO toDtoId(Comment comment);
}
