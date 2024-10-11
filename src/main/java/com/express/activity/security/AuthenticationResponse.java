package com.express.activity.security;

public class AuthenticationResponse {
    private final String access_token;

    public AuthenticationResponse(String access_token) {
        this.access_token = access_token;
    }

    public String getJwt() {
        return access_token;
    }
}

