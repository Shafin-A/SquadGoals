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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists."));
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
                .andExpect(jsonPath("$.timezone").value("Europe/London"))
                .andExpect(jsonPath("$.firebaseUid").value(firebaseUid));
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
}