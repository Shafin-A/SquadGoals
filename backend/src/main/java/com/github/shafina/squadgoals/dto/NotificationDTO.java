package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.enums.NotificationType;
import com.github.shafina.squadgoals.model.Notification;

import java.time.LocalDateTime;
import java.util.Optional;

public record NotificationDTO(
        Long id,
        NotificationType notificationType,
        boolean read,
        LocalDateTime createdAt,
        String senderName,
        String senderProfilePicture,
        String goalTitle) {
    public static NotificationDTO from(Notification notification) {
        UserDTO senderDTO = Optional.ofNullable(notification.getSender())
                .map(UserDTO::from)
                .orElse(null);

        return new NotificationDTO(
                notification.getId(),
                notification.getNotificationType(),
                notification.isRead(),
                notification.getCreatedAt(),
                senderDTO != null ? senderDTO.name() : null,
                null,
                notification.getGoal().getTitle()
        );
    }
}