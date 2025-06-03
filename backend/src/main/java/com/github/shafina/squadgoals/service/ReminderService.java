package com.github.shafina.squadgoals.service;

import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Notification;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.GoalRepository;
import com.github.shafina.squadgoals.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ReminderService {
    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // @Autowired
    // private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *") // every day at 8am
    public void sendDailyReminders() {
        List<Goal> dailyGoals = goalRepository.findGoalsDueToday(LocalDateTime.now());

        for (Goal goal : dailyGoals) {
            Set<User> squad = goal.getSquad();
            String msg = "Reminder: Your goal '" + goal.getTitle() + "' is due today!";

            for (User user : squad) {
                boolean exists = notificationRepository.existsByUserAndMessageAndCreatedAtBetween(
                        user, msg, LocalDateTime.now().toLocalDate().atStartOfDay(),
                        LocalDateTime.now().toLocalDate().atTime(23, 59, 59));

                if (exists) {
                    continue;
                }

                Notification notif = new Notification();
                notif.setUser(user);
                notif.setMessage(msg);

                notificationRepository.save(notif);

                // emailService.send(user.getEmail(), "Goal Reminder", msg);
            }

            LocalDateTime nextDueAt = switch (goal.getFrequency()) {
                case DAILY -> goal.getNextDueAt().plusDays(1);
                case WEEKLY -> goal.getNextDueAt().plusWeeks(1);
                case MONTHLY -> goal.getNextDueAt().plusMonths(1);
                default -> goal.getNextDueAt();
            };

            goal.setNextDueAt(nextDueAt);
            goalRepository.save(goal);
        }
    }
}