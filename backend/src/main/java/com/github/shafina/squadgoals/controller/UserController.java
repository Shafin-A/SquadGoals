package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateUserRequest;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request, Authentication authentication) {
        String firebaseUid = (String) authentication.getPrincipal();

        if (userRepository.existsByFirebaseUid(firebaseUid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        }

        User user = new User();
        user.setFirebaseUid(firebaseUid);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setTimezone(request.getTimezone());
        user.setCreatedAt(request.getCreatedAt());

        User savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
