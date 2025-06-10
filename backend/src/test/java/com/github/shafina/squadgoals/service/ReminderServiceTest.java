package com.github.shafina.squadgoals.service;

import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Notification;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.GoalRepository;
import com.github.shafina.squadgoals.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

public class ReminderServiceTest {

    private GoalRepository goalRepository;
    private NotificationRepository notificationRepository;
    private ReminderService reminderService;

    @BeforeEach
    void setUp() {
        goalRepository = mock(GoalRepository.class);
        notificationRepository = mock(NotificationRepository.class);
        reminderService = new ReminderService();
        // Use reflection to inject mocks since fields are package-private
        try {
            Field goalRepoField = ReminderService.class.getDeclaredField("goalRepository");
            goalRepoField.setAccessible(true);
            goalRepoField.set(reminderService, goalRepository);

            Field notifRepoField = ReminderService.class.getDeclaredField("notificationRepository");
            notifRepoField.setAccessible(true);
            notifRepoField.set(reminderService, notificationRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void sendDailyReminders_shouldSendNotificationAndUpdateGoal() {
        Goal goal = mock(Goal.class);
        User user = mock(User.class);
        Set<User> squad = new HashSet<>(List.of(user));
        when(goal.getSquad()).thenReturn(squad);
        when(goal.getTitle()).thenReturn("Test Goal");
        when(goal.getFrequency()).thenReturn(com.github.shafina.squadgoals.enums.Frequency.DAILY);
        when(goal.getNextDueAt()).thenReturn(LocalDateTime.now());

        when(goalRepository.findGoalsDueToday(any())).thenReturn(List.of(goal));
        when(notificationRepository.existsByUserAndGoalAndNotificationTypeAndCreatedAtBetween(any(), any(), any(),
                any(), any()))
                .thenReturn(false);

        reminderService.sendDailyReminders();

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void sendDailyReminders_shouldNotSendDuplicateNotification() {
        Goal goal = mock(Goal.class);
        User user = mock(User.class);
        Set<User> squad = new HashSet<>(List.of(user));
        when(goal.getSquad()).thenReturn(squad);
        when(goal.getTitle()).thenReturn("Test Goal");
        when(goal.getFrequency()).thenReturn(com.github.shafina.squadgoals.enums.Frequency.DAILY);
        when(goal.getNextDueAt()).thenReturn(LocalDateTime.now());

        when(goalRepository.findGoalsDueToday(any())).thenReturn(List.of(goal));
        when(notificationRepository.existsByUserAndGoalAndNotificationTypeAndCreatedAtBetween(any(), any(), any(),
                any(), any()))
                .thenReturn(true);

        reminderService.sendDailyReminders();

        verify(notificationRepository, never()).save(any(Notification.class));
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    void sendDailyReminders_shouldHandleNoGoals() {
        when(goalRepository.findGoalsDueToday(any())).thenReturn(Collections.emptyList());

        reminderService.sendDailyReminders();

        verify(notificationRepository, never()).save(any(Notification.class));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void sendDailyReminders_shouldHandleMultipleUsers() {
        Goal goal = mock(Goal.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        Set<User> squad = new HashSet<>(Arrays.asList(user1, user2));
        when(goal.getSquad()).thenReturn(squad);
        when(goal.getTitle()).thenReturn("Test Goal");
        when(goal.getFrequency()).thenReturn(com.github.shafina.squadgoals.enums.Frequency.DAILY);
        when(goal.getNextDueAt()).thenReturn(LocalDateTime.now());

        when(goalRepository.findGoalsDueToday(any())).thenReturn(List.of(goal));
        when(notificationRepository.existsByUserAndGoalAndNotificationTypeAndCreatedAtBetween(any(), any(), any(),
                any(), any()))
                .thenReturn(false);

        reminderService.sendDailyReminders();

        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(goalRepository, times(1)).save(goal);
    }
}
