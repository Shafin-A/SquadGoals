package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateUserRequest;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserRepository userRepository;
    private UserController userController;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authentication = mock(Authentication.class);
        userController = new UserController(userRepository);
    }

    @Test
    void createUser_shouldReturnConflict_ifUserExists() {
        String firebaseUid = "firebase123";
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setTimezone("America/New_York");
        LocalDateTime now = LocalDateTime.now();
        request.setCreatedAt(now);

        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.existsByFirebaseUid(firebaseUid)).thenReturn(true);

        ResponseEntity<?> response = userController.createUser(request, authentication);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("User already exists.", response.getBody());
    }

    @Test
    void createUser_shouldCreateUser_whenUserDoesNotExist() {
        String firebaseUid = "firebase456";
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Jane Smith");
        request.setEmail("jane@example.com");
        request.setTimezone("Europe/London");
        LocalDateTime now = LocalDateTime.now();
        request.setCreatedAt(now);

        User savedUser = new User();
        savedUser.setFirebaseUid(firebaseUid);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setTimezone(request.getTimezone());
        savedUser.setCreatedAt(request.getCreatedAt());

        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.existsByFirebaseUid(firebaseUid)).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        ResponseEntity<?> response = userController.createUser(request, authentication);

        assertEquals(201, response.getStatusCode().value());

        User returnedUser = (User) response.getBody();
        assertNotNull(returnedUser);
        assertEquals("Jane Smith", returnedUser.getName());
        assertEquals("jane@example.com", returnedUser.getEmail());
        assertEquals("Europe/London", returnedUser.getTimezone());
        assertEquals(firebaseUid, returnedUser.getFirebaseUid());
    }
}