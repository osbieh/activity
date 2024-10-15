package com.express.activity.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Data
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;

    public Privilege(Long id,String name) {
        this.id=id;
        this.name = name;
    }

    public Privilege() {
    }
// getters and setters
}
