package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateUserRequest;
import com.github.shafina.squadgoals.dto.UserDTO;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        ResponseEntity<UserDTO> response = userController.createUser(request, authentication);

        assertEquals(409, response.getStatusCode().value());
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

        ResponseEntity<UserDTO> response = userController.createUser(request, authentication);

        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void searchUsers_shouldReturnBadRequest_whenQueryIsTooShort() {
        when(authentication.getName()).thenReturn("firebaseUid");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userController.searchUsers("a", 5, authentication));
        assertEquals(400, exception.getStatusCode().value());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("at least 2 characters"));
    }

    @Test
    void searchUsers_shouldReturnBadRequest_whenLimitIsLessThanOne() {
        when(authentication.getName()).thenReturn("firebaseUid");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userController.searchUsers("test", 0, authentication));
        assertEquals(400, exception.getStatusCode().value());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Limit cannot be less than 1"));
    }

    @Test
    void searchUsers_shouldReturnNotFound_whenAuthUserNotFound() {
        when(authentication.getName()).thenReturn("firebaseUid");
        when(userRepository.findByFirebaseUid("firebaseUid")).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userController.searchUsers("test", 2, authentication));
        assertEquals(404, exception.getStatusCode().value());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("User not found"));
    }

    @Test
    void searchUsers_shouldReturnEmptyList_whenNoUsersFound() {
        String firebaseUid = "firebaseUid";
        User authUser = new User();
        authUser.setId(1L);
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(authUser));
        when(userRepository.searchUsersExcludingCurrent("notfound", 1L, PageRequest.of(0, 5)))
                .thenReturn(List.of());

        ResponseEntity<List<UserDTO>> response = userController.searchUsers("notfound", 5, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void searchUsers_shouldNotReturnAuthUserInResults() {
        String firebaseUid = "firebaseUid";
        User authUser = new User();
        authUser.setId(1L);
        authUser.setName("Auth User");
        authUser.setEmail("auth@example.com");
        authUser.setTimezone("UTC");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Bob");
        user2.setEmail("bob@example.com");
        user2.setTimezone("UTC");

        List<User> users = List.of(user2);

        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(authUser));
        when(userRepository.searchUsersExcludingCurrent("example", 1L, PageRequest.of(0, 5)))
                .thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.searchUsers("example", 5, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Bob", response.getBody().get(0).name());
    }
}