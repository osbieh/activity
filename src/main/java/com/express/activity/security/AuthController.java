package com.express.activity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);
         System.out.println(jwt);
        return ResponseEntity.ok().body(new AuthenticationResponse(jwt));
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // Authenticate user (this is just a placeholder, implement your own authentication logic)
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // Generate token
        return jwtUtil.generateToken(userDetails);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token, @RequestParam String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.validateToken(token, userDetails);
    }

    @GetMapping("/username")
    public String getUsername(@RequestParam String token) {
        return jwtUtil.extractUsername(token);
    }

}

