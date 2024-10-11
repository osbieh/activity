package com.express.activity.web;


import com.express.activity.domain.Account;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class HelloResource {

    @GetMapping("/hello")
   @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<String> hello(){

        Account acc = Account.builder().id(1L).name("Osama Sbieh").email("osbieh@ccc.net").build();


        return ResponseEntity.ok().body(""+acc.toString());
    }


}
