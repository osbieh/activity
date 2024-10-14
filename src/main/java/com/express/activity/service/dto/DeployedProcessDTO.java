package com.express.activity.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class DeployedProcessDTO implements Serializable {

    private String id;
    private String name;
    private String key;
    private int version;
    private String category;
    private String deploymentId;
    private String resourceName;
}
