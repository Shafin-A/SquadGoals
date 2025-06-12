package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.InvitationDTO;
import com.github.shafina.squadgoals.enums.InvitationStatus;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Invitation;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.GoalRepository;
import com.github.shafina.squadgoals.repository.InvitationRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {
    private final InvitationRepository invitationRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public InvitationController(InvitationRepository invitationRepository, GoalRepository goalRepository,
                                UserRepository userRepository) {
        this.invitationRepository = invitationRepository;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<InvitationDTO>> getInvitations(Authentication authentication) {
        String firebaseUid = authentication.getName();

        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Invitation> pendingInvitations = invitationRepository.findByInvitedUserAndStatus(user,
                InvitationStatus.PENDING);

        List<InvitationDTO> result = pendingInvitations.stream()
                .map(InvitationDTO::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{invitationId}/accept")
    @Transactional
    public ResponseEntity<Void> acceptInvitation(@PathVariable Long invitationId, Authentication authentication) {
        Invitation invitation = invitationRepository
                .findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Invitation not found"));

        User invitedUser = invitation.getInvitedUser();

        String firebaseUid = authentication.getName();

        Long authUserId = userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .getId();

        if (!authUserId.equals(invitedUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);

        Goal goal = invitation.getGoal();
        Set<User> squad = goal.getSquad();
        squad.add(invitedUser);
        goal.setSquad(squad);
        goalRepository.save(goal);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invitationId}/decline")
    @Transactional
    public ResponseEntity<Void> declineInvitation(@PathVariable Long invitationId, Authentication authentication) {
        Invitation invitation = invitationRepository
                .findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Invitation not found"));

        User invitedUser = invitation.getInvitedUser();

        String firebaseUid = authentication.getName();

        Long authUserId = userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .getId();

        if (!authUserId.equals(invitedUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        invitation.setStatus(InvitationStatus.DECLINED);
        invitationRepository.save(invitation);

        return ResponseEntity.ok().build();
    }
}
