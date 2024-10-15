package com.express.activity.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Table(name = "app_role")
@Data

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles")
    private Collection<AppUser> users;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private Collection<Privilege> privileges;

    public Role(Long id,String name) {
        this.name = name;
        this.id=id;
    }

    // Default constructor for JPA
    public Role() {
    }
}

