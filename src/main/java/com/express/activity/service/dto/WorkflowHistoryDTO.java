package com.express.activity.service.dto;

import com.express.activity.domain.enumuration.ActionStatus;
import com.express.activity.domain.enumuration.LocationType;
import lombok.*;


import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"workflowTasks"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowHistoryDTO implements Serializable {
    private LocationType locationType;
    @Builder.Default
    private List<WorkflowHistoryCategory> workflowTasks = new ArrayList<>();

    @Data
    @EqualsAndHashCode(exclude = {"taskDetails"}, callSuper = false)
    @ToString(exclude = {"taskDetails"})
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkflowHistoryCategory {
        private String taskName;
        private Integer taskSequence;
        @Builder.Default
        private List<WorkflowTaskDetails> taskDetails = new ArrayList<>();
    }

    @Data
    @EqualsAndHashCode(exclude = {"comment"}, callSuper = false)
    @ToString(exclude = {"comment"})
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkflowTaskDetails {
        private String userName;
        private String userGroupNo;
        private String userEmail;
        private String title;
        private Instant actionTime;
        private ActionStatus actionStatus;
        private CommentDTO comment;
        private String documentId;
        private String onBehalfUser;
        private String taskId;
        private String taskInstanceId;
    }
}
