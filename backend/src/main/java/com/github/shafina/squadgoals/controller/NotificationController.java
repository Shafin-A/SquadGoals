package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.NotificationDTO;
import com.github.shafina.squadgoals.model.Notification;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.NotificationRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(
            @RequestParam(defaultValue = "true") boolean recent,
            @RequestParam(defaultValue = "10") int limit, Authentication authentication) {
        String firebaseUid = authentication.getName();

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Notification> notifications = notificationRepository.findByUser(user);

        if (recent) {
            notifications = notifications
                    .stream()
                    .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else {
            notifications = notifications
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(notifications.stream().map(NotificationDTO::from).toList());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        String firebaseUid = authentication.getName();

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Notification notification = notificationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        String firebaseUid = authentication.getName();

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Notification> unreadNotifications = notificationRepository.findAllByUserAndReadFalse(user);

        if (!unreadNotifications.isEmpty()) {
            for (Notification notification : unreadNotifications) {
                notification.setRead(true);
            }
            notificationRepository.saveAll(unreadNotifications);
        }

        return ResponseEntity.noContent().build();
    }
}
