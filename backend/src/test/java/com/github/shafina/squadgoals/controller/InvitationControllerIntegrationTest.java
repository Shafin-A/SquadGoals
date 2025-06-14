package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.config.SecurityConfig;
import com.github.shafina.squadgoals.enums.InvitationStatus;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Invitation;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.GoalRepository;
import com.github.shafina.squadgoals.repository.InvitationRepository;
import com.github.shafina.squadgoals.repository.TagRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import com.github.shafina.squadgoals.security.FirebaseAuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = InvitationController.class)
public class InvitationControllerIntegrationTest {

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
    private FirebaseAuthProvider firebaseAuthProvider;

    private User invitedUser;
    private User inviter;
    private Goal goal;
    private Invitation invitation;

    @BeforeEach
    void setup() {
        invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setFirebaseUid("firebase-uid-1");

        inviter = new User();
        inviter.setId(2L);
        inviter.setFirebaseUid("firebase-uid-2");

        goal = new Goal();
        goal.setId(10L);
        goal.setSquad(new HashSet<>());
        goal.setCreatedBy(inviter);

        invitation = new Invitation();
        invitation.setId(100L);
        invitation.setInvitedUser(invitedUser);
        invitation.setInviter(inviter);
        invitation.setGoal(goal);
        invitation.setStatus(InvitationStatus.PENDING);
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getInvitations_shouldReturnPendingInvitations() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Invitation> invitationPage = new PageImpl<>(List.of(invitation), pageable, 1);

        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));
        when(invitationRepository.findAllByInvitedUserAndStatus(eq(invitedUser), eq(InvitationStatus.PENDING), any(Pageable.class)))
                .thenReturn(invitationPage);

        mockMvc.perform(get("/api/invitations?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(invitation.getId()))
                .andExpect(jsonPath("$.content[0].status").value(InvitationStatus.PENDING.name()));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getInvitations_shouldReturnAcceptedInvitations() throws Exception {
        invitation.setStatus(InvitationStatus.ACCEPTED);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Invitation> invitationPage = new PageImpl<>(List.of(invitation), pageable, 1);

        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));
        when(invitationRepository.findAllByInvitedUserAndStatus(eq(invitedUser), eq(InvitationStatus.ACCEPTED), any(Pageable.class)))
                .thenReturn(invitationPage);

        mockMvc.perform(get("/api/invitations?status=accepted&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(invitation.getId()))
                .andExpect(jsonPath("$.content[0].status").value(InvitationStatus.ACCEPTED.name()));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getInvitations_shouldReturnDeclinedInvitations() throws Exception {
        invitation.setStatus(InvitationStatus.DECLINED);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Invitation> invitationPage = new PageImpl<>(List.of(invitation), pageable, 1);

        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));
        when(invitationRepository.findAllByInvitedUserAndStatus(eq(invitedUser), eq(InvitationStatus.DECLINED), any(Pageable.class)))
                .thenReturn(invitationPage);

        mockMvc.perform(get("/api/invitations?status=declined&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(invitation.getId()))
                .andExpect(jsonPath("$.content[0].status").value(InvitationStatus.DECLINED.name()));
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getInvitations_shouldReturnEmptyList_whenNoInvitations() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Invitation> invitationPage = new PageImpl<>(Collections.emptyList(), pageable, 1);

        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));
        when(invitationRepository.findAllByInvitedUserAndStatus(invitedUser, InvitationStatus.PENDING, pageable))
                .thenReturn(invitationPage);

        mockMvc.perform(get("/api/invitations?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void getInvitations_shouldReturnNotFound_whenUserNotFound() throws Exception {
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/invitations"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void acceptInvitation_shouldReturnOk_whenSuccess() throws Exception {
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        mockMvc.perform(post("/api/invitations/100/accept"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "firebase-uid-2")
    void acceptInvitation_shouldReturnForbidden_whenNotInvitedUser() throws Exception {
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-2")).thenReturn(Optional.of(inviter));

        mockMvc.perform(post("/api/invitations/100/accept"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void acceptInvitation_shouldReturnNotFound_whenInvitationNotFound() throws Exception {
        when(invitationRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/invitations/999/accept"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void acceptInvitation_shouldReturnNotFound_whenUserNotFound() throws Exception {
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/invitations/100/accept"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void acceptInvitation_shouldReturnOk_whenAlreadyAccepted() throws Exception {
        invitation.setStatus(InvitationStatus.ACCEPTED);
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));

        mockMvc.perform(post("/api/invitations/100/accept"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void declineInvitation_shouldReturnOk_whenSuccess() throws Exception {
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        mockMvc.perform(post("/api/invitations/100/decline"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "firebase-uid-2")
    void declineInvitation_shouldReturnForbidden_whenNotInvitedUser() throws Exception {
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-2")).thenReturn(Optional.of(inviter));

        mockMvc.perform(post("/api/invitations/100/decline"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void declineInvitation_shouldReturnNotFound_whenInvitationNotFound() throws Exception {
        when(invitationRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/invitations/999/decline"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void declineInvitation_shouldReturnNotFound_whenUserNotFound() throws Exception {
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/invitations/100/decline"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "firebase-uid-1")
    void declineInvitation_shouldReturnOk_whenAlreadyDeclined() throws Exception {
        invitation.setStatus(InvitationStatus.DECLINED);
        when(invitationRepository.findById(100L)).thenReturn(Optional.of(invitation));
        when(userRepository.findByFirebaseUid("firebase-uid-1")).thenReturn(Optional.of(invitedUser));

        mockMvc.perform(post("/api/invitations/100/decline"))
                .andExpect(status().isOk());
    }
}