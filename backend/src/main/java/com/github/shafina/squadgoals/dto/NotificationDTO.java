package com.github.shafina.squadgoals.dto;

import java.time.LocalDateTime;

import com.github.shafina.squadgoals.model.Notification;

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