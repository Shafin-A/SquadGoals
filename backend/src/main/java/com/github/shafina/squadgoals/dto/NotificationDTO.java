package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.model.Notification;

import java.time.LocalDateTime;
import java.util.Optional;

public record NotificationDTO(
        Long id,
        String message,
        boolean read,
        LocalDateTime createdAt,
        UserDTO sender) {
    public static NotificationDTO from(Notification notification) {
        UserDTO senderDTO = Optional.ofNullable(notification.getSender())
                .map(UserDTO::from)
                .orElse(null);

        return new NotificationDTO(
                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt(),
                senderDTO
        );
    }
}