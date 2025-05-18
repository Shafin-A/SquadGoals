package com.github.shafina.squadgoals;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/public/greeting")
    public String getPublicGreeting() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/user/profile")
    public String getUserProfile(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        return "Hello, user with UID: " + uid;
    }
}
