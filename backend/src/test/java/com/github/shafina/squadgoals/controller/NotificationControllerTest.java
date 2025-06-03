package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.NotificationDTO;
import com.github.shafina.squadgoals.model.Notification;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.NotificationRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private NotificationController notificationController;
    private Authentication authentication;
    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        authentication = mock(Authentication.class);
        notificationController = new NotificationController(notificationRepository, userRepository);

        user = new User();
        user.setId(1L);
        user.setFirebaseUid("firebase-uid-1");

        notification = new Notification();
        notification.setId(101L);
        notification.setUser(user);
        notification.setMessage("Test notification");
        notification.setRead(false);
    }

    @Test
    void getUserInvitations_shouldReturnNotifications() {
        when(authentication.getName()).thenReturn("firebase-uid-1");
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification));

        ResponseEntity<List<NotificationDTO>> response = notificationController.getUserInvitations(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<NotificationDTO> body =response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        NotificationDTO dto = body.get(0);
        assertEquals(notification.getId(), dto.id());
        assertEquals(notification.getMessage(), dto.message());
    }

    @Test
    void getUserInvitations_shouldReturnEmptyList_whenNoNotifications() {
        when(authentication.getName()).thenReturn("firebase-uid-1");
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(Collections.emptyList());

        ResponseEntity<List<NotificationDTO>> response = notificationController.getUserInvitations(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<NotificationDTO> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty());
    }

    @Test
    void getUserInvitations_shouldThrowNotFound_whenUserNotFound() {
        when(authentication.getName()).thenReturn("firebase-uid-1");
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> notificationController.getUserInvitations(authentication));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }

    @Test
    void getUserInvitations_shouldReturnMultipleNotifications() {
        Notification notification2 = new Notification();
        notification2.setId(102L);
        notification2.setUser(user);
        notification2.setMessage("Another notification");
        notification2.setRead(true);

        when(authentication.getName()).thenReturn("firebase-uid-1");
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(user));
        when(notificationRepository.findByUser(user)).thenReturn(List.of(notification, notification2));

        ResponseEntity<List<NotificationDTO>> response = notificationController.getUserInvitations(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<NotificationDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
        NotificationDTO dto1 = body.get(0);
        NotificationDTO dto2 = body.get(1);
        assertEquals(notification.getId(), dto1.id());
        assertEquals(notification2.getId(), dto2.id());
        assertEquals("Another notification", dto2.message());
    }
}
