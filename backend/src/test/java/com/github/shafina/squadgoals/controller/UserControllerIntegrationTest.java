package com.github.shafina.squadgoals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shafina.squadgoals.config.SecurityConfig;
import com.github.shafina.squadgoals.dto.CreateUserRequest;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.UserRepository;
import com.github.shafina.squadgoals.security.FirebaseAuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FirebaseAuthProvider firebaseAuthProvider;

    @Test
    void createUser_shouldReturnConflict_ifUserExists() throws Exception {
        String firebaseUid = "existing-firebase-uid";

        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setTimezone("America/New_York");
        request.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByFirebaseUid(firebaseUid)).thenReturn(true);

        mockMvc.perform(post("/api/users")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void createUser_shouldCreateUser_whenUserDoesNotExist() throws Exception {
        String firebaseUid = "new-firebase-uid";

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

        when(userRepository.existsByFirebaseUid(firebaseUid)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.timezone").value("Europe/London"));
    }

    @Test
    void createUser_shouldReturnUnauthorized_whenNoAuthentication() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setTimezone("America/New_York");
        request.setCreatedAt(LocalDateTime.now());

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createUser_shouldReturnBadRequest_whenMissingRequiredFields() throws Exception {
        CreateUserRequest request = new CreateUserRequest();

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void searchUsers_shouldReturnBadRequest_whenLimitIsLessThanOne() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("query", "test")
                        .param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchUsers_shouldReturnEmptyList_whenNoUsersFound() throws Exception {
        String firebaseUid = "firebaseUid";

        User user = new User();

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/search")
                        .with(user(firebaseUid).roles("USER"))
                        .param("query", "test")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchUsers_shouldNotReturnAuthUserInResults() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Alice");
        user1.setEmail("alice@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Bob");
        user2.setEmail("bob@example.com");

        String firebaseUid = "firebaseUid";

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(user1));

        when(userRepository.searchUsersExcludingCurrent("example", 1L, PageRequest.of(0, 5)))
                .thenReturn(List.of(user2));

        mockMvc.perform(get("/api/users/search")
                        .with(user(firebaseUid).roles("USER"))
                        .param("query", "example")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Bob"));
    }

    @Test
    void searchUser_shouldReturnUnauthorized_whenNoAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("query", "test")
                        .param("limit", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void searchUser_shouldReturnBadRequest_whenMissingRequiredQuery() throws Exception {
        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isBadRequest());
    }
}