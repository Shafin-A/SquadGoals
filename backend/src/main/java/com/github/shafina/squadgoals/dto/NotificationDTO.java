package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.model.Notification;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,
        String message,
        boolean read,
        LocalDateTime createdAt) {
    public static NotificationDTO from(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt());
    }
}