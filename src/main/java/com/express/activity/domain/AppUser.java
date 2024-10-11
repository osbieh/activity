package com.express.activity.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;


@Entity
@Table(name = "app_user")
@Data
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_seq")
    @SequenceGenerator(name = "app_user_seq", sequenceName = "app_user_seq", allocationSize = 50)
    private Long id;
    private String username;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    // getters and setters

  /**
    drop schema public cascade;
    create schema public;


    CREATE SEQUENCE app_user_seq
    INCREMENT BY 50
    START WITH 1;

    CREATE SEQUENCE app_role_seq
    INCREMENT BY 50
    START WITH 1;

    CREATE SEQUENCE privilege_seq
    INCREMENT BY 50
    START WITH 1;

    CREATE TABLE app_role (
            id BIGINT PRIMARY KEY DEFAULT nextval('app_role_seq'),
    name VARCHAR(255) NOT NULL
);

    CREATE TABLE app_user (
            id BIGINT PRIMARY KEY DEFAULT nextval('app_user_seq'),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

    ALTER TABLE app_user
    ALTER COLUMN id SET DEFAULT nextval('app_user_seq');




    ALTER TABLE app_role
    ALTER COLUMN id SET DEFAULT nextval('app_role_seq');

    CREATE TABLE privilege (
            id SERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL
);

    CREATE TABLE users_roles (
            user_id BIGINT NOT NULL,
            role_id BIGINT NOT NULL,
            PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (role_id) REFERENCES app_role(id)
            );

    CREATE TABLE roles_privileges (
            role_id BIGINT NOT NULL,
            privilege_id BIGINT NOT NULL,
            PRIMARY KEY (role_id, privilege_id),
    FOREIGN KEY (role_id) REFERENCES app_role(id),
    FOREIGN KEY (privilege_id) REFERENCES privilege(id)
            );
**/
}
