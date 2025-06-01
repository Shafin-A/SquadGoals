package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateUserRequest;
import com.github.shafina.squadgoals.dto.UserSearchDTO;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request, Authentication authentication) {
        String firebaseUid = authentication.getName();

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

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchDTO>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {

        if (limit < 1) {
            return ResponseEntity.badRequest().build();
        }

        List<User> foundUsers = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);

        return ResponseEntity.ok(
                foundUsers
                        .stream()
                        .limit(limit)
                        .map(u -> new UserSearchDTO(u.getId(), u.getName(), u.getEmail()))
                        .toList());
    }
}
