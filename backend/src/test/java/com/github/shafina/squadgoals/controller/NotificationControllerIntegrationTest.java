package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.config.SecurityConfig;
import com.github.shafina.squadgoals.enums.NotificationType;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Notification;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.NotificationRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import com.github.shafina.squadgoals.security.FirebaseAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = NotificationController.class)
public class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @MockitoBean
    private FirebaseAuthProvider firebaseAuthProvider;

    private User user;
    private Notification notification;
    private Goal goal;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFirebaseUid("firebase-uid-1");

        goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Test system title");

        notification = new Notification();
        notification.setId(101L);
        notification.setNotificationType(NotificationType.SYSTEM);
        notification.setGoal(goal);
        notification.setUser(user);
        notification.setRead(false);
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getNotifications_shouldReturnNotifications() throws Exception {
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(notification.getId()))
                .andExpect(jsonPath("$[0].notificationType").value(NotificationType.SYSTEM.name()))
                .andExpect(jsonPath("$[0].goalTitle").value(goal.getTitle()));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getNotifications_shouldReturnEmptyList_whenNoNotifications() throws Exception {
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getNotifications_shouldReturnNotFound_whenUserNotFound() throws Exception {
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getNotifications_shouldReturnMultipleNotifications() throws Exception {
        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Test invite title");

        Notification notification2 = new Notification();
        notification2.setId(102L);
        notification2.setNotificationType(NotificationType.INVITE);
        notification2.setUser(user);
        notification2.setGoal(goal2);
        notification2.setRead(true);
        notification2.setCreatedAt(notification2.getCreatedAt().plusDays(1));

        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification, notification2));

        mockMvc.perform(get("/api/notifications")
                .param("recent", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(notification2.getId()))
                .andExpect(jsonPath("$[0].notificationType").value(NotificationType.INVITE.name()))
                .andExpect(jsonPath("$[0].goalTitle").value(goal2.getTitle()))
                .andExpect(jsonPath("$[1].id").value(notification.getId()))
                .andExpect(jsonPath("$[1].notificationType").value(NotificationType.SYSTEM.name()))
                .andExpect(jsonPath("$[1].goalTitle").value(goal.getTitle()));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getNotifications_shouldReturnUnreadStatus() throws Exception {
        notification.setRead(false);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getNotifications_shouldReturnReadStatus() throws Exception {
        notification.setRead(true);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read").value(true));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getNotifications_shouldReturnCorrectId() throws Exception {
        notification.setId(555L);
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(555L));
    }
}