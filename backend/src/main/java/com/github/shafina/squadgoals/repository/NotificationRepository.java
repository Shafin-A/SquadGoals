package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.enums.NotificationType;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Notification;
import com.github.shafina.squadgoals.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);

    boolean existsByUserAndGoalAndNotificationTypeAndCreatedAtBetween(User user, Goal goal, NotificationType notificationType, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
