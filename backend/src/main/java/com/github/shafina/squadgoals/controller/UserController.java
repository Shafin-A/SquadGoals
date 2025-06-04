package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateUserRequest;
import com.github.shafina.squadgoals.dto.UserDTO;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request,
                                              Authentication authentication) {
        String firebaseUid = authentication.getName();

        if (userRepository.existsByFirebaseUid(firebaseUid)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User();
        user.setFirebaseUid(firebaseUid);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setTimezone(request.getTimezone());
        user.setCreatedAt(request.getCreatedAt());

        User savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.from(savedUser));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {

        if (query.length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query must be at least 2 characters long");
        }

        if (limit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit cannot be less than 1");
        }

        String firebaseUid = authentication.getName();

        Long authUserId = userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .getId();

        PageRequest limitOnly = PageRequest.of(0, limit);

        List<User> foundUsers = userRepository
                .searchUsersExcludingCurrent(query, authUserId, limitOnly);

        return ResponseEntity.ok(
                foundUsers
                        .stream()
                        .limit(limit)
                        .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getTimezone()))
                        .toList());
    }
}
