package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateGoalRequest;
import com.github.shafina.squadgoals.dto.GoalDTO;
import com.github.shafina.squadgoals.enums.Status;
import com.github.shafina.squadgoals.model.*;
import com.github.shafina.squadgoals.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final InvitationRepository invitationRepository;
    private final NotificationRepository notificationRepository;

    public GoalController(GoalRepository goalRepository, UserRepository userRepository, TagRepository tagRepository,
                          InvitationRepository invitationRepository, NotificationRepository notificationRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.invitationRepository = invitationRepository;
        this.notificationRepository = notificationRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<GoalDTO> createGoal(@Valid @RequestBody CreateGoalRequest createGoalRequest,
            Authentication authentication) {
        String firebaseUid = authentication.getName();

        User creator = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Goal goal = new Goal();
        goal.setCreatedBy(creator);

        goal.setTitle(createGoalRequest.getTitle());
        goal.setDescription(createGoalRequest.getDescription());
        goal.setTimezone(createGoalRequest.getTimezone());
        goal.setStartAt(createGoalRequest.getStartAt());
        goal.setFrequency(createGoalRequest.getFrequency());

        Set<Tag> tagEntities = Optional.ofNullable(createGoalRequest.getTagNames())
                .orElse(Collections.emptySet())
                .stream()
                .map(tagName -> tagRepository.findByName(tagName).orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    return tagRepository.save(tag);
                }))
                .collect(Collectors.toSet());

        goal.setTags(tagEntities);

        Boolean isPublic = Optional.ofNullable(createGoalRequest.getPublic()).orElse(true);
        goal.setPublic(isPublic);

        goal.setSquad(Set.of(creator));
        Goal savedGoal = goalRepository.save(goal);

        Optional.ofNullable(createGoalRequest.getSquadUserIds())
                .orElse(Collections.emptySet())
                .stream()
                .filter(userId -> !userId.equals(creator.getId()))
                .forEach(userId -> {
                    User invitedUser = userRepository.findById(userId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "User not found with ID: " + userId));
                    Invitation invitation = new Invitation();
                    invitation.setGoal(savedGoal);
                    invitation.setInvitedUser(invitedUser);
                    invitation.setInviter(creator);
                    invitation.setStatus(Status.PENDING);
                    invitationRepository.save(invitation);

                    Notification notification = new Notification();
                    notification.setMessage(creator.getName() + " has invited you to join their goal - " + goal.getTitle() + "!");
                    notification.setUser(invitedUser);
                    notification.setSender(creator);
                    notificationRepository.save(notification);

                });

        return ResponseEntity.status(HttpStatus.CREATED).body(GoalDTO.from(savedGoal));
    }

    @GetMapping
    public ResponseEntity<List<GoalDTO>> getPublicGoals(
            @RequestParam(defaultValue = "true") boolean recent,
            @RequestParam(defaultValue = "10") int limit) {
        List<Goal> goals = goalRepository.findByIsPublicTrue();

        if (recent) {
            goals = goals
                    .stream()
                    .sorted((g1, g2) -> g2.getCreatedAt().compareTo(g1.getCreatedAt()))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else {
            goals = goals
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(
                goals
                        .stream()
                        .map(GoalDTO::from)
                        .collect(Collectors.toList()));
    }
}
