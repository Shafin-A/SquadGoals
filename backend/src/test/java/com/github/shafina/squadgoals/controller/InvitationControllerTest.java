package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.InvitationDTO;
import com.github.shafina.squadgoals.enums.Status;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Invitation;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.GoalRepository;
import com.github.shafina.squadgoals.repository.InvitationRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        inviterUser.setId(1L);
        inviterUser.setFirebaseUid(firebaseInviterUid);

        Goal goal = new Goal();
        goal.setId(5L);
        goal.setSquad(new HashSet<>());

        Invitation invitation = new Invitation();
        invitation.setId(10L);
        invitation.setInviter(inviterUser);
        invitation.setInvitedUser(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(Status.PENDING);

        when(authentication.getName()).thenReturn(firebaseInvitedUid);
        when(userRepository.findByFirebaseUid(firebaseInvitedUid)).thenReturn(Optional.of(invitedUser));
        when(invitationRepository.findByInvitedUserAndStatus(invitedUser, Status.PENDING)).thenReturn(List.of(invitation));

        ResponseEntity<List<InvitationDTO>> response = invitationController.getInvitations(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<InvitationDTO> body = response.getBody();

        assertNotNull(body);
        assertEquals(1, body.size());
    }

    @Test
    void getInvitations_userNotFound_throwsException() {
        String firebaseUid = "uid123";
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            invitationController.getInvitations(authentication);
        });
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
        invitation.setStatus(Status.PENDING);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(invitedUser));

        ResponseEntity<Void> response = invitationController.acceptInvitation(invitationId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Status.ACCEPTED, invitation.getStatus());
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
        assertThrows(ResponseStatusException.class, () -> {
            invitationController.acceptInvitation(invitationId, authentication);
        });
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
        invitation.setStatus(Status.PENDING);

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitation));
        when(authentication.getName()).thenReturn(firebaseUid);
        when(userRepository.findByFirebaseUid(firebaseUid)).thenReturn(Optional.of(invitedUser));

        ResponseEntity<Void> response = invitationController.declineInvitation(invitationId, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Status.DECLINED, invitation.getStatus());
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
        assertThrows(ResponseStatusException.class, () -> {
            invitationController.declineInvitation(invitationId, authentication);
        });
    }
}
