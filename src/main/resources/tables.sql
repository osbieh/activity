
-- Table to store roles available in the application
CREATE TABLE app_role (
   id SERIAL PRIMARY KEY,
   name VARCHAR(255) NOT NULL
);

-- Table to store privileges
CREATE TABLE privilege (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Table to store user information
CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255),
    mobile_no VARCHAR(255),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(254) UNIQUE,
    activated BOOLEAN NOT NULL DEFAULT FALSE,
    lang_key VARCHAR(10),
    image_url VARCHAR(256)
);

-- Table to establish many-to-many relationship between users and roles
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (role_id) REFERENCES app_role(id)
);

-- Table to store information about workflow processes
CREATE TABLE workflow_process (
    id SERIAL PRIMARY KEY,
    form_type VARCHAR(255),
    form_id BIGINT,
    revision VARCHAR(255),
    category VARCHAR(255),
    init_time TIMESTAMP,
    initiated_by BIGINT,
    process_instance_id VARCHAR(255),
    process_name VARCHAR(255),
    status VARCHAR(255),
    complete BOOLEAN,
    complete_time TIMESTAMP
);

-- Table to store information about tasks within a workflow process
CREATE TABLE workflow_task (
    id SERIAL PRIMARY KEY,
    init_time TIMESTAMP,
    task_id VARCHAR(255),
    task_instance_id VARCHAR(255),
    task_name VARCHAR(255),
    task_sequence INTEGER,
    index INTEGER,
    assignee BIGINT,
    action_required BOOLEAN,
    action_type VARCHAR(255),
    action_time TIMESTAMP,
    action_status VARCHAR(255),
    task_complete BOOLEAN,
    workflow_process_id BIGINT,
    reassigned BOOLEAN,
    parent_task_instance_id VARCHAR(255),
    reassign_index INTEGER,
    act_execution_id VARCHAR(255),
    act_assignee_variable_name VARCHAR(255),
    category VARCHAR(255),
    CONSTRAINT fk_workflow_process FOREIGN KEY (workflow_process_id) REFERENCES workflow_process(id)
);

-- Table to store possible actions that can be taken on tasks
CREATE TABLE workflow_action (
    action VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255),
    "order" INTEGER
);

-- Table to store comments related to workflow tasks
CREATE TABLE comment (
    id SERIAL PRIMARY KEY,
    date DATE,
    user_id BIGINT,
    text TEXT,
    workflow_task_id BIGINT UNIQUE,
    CONSTRAINT fk_workflow_task FOREIGN KEY (workflow_task_id) REFERENCES workflow_task(id)
);



-- Insert roles into app_role table
INSERT INTO app_role (name) VALUES ('CUSTOMER'), ('SELLER'), ('SUPERVISOR'), ('MANAGER');

-- Insert privileges into privilege table (example privileges)
INSERT INTO privilege (name) VALUES ('READ_PRIVILEGE'), ('WRITE_PRIVILEGE'), ('DELETE_PRIVILEGE');

-- Insert users into app_user table
INSERT INTO app_user (name, username, password, activated) VALUES
('User One', 'user1', '123', TRUE),
('User Two', 'user2', '123', TRUE),
('User Three', 'user3', '123', TRUE),
('User Four', 'user4', '123', TRUE);

-- Assign roles to users
INSERT INTO users_roles (user_id, role_id) VALUES
((SELECT id FROM app_user WHERE username = 'user1'), (SELECT id FROM app_role WHERE name = 'CUSTOMER')),
((SELECT id FROM app_user WHERE username = 'user2'), (SELECT id FROM app_role WHERE name = 'SELLER')),
((SELECT id FROM app_user WHERE username = 'user3'), (SELECT id FROM app_role WHERE name = 'SUPERVISOR')),
((SELECT id FROM app_user WHERE username = 'user4'), (SELECT id FROM app_role WHERE name = 'MANAGER'));

-- Assign privileges to roles (example assignments)
-- Assuming you have a table to map roles to privileges, e.g., roles_privileges
CREATE TABLE roles_privileges (
    role_id BIGINT NOT NULL,
    privilege_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, privilege_id),
    FOREIGN KEY (role_id) REFERENCES app_role(id),
    FOREIGN KEY (privilege_id) REFERENCES privilege(id)
);

-- Example privilege assignments
INSERT INTO roles_privileges (role_id, privilege_id) VALUES
((SELECT id FROM app_role WHERE name = 'CUSTOMER'), (SELECT id FROM privilege WHERE name = 'READ_PRIVILEGE')),
((SELECT id FROM app_role WHERE name = 'SELLER'), (SELECT id FROM privilege WHERE name = 'WRITE_PRIVILEGE')),
((SELECT id FROM app_role WHERE name = 'SUPERVISOR'), (SELECT id FROM privilege WHERE name = 'DELETE_PRIVILEGE')),
((SELECT id FROM app_role WHERE name = 'MANAGER'), (SELECT id FROM privilege WHERE name = 'READ_PRIVILEGE')),
((SELECT id FROM app_role WHERE name = 'MANAGER'), (SELECT id FROM privilege WHERE name = 'WRITE_PRIVILEGE')),
((SELECT id FROM app_role WHERE name = 'MANAGER'), (SELECT id FROM privilege WHERE name = 'DELETE_PRIVILEGE'));

