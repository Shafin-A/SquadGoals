package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateGoalRequest;
import com.github.shafina.squadgoals.dto.GoalDTO;
import com.github.shafina.squadgoals.enums.Frequency;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Tag;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoalControllerTest {

    private GoalRepository goalRepository;
    private UserRepository userRepository;
    private TagRepository tagRepository;
    private InvitationRepository invitationRepository;
    private NotificationRepository notificationRepository;
    private GoalController goalController;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        goalRepository = mock(GoalRepository.class);
        userRepository = mock(UserRepository.class);
        tagRepository = mock(TagRepository.class);
        invitationRepository = mock(InvitationRepository.class);
        notificationRepository = mock(NotificationRepository.class);
        authentication = mock(Authentication.class);
        goalController = new GoalController(goalRepository, userRepository, tagRepository, invitationRepository, notificationRepository);
    }

    @Test
    void createGoal_shouldCreateGoal_whenValidRequest() {
        String firebaseUid = "firebase-uid";
        when(authentication.getName()).thenReturn(firebaseUid);

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));

        Tag tag1 = new Tag();
        tag1.setId(10L);
        tag1.setName("reading");
        when(tagRepository.findByName("reading")).thenReturn(Optional.of(tag1));

        Tag tag2 = new Tag();
        tag2.setId(11L);
        tag2.setName("habit");
        when(tagRepository.findByName("habit")).thenReturn(Optional.of(tag2));

        User squadUser = new User();
        squadUser.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(squadUser));

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Read Books");
        request.setDescription("Read 30 minutes daily");
        request.setTimezone("America/New_York");
        request.setStartAt(LocalDateTime.now());
        request.setFrequency(Frequency.DAILY);
        request.setTagNames(Set.of("reading", "habit"));
        request.setSquadUserIds(Set.of(2L));

        Goal savedGoal = new Goal();
        savedGoal.setId(100L);
        savedGoal.setTitle("Read Books");
        savedGoal.setDescription("Read 30 minutes daily");
        savedGoal.setTimezone("America/New_York");
        savedGoal.setStartAt(LocalDateTime.now());
        savedGoal.setFrequency(Frequency.DAILY);
        savedGoal.setTags(Set.of(tag1, tag2));
        savedGoal.setSquad(Set.of(creator, squadUser));
        savedGoal.setCreatedBy(creator);

        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        ResponseEntity<GoalDTO> response = goalController.createGoal(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(savedGoal.getId(), response.getBody().id());
        assertEquals(savedGoal.getTitle(), response.getBody().title());
        assertEquals(savedGoal.getDescription(), response.getBody().description());
        assertEquals(savedGoal.getCreatedBy().getId(), response.getBody().createdBy().id());

        verify(invitationRepository, times(1))
                .save(argThat(invitation -> invitation.getInvitedUser().equals(squadUser) &&
                        invitation.getInviter().equals(creator) &&
                        invitation.getGoal().equals(savedGoal)));

        verify(notificationRepository, times(1))
                .save(argThat(notification -> notification.getUser().equals(squadUser) &&
                        notification.getMessage().equals(creator.getName() + " has invited you to join their goal - " + savedGoal.getTitle() + "!")));
    }

    @Test
    void createGoal_shouldCreateNewTags_whenTagDoesNotExist() {
        String firebaseUid = "firebase-uid";
        when(authentication.getName()).thenReturn(firebaseUid);

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));

        when(tagRepository.findByName("guitar")).thenReturn(Optional.empty());
        when(tagRepository.findByName("music")).thenReturn(Optional.empty());

        Tag tag1 = new Tag();
        tag1.setId(100L);
        tag1.setName("guitar");
        Tag tag2 = new Tag();
        tag2.setId(101L);
        tag2.setName("music");

        when(tagRepository.save(argThat(tag -> tag != null && "guitar".equals(tag.getName())))).thenReturn(tag1);
        when(tagRepository.save(argThat(tag -> tag != null && "music".equals(tag.getName())))).thenReturn(tag2);

        User squadUser = new User();
        squadUser.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(squadUser));

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Learn Guitar");
        request.setDescription("Practice chords daily");
        request.setTimezone("America/Chicago");
        request.setStartAt(LocalDateTime.now());
        request.setFrequency(Frequency.DAILY);
        request.setTagNames(Set.of("guitar", "music"));
        request.setSquadUserIds(Set.of(2L));

        Goal savedGoal = new Goal();
        savedGoal.setId(200L);
        savedGoal.setTitle("Learn Guitar");
        savedGoal.setDescription("Practice chords daily");
        savedGoal.setTimezone("America/Chicago");
        savedGoal.setStartAt(LocalDateTime.now());
        savedGoal.setFrequency(Frequency.DAILY);
        savedGoal.setTags(Set.of(tag1, tag2));
        savedGoal.setSquad(Set.of(creator, squadUser));
        savedGoal.setCreatedBy(creator);

        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        ResponseEntity<GoalDTO> response = goalController.createGoal(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(savedGoal.getId(), response.getBody().id());
        assertEquals(savedGoal.getTitle(), response.getBody().title());
        assertEquals(savedGoal.getDescription(), response.getBody().description());
        assertEquals(savedGoal.getCreatedBy().getId(), response.getBody().createdBy().id());

        verify(tagRepository, times(1)).save(argThat(tag -> tag.getName().equals("guitar")));
        verify(tagRepository, times(1)).save(argThat(tag -> tag.getName().equals("music")));
        verify(invitationRepository, times(1))
                .save(argThat(invitation -> invitation.getInvitedUser().equals(squadUser) &&
                        invitation.getInviter().equals(creator) &&
                        invitation.getGoal().equals(savedGoal)));
        verify(notificationRepository, times(1))
                .save(argThat(notification -> notification.getUser().equals(squadUser) &&
                        notification.getMessage().equals(creator.getName() + " has invited you to join their goal - " + savedGoal.getTitle() + "!")));
    }

    @Test
    void createGoal_shouldReturnNotFound_whenCreatorUserNotFound() {
        String firebaseUid = "notfound-uid";
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.empty());

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> goalController.createGoal(request, authentication));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertNotNull(ex.getReason());
        assertTrue(ex.getReason().contains("User not found"));
    }

    @Test
    void createGoal_shouldReturnNotFound_whenSquadUserNotFound() {
        String firebaseUid = "firebase-uid";
        when(authentication.getName()).thenReturn(firebaseUid);

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(new Tag());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");
        request.setTagNames(Set.of("tag"));
        request.setSquadUserIds(Set.of(2L));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> goalController.createGoal(request, authentication));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertNotNull(ex.getReason());
        assertTrue(ex.getReason().contains("User not found with ID: 2"));
    }

    @Test
    void createGoal_shouldSetIsPublicTrue_whenPublicTrueProvided() {
        String firebaseUid = "firebase-uid";
        when(authentication.getName()).thenReturn(firebaseUid);

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");
        request.setPublic(true);

        Goal savedGoal = new Goal();
        savedGoal.setId(1L);
        savedGoal.setCreatedBy(creator);

        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            assertTrue(g.getPublic());
            return savedGoal;
        });

        ResponseEntity<GoalDTO> response = goalController.createGoal(request, authentication);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createGoal_shouldSetIsPublicFalse_whenPublicFalseProvided() {
        String firebaseUid = "firebase-uid";
        when(authentication.getName()).thenReturn(firebaseUid);

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");
        request.setPublic(false);

        Goal savedGoal = new Goal();
        savedGoal.setId(1L);
        savedGoal.setCreatedBy(creator);

        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            assertFalse(g.getPublic());
            return savedGoal;
        });

        ResponseEntity<GoalDTO> response = goalController.createGoal(request, authentication);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createGoal_shouldDefaultIsPublicTrue_whenPublicNull() {
        String firebaseUid = "firebase-uid";
        when(authentication.getName()).thenReturn(firebaseUid);

        User creator = new User();
        creator.setId(1L);
        creator.setFirebaseUid(firebaseUid);

        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(creator));

        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Test Goal");
        request.setPublic(null);

        Goal savedGoal = new Goal();
        savedGoal.setId(1L);
        savedGoal.setCreatedBy(creator);

        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> {
            Goal g = invocation.getArgument(0);
            assertTrue(g.getPublic());
            return savedGoal;
        });

        ResponseEntity<GoalDTO> response = goalController.createGoal(request, authentication);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getPublicGoals_shouldReturnOnlyPublicGoals() {
        String firebaseUid = "firebase-uid";
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

        ResponseEntity<List<GoalDTO>> response = goalController.getPublicGoals(true, 10);
        List<GoalDTO> goals = response.getBody();

        assertNotNull(goals);
        assertEquals(1, goals.size());
        assertEquals("Public Goal", goals.get(0).title());
    }
}
