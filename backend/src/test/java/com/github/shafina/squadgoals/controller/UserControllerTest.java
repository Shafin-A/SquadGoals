package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateUserRequest;
import com.github.shafina.squadgoals.dto.UserDTO;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

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

        UserDTO returnedUser = (UserDTO) response.getBody();
        assertNotNull(returnedUser);
        assertEquals("Jane Smith", returnedUser.name());
        assertEquals("jane@example.com", returnedUser.email());
        assertEquals("Europe/London", returnedUser.timezone());
    }

    @Test
    void searchUsers_shouldReturnBadRequest_whenLimitIsLessThanOne() {
        ResponseEntity<List<UserDTO>> response = userController.searchUsers("test", 0);
        assertEquals(400, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void searchUsers_shouldReturnEmptyList_whenNoUsersFound() {
        when(userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase("notfound", "notfound"))
                .thenReturn(List.of());

        ResponseEntity<List<UserDTO>> response = userController.searchUsers("notfound", 5);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void searchUsers_shouldReturnLimitedResults_whenMoreUsersFoundThanLimit() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Alice");
        user1.setEmail("alice@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Bob");
        user2.setEmail("bob@example.com");

        User user3 = new User();
        user3.setId(3L);
        user3.setName("Charlie");
        user3.setEmail("charlie@example.com");

        List<User> users = List.of(user1, user2, user3);

        when(userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase("a", "a"))
                .thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.searchUsers("a", 2);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Alice", response.getBody().get(0).name());
        assertEquals("Bob", response.getBody().get(1).name());
    }

    @Test
    void searchUsers_shouldReturnAllResults_whenUsersFoundLessThanLimit() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Alice");
        user1.setEmail("alice@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Bob");
        user2.setEmail("bob@example.com");

        List<User> users = List.of(user1, user2);

        when(userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase("b", "b"))
                .thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.searchUsers("b", 5);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Alice", response.getBody().get(0).name());
        assertEquals("Bob", response.getBody().get(1).name());
    }

}