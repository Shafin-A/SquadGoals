package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.NotificationDTO;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.NotificationRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getUserInvitations(Authentication authentication) {
        String firebaseUid = authentication.getName();

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<NotificationDTO> notifications = notificationRepository.findByUser(user)
                .stream()
                .map(NotificationDTO::from)
                .toList();

        return ResponseEntity.ok(notifications);
    }
}
