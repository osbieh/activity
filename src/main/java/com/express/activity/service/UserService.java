package com.express.activity.service;

import com.express.activity.config.Constants;
import com.express.activity.domain.AppUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private static AppUser getUser(Map<String, Object> details) {
        AppUser user = new AppUser();
        Boolean activated = Boolean.TRUE;
        String sub = String.valueOf(details.get("sub"));
        String username = null;
        if (details.get("preferred_username") != null) {
            username = ((String) details.get("preferred_username")).toLowerCase();
        }
        // handle resource server JWT, where sub claim is email and uid is ID
        if (details.get("uid") != null) {
            user.setId((Long) details.get("uid"));
            user.setUsername(sub);
        } else {
            user.setId(Long.parseLong(sub));
        }
        if (username != null) {
            user.setUsername(username);
        } else if (user.getUsername() == null) {
            user.setUsername(user.getUsername());
        }
        if (details.get("given_name") != null) {
            user.setFirstName((String) details.get("given_name"));
        } else if (details.get("name") != null) {
            user.setFirstName((String) details.get("name"));
        }
        if (details.get("family_name") != null) {
            user.setLastName((String) details.get("family_name"));
        }
        if (details.get("email_verified") != null) {
            activated = (Boolean) details.get("email_verified");
        }
        if (details.get("email") != null) {
            String email = ((String) details.get("email")).toLowerCase();
            user.setEmail(email);
        } else if (sub.contains("|") && (username != null && username.contains("@"))) {
            // special handling for Auth0
            user.setEmail(username);
        } else {
            user.setEmail(sub);
        }
        if (details.get("langKey") != null) {
            user.setLangKey((String) details.get("langKey"));
        } else if (details.get("locale") != null) {
            // trim off country code if it exists
            String locale = (String) details.get("locale");
            if (locale.contains("_")) {
                locale = locale.substring(0, locale.indexOf('_'));
            } else if (locale.contains("-")) {
                locale = locale.substring(0, locale.indexOf('-'));
            }
            user.setLangKey(locale.toLowerCase());
        } else {
            // set langKey to default if not specified by IdP
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
        if (details.get("picture") != null) {
            user.setImageUrl((String) details.get("picture"));
        }
        user.setActivated(activated);
        return user;
    }



    public static AppUser currentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user;
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken oauthToken = (UsernamePasswordAuthenticationToken) authentication;
            user =null; //getUser(oauthToken.getPrincipal());
        } else {
            JwtAuthenticationToken oauthToken = (JwtAuthenticationToken) authentication;
            user = getUser(oauthToken.getTokenAttributes());
        }
        return user;
    }
}
