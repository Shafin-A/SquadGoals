package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.model.Notification;
import com.github.shafina.squadgoals.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByUserAndMessageAndCreatedAtBetween(User user, String msg, LocalDateTime localDateTime,
                                                      LocalDateTime localDateTime1);

    List<Notification> findByUser(User user);
}
