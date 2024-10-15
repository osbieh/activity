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

   // @PostConstruct
    public void init() {
        // Create Privileges
        Privilege readPrivilege = new Privilege(1L,"READ_PRIVILEGE");
        Privilege writePrivilege = new Privilege(2L,"WRITE_PRIVILEGE");
        Privilege deletePrivilege = new Privilege(3L,"DELETE_PRIVILEGE");
        privilegeRepository.saveAll(Arrays.asList(readPrivilege, writePrivilege, deletePrivilege));

        // Create Roles
        Role customerRole = new Role(1L,"ROLE_CUSTOMER");
        customerRole.setPrivileges(Arrays.asList(readPrivilege));
        Role sellerRole = new Role(2L,"ROLE_SELLER");
        sellerRole.setPrivileges(Arrays.asList(readPrivilege, writePrivilege));
        Role supervisorRole = new Role(3L,"ROLE_SUPERVISOR");
        supervisorRole.setPrivileges(Arrays.asList(readPrivilege, writePrivilege));
        Role managerRole = new Role(4L,"ROLE_MANAGER");
        managerRole.setPrivileges(Arrays.asList(readPrivilege, writePrivilege, deletePrivilege));
        roleRepository.saveAll(Arrays.asList(customerRole, sellerRole, supervisorRole, managerRole));

        // Create Users
        AppUser user1 = new AppUser();
        user1.setId(1L);
        user1.setName("User One");
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("123"));
        user1.setRoles(Arrays.asList(customerRole));
        user1.setMobileNo("0568000586");
        user1.setEmail("user1@xml.net");
        userRepository.save(user1);

        AppUser user2 = new AppUser();
        user2.setId(2L);
        user2.setName("User Two");
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("123"));
        user2.setRoles(Arrays.asList(sellerRole));
        user2.setMobileNo("05999999999");
        user2.setEmail("user2@ysx.net");
        userRepository.save(user2);

        AppUser user3 = new AppUser();
        user3.setId(3L);
        user3.setName("User Three");
        user3.setUsername("user3");
        user3.setPassword(passwordEncoder.encode("123"));
        user3.setRoles(Arrays.asList(supervisorRole));
        userRepository.save(user3);

        AppUser user4 = new AppUser();
        user4.setId(4L);
        user4.setName("User Four");
        user4.setUsername("user4");
        user4.setPassword(passwordEncoder.encode("123"));
        user4.setRoles(Arrays.asList(managerRole));
        userRepository.save(user4);
    }
}