package com.github.shafina.squadgoals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.shafina.squadgoals.config.SecurityConfig;
import com.github.shafina.squadgoals.dto.CreateGoalRequest;
import com.github.shafina.squadgoals.enums.Frequency;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Tag;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.*;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = GoalController.class)
public class GoalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private GoalRepository goalRepository;

    @MockitoBean
    private TagRepository tagRepository;

    @MockitoBean
    private InvitationRepository invitationRepository;

    @MockitoBean
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FirebaseAuthProvider firebaseAuthProvider;

    @Test
    void createGoal_shouldCreateGoal_whenValidRequest() throws Exception {
        String firebaseUid = "test-firebase-uid";

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Read Books");
        request.setDescription("Read 30 minutes daily");
        request.setTimezone("America/New_York");
        LocalDateTime now = LocalDateTime.now();
        request.setStartAt(now);
        request.setFrequency(Frequency.DAILY);
        request.setTagNames(Set.of("reading", "habit"));
        request.setSquadUserIds(Set.of(2L, 3L));

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        User squadUser1 = new User();
        squadUser1.setId(2L);
        User squadUser2 = new User();
        squadUser2.setId(3L);

        Tag tag1 = new Tag();
        tag1.setId(10L);
        tag1.setName("reading");

        Tag tag2 = new Tag();
        tag2.setId(11L);
        tag2.setName("habit");

        Goal savedGoal = new Goal();
        savedGoal.setId(100L);
        savedGoal.setTitle(request.getTitle());
        savedGoal.setDescription(request.getDescription());
        savedGoal.setTimezone(request.getTimezone());
        savedGoal.setCreatedBy(creator);
        savedGoal.setTags(Set.of(tag1, tag2));
        savedGoal.setSquad(Set.of(squadUser1, squadUser2));

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(squadUser1));
        when(userRepository.findById(3L)).thenReturn(Optional.of(squadUser2));
        when(tagRepository.findByName("reading")).thenReturn(Optional.of(tag1));
        when(tagRepository.findByName("habit")).thenReturn(Optional.of(tag2));
        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        mockMvc.perform(post("/api/goals")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Read Books"))
                .andExpect(jsonPath("$.description").value("Read 30 minutes daily"))
                .andExpect(jsonPath("$.timezone").value("America/New_York"))
                .andExpect(jsonPath("$.id").value(100L));

        verify(invitationRepository, times(1)).save(argThat(invitation ->
                invitation.getInvitedUser().equals(squadUser1) &&
                        invitation.getInviter().equals(creator) &&
                        invitation.getGoal().equals(savedGoal)
        ));

        verify(invitationRepository, times(1)).save(argThat(invitation ->
                invitation.getInvitedUser().equals(squadUser1) &&
                        invitation.getInviter().equals(creator) &&
                        invitation.getGoal().equals(savedGoal)
        ));

        verify(notificationRepository, times(1))
                .save(argThat(notification -> notification.getUser().equals(squadUser1) &&
                        notification.getMessage().equals(creator.getName() + " has invited you to join their goal - " + savedGoal.getTitle() + "!")));

        verify(invitationRepository, times(1)).save(argThat(invitation ->
                invitation.getInvitedUser().equals(squadUser2) &&
                        invitation.getInviter().equals(creator) &&
                        invitation.getGoal().equals(savedGoal)
        ));

        verify(notificationRepository, times(1))
                .save(argThat(notification -> notification.getUser().equals(squadUser2) &&
                        notification.getMessage().equals(creator.getName() + " has invited you to join their goal - " + savedGoal.getTitle() + "!")));
    }

    @Test
    void createGoal_shouldCreateNewTags_whenTagDoesNotExist() throws Exception {
        String firebaseUid = "test-firebase-uid";

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Learn Guitar");
        request.setDescription("Practice chords daily");
        request.setTimezone("America/Chicago");
        LocalDateTime now = LocalDateTime.now();
        request.setStartAt(now);
        request.setFrequency(Frequency.DAILY);
        request.setTagNames(Set.of("guitar", "music"));
        request.setSquadUserIds(Set.of(2L));

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        User squadUser = new User();
        squadUser.setId(2L);

        Tag createdTag1 = new Tag();
        createdTag1.setId(100L);
        createdTag1.setName("guitar");

        Tag createdTag2 = new Tag();
        createdTag2.setId(101L);
        createdTag2.setName("music");

        Goal savedGoal = new Goal();
        savedGoal.setId(200L);
        savedGoal.setTitle(request.getTitle());
        savedGoal.setDescription(request.getDescription());
        savedGoal.setTimezone(request.getTimezone());
        savedGoal.setCreatedBy(creator);
        savedGoal.setTags(Set.of(createdTag1, createdTag2));
        savedGoal.setSquad(Set.of(squadUser));

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(squadUser));

        when(tagRepository.findByName("guitar")).thenReturn(Optional.empty());
        when(tagRepository.findByName("music")).thenReturn(Optional.empty());

        when(tagRepository.save(argThat(tag -> tag != null && "guitar".equals(tag.getName())))).thenReturn(createdTag1);
        when(tagRepository.save(argThat(tag -> tag != null && "music".equals(tag.getName())))).thenReturn(createdTag2);

        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        mockMvc.perform(post("/api/goals")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Learn Guitar"))
                .andExpect(jsonPath("$.description").value("Practice chords daily"))
                .andExpect(jsonPath("$.tags[0]").value("music"))
                .andExpect(jsonPath("$.tags[1]").value("guitar"));

        verify(tagRepository, times(1)).save(argThat(tag -> tag.getName().equals("guitar")));
        verify(tagRepository, times(1)).save(argThat(tag -> tag.getName().equals("music")));

        verify(invitationRepository, times(1)).save(argThat(invitation ->
                invitation.getInvitedUser().equals(squadUser) &&
                        invitation.getInviter().equals(creator) &&
                        invitation.getGoal().equals(savedGoal)
        ));

        verify(notificationRepository, times(1))
                .save(argThat(notification -> notification.getUser().equals(squadUser) &&
                        notification.getMessage().equals(creator.getName() + " has invited you to join their goal - " + savedGoal.getTitle() + "!")));
    }

    @Test
    void createGoal_shouldReturnNotFound_whenCreatorUserNotFound() throws Exception {
        String firebaseUid = "nonexistent-firebase-uid";

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Learn Guitar");
        request.setDescription("Practice 30 minutes a day");
        request.setTimezone("America/Toronto");
        request.setStartAt(LocalDateTime.now());
        request.setFrequency(Frequency.DAILY);
        request.setTagNames(Set.of("music"));
        request.setSquadUserIds(Set.of(1L, 2L));

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/goals")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createGoal_shouldReturnNotFound_whenSquadUserNotFound() throws Exception {
        String firebaseUid = "existing-firebase-uid";

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Learn Guitar");
        request.setDescription("Practice 30 minutes a day");
        request.setTimezone("America/Toronto");
        request.setStartAt(LocalDateTime.now());
        request.setFrequency(Frequency.DAILY);
        request.setTagNames(Set.of("music"));
        request.setSquadUserIds(Set.of(999L));

        User creator = new User();
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/goals")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createGoal_shouldReturnBadRequest_whenMissingRequiredFields() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest();

        mockMvc.perform(post("/api/goals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createGoal_shouldReturnUnauthorized_whenNoAuthentication() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Read Books");
        request.setDescription("Read 30 minutes daily");
        request.setTimezone("America/New_York");
        LocalDateTime now = LocalDateTime.now();
        request.setStartAt(now);
        request.setFrequency(Frequency.DAILY);
        request.setTagNames(Set.of("reading", "habit"));
        request.setSquadUserIds(Set.of(2L, 3L));

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createGoal_shouldSetIsPublicTrue_whenPublicTrueProvided() throws Exception {
        String firebaseUid = "test-firebase-uid";
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");
        request.setDescription("desc");
        request.setTimezone("UTC");
        request.setStartAt(LocalDateTime.now());
        request.setFrequency(Frequency.DAILY);
        request.setPublic(true);
        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            assertTrue(g.getPublic());
            return g;
        });

        mockMvc.perform(post("/api/goals")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createGoal_shouldSetIsPublicFalse_whenPublicFalseProvided() throws Exception {
        String firebaseUid = "test-firebase-uid";
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");
        request.setDescription("desc");
        request.setTimezone("UTC");
        request.setStartAt(LocalDateTime.now());
        request.setFrequency(Frequency.DAILY);
        request.setPublic(false);
        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            assertFalse(g.getPublic());
            return g;
        });

        mockMvc.perform(post("/api/goals")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createGoal_shouldDefaultIsPublicTrue_whenPublicNull() throws Exception {
        String firebaseUid = "test-firebase-uid";
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");
        request.setDescription("desc");
        request.setTimezone("UTC");
        request.setStartAt(LocalDateTime.now());
        request.setFrequency(Frequency.DAILY);
        request.setPublic(null);
        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            assertTrue(g.getPublic());
            return g;
        });

        mockMvc.perform(post("/api/goals")
                        .with(user(firebaseUid).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getPublicGoals_shouldReturnOnlyPublicGoals() throws Exception {
        String firebaseUid = "test-firebase-uid";
        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        Goal publicGoal = new Goal();
        publicGoal.setId(1L);
        publicGoal.setTitle("Public Goal");
        publicGoal.setPublic(true);
        publicGoal.setCreatedBy(creator);

        Goal privateGoal = new Goal();
        privateGoal.setId(2L);
        privateGoal.setTitle("Private Goal");
        privateGoal.setPublic(false);
        privateGoal.setCreatedBy(creator);

        when(goalRepository.findByIsPublicTrue()).thenReturn(List.of(publicGoal));

        mockMvc.perform(get("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Public Goal"))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
