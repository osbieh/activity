package com.express.activity.service;

import com.express.activity.domain.Privilege;
import com.express.activity.domain.Role;
import com.express.activity.domain.AppUser;
import com.express.activity.repository.PrivilegeRepository;
import com.express.activity.repository.RoleRepository;
import com.express.activity.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @PostConstruct
    public void init() {
        // Create Privileges
        Privilege readPrivilege = new Privilege("READ_PRIVILEGE");
        Privilege writePrivilege = new Privilege("WRITE_PRIVILEGE");
        Privilege deletePrivilege = new Privilege("DELETE_PRIVILEGE");
        privilegeRepository.saveAll(Arrays.asList(readPrivilege, writePrivilege, deletePrivilege));

        // Create Roles
        Role customerRole = new Role("ROLE_CUSTOMER");
        customerRole.setPrivileges(Arrays.asList(readPrivilege));
        Role sellerRole = new Role("ROLE_SELLER");
        sellerRole.setPrivileges(Arrays.asList(readPrivilege, writePrivilege));
        Role supervisorRole = new Role("ROLE_SUPERVISOR");
        supervisorRole.setPrivileges(Arrays.asList(readPrivilege, writePrivilege));
        Role managerRole = new Role("ROLE_MANAGER");
        managerRole.setPrivileges(Arrays.asList(readPrivilege, writePrivilege, deletePrivilege));
        roleRepository.saveAll(Arrays.asList(customerRole, sellerRole, supervisorRole, managerRole));

        // Create Users
        AppUser user1 = new AppUser();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("123"));
        user1.setRoles(Arrays.asList(customerRole));
        userRepository.save(user1);

        AppUser user2 = new AppUser();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("123"));
        user2.setRoles(Arrays.asList(sellerRole));
        userRepository.save(user2);

        AppUser user3 = new AppUser();
        user3.setUsername("user3");
        user3.setPassword(passwordEncoder.encode("123"));
        user3.setRoles(Arrays.asList(supervisorRole));
        userRepository.save(user3);

        AppUser user4 = new AppUser();
        user4.setUsername("user4");
        user4.setPassword(passwordEncoder.encode("123"));
        user4.setRoles(Arrays.asList(managerRole));
        userRepository.save(user4);
    }
}