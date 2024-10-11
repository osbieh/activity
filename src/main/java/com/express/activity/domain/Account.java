package com.express.activity.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

    private Long id;
    private String name;
    private String email;


}
