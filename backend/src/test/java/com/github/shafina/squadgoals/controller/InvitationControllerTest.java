package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.InvitationDTO;
import com.github.shafina.squadgoals.dto.PaginatedResponse;
import com.github.shafina.squadgoals.enums.InvitationStatus;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Invitation;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.GoalRepository;
import com.github.shafina.squadgoals.repository.InvitationRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvitationControllerTest {

    private InvitationRepository invitationRepository;
    private GoalRepository goalRepository;
    private UserRepository userRepository;
    private InvitationController invitationController;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        invitationRepository = mock(InvitationRepository.class);
        goalRepository = mock(GoalRepository.class);
        userRepository = mock(UserRepository.class);
        authentication = mock(Authentication.class);
        invitationController = new InvitationController(invitationRepository, goalRepository, userRepository);
    }

    @Test
    void getInvitations_returnsPendingInvitations() {
        String firebaseInvitedUid = "uid123";
        User invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setFirebaseUid(firebaseInvitedUid);

        String firebaseInviterUid = "uid123";
        User inviterUser = new User();
        inviterUser.setId(2L);
        inviterUser.setFirebaseUid(firebaseInviterUid);

        Goal goal = new Goal();
        goal.setId(5L);
        goal.setSquad(new HashSet<>());
        goal.setCreatedBy(inviterUser);

        Invitation invitation = new Invitation();
        invitation.setId(10L);
        invitation.setInviter(inviterUser);
        invitation.setInvitedUser(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(InvitationStatus.PENDING);

        when(authentication.getName()).thenReturn(firebaseInvitedUid);
        when(userRepository.findByFirebaseUid(firebaseInvitedUid)).thenReturn(Optional.of(invitedUser));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Invitation> invitationPage = new PageImpl<>(List.of(invitation), pageable, 1);

        when(invitationRepository.findAllByInvitedUserAndStatus(invitedUser, InvitationStatus.PENDING, pageable))
                .thenReturn(invitationPage);

        ResponseEntity<PaginatedResponse<InvitationDTO>> response = invitationController.getInvitations(
                "pending", pageable, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedResponse<InvitationDTO> body = response.getBody();

        assertNotNull(body);
        assertEquals(1, body.totalElements());
        assertEquals(InvitationStatus.PENDING, body.content().get(0).status());
    }

    @Test
    void getInvitations_returnsAcceptedInvitations() {
        String firebaseInvitedUid = "uid123";
        User invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setFirebaseUid(firebaseInvitedUid);

        String firebaseInviterUid = "uid123";
        User inviterUser = new User();
        inviterUser.setId(2L);
        inviterUser.setFirebaseUid(firebaseInviterUid);

        Goal goal = new Goal();
        goal.setId(5L);
        goal.setSquad(new HashSet<>());
        goal.setCreatedBy(inviterUser);

        Invitation invitation = new Invitation();
        invitation.setId(10L);
        invitation.setInviter(inviterUser);
        invitation.setInvitedUser(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(InvitationStatus.ACCEPTED);

        when(authentication.getName()).thenReturn(firebaseInvitedUid);
        when(userRepository.findByFirebaseUid(firebaseInvitedUid)).thenReturn(Optional.of(invitedUser));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Invitation> invitationPage = new PageImpl<>(List.of(invitation), pageable, 1);

        when(invitationRepository.findAllByInvitedUserAndStatus(invitedUser, InvitationStatus.ACCEPTED, pageable))
                .thenReturn(invitationPage);

        ResponseEntity<PaginatedResponse<InvitationDTO>> response = invitationController.getInvitations(
                "accepted", pageable, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedResponse<InvitationDTO> body = response.getBody();

        assertNotNull(body);
        assertEquals(1, body.totalElements());
        assertEquals(InvitationStatus.ACCEPTED, body.content().get(0).status());
    }

    @Test
    void getInvitations_returnsDeclinedInvitations() {
        String firebaseInvitedUid = "uid123";
        User invitedUser = new User();
        invitedUser.setId(1L);
        invitedUser.setFirebaseUid(firebaseInvitedUid);

        String firebaseInviterUid = "uid123";
        User inviterUser = new User();
        inviterUser.setId(2L);
        inviterUser.setFirebaseUid(firebaseInviterUid);

        Goal goal = new Goal();
        goal.setId(5L);
        goal.setSquad(new HashSet<>());
        goal.setCreatedBy(inviterUser);

        Invitation invitation = new Invitation();
        invitation.setId(10L);
        invitation.setInviter(inviterUser);
        invitation.setInvitedUser(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(InvitationStatus.DECLINED);

        when(authentication.getName()).thenReturn(firebaseInvitedUid);
        when(userRepository.findByFirebaseUid(firebaseInvitedUid)).thenReturn(Optional.of(invitedUser));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Invitation> invitationPage = new PageImpl<>(List.of(invitation), pageable, 1);

        when(invitationRepository.findAllByInvitedUserAndStatus(invitedUser, InvitationStatus.DECLINED, pageable))
                .thenReturn(invitationPage);

        ResponseEntity<PaginatedResponse<InvitationDTO>> response = invitationController.getInvitations(
                "declined", pageable, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaginatedResponse<InvitationDTO> body = response.getBody();

        assertNotNull(body);
        assertEquals(1, body.totalElements());
        assertEquals(InvitationStatus.DECLINED, body.content().get(0).status());
    }

    @Test
    void getInvitations_userNotFound_throwsException() {
        String firebaseUid = "uid123";

        Pageable pageable = PageRequest.of(0, 10);

        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> invitationController.getInvitations("pending", pageable, authentication));
    }

    @Test
    void acceptInvitation_success() {
        Long invitationId = 1L;
        String firebaseUid = "uid123";
        User invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setFirebaseUid(firebaseUid);

        Goal goal = new Goal();
        goal.setId(5L);
        goal.setSquad(new HashSet<>());

        Invitation invitation = new Invitation();
        invitation.setId(invitationId);
        invitation.setInvitedUser(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(InvitationStatus.PENDING);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(invitedUser));

        ResponseEntity<Void> response = invitationController.acceptInvitation(invitationId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        assertTrue(goal.getSquad().contains(invitedUser));
        verify(invitationRepository).save(invitation);
        verify(goalRepository).save(goal);
    }

    @Test
    void acceptInvitation_forbidden() {
        Long invitationId = 1L;
        String firebaseUid = "uid123";
        User invitedUser = new User();
        invitedUser.setId(2L);

        User authUser = new User();
        authUser.setId(3L);
        authUser.setFirebaseUid(firebaseUid);

        Invitation invitation = new Invitation();
        invitation.setId(invitationId);
        invitation.setInvitedUser(invitedUser);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(authUser));

        ResponseEntity<Void> response = invitationController.acceptInvitation(invitationId, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void acceptInvitation_invitationNotFound() {
        Long invitationId = 1L;
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> invitationController.acceptInvitation(invitationId, authentication));
    }

    @Test
    void declineInvitation_success() {
        Long invitationId = 1L;
        String firebaseUid = "uid123";
        User invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setFirebaseUid(firebaseUid);

        Invitation invitation = new Invitation();
        invitation.setId(invitationId);
        invitation.setInvitedUser(invitedUser);
        invitation.setStatus(InvitationStatus.PENDING);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(invitedUser));

        ResponseEntity<Void> response = invitationController.declineInvitation(invitationId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(InvitationStatus.DECLINED, invitation.getStatus());
        verify(invitationRepository).save(invitation);
    }

    @Test
    void declineInvitation_forbidden() {
        Long invitationId = 1L;
        String firebaseUid = "uid123";
        User invitedUser = new User();
        invitedUser.setId(2L);

        User authUser = new User();
        authUser.setId(3L);
        authUser.setFirebaseUid(firebaseUid);

        Invitation invitation = new Invitation();
        invitation.setId(invitationId);
        invitation.setInvitedUser(invitedUser);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(authUser));

        ResponseEntity<Void> response = invitationController.declineInvitation(invitationId, authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void declineInvitation_invitationNotFound() {
        Long invitationId = 1L;
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> invitationController.declineInvitation(invitationId, authentication));
    }
}
