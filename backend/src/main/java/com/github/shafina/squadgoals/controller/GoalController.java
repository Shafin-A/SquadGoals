package com.github.shafina.squadgoals.controller;

import com.github.shafina.squadgoals.dto.CreateGoalRequest;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Tag;
import com.github.shafina.squadgoals.model.User;
import com.github.shafina.squadgoals.repository.GoalRepository;
import com.github.shafina.squadgoals.repository.TagRepository;
import com.github.shafina.squadgoals.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public GoalController(GoalRepository goalRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @PostMapping
    public ResponseEntity<?> createGoal(@RequestBody CreateGoalRequest createGoalRequest, Authentication authentication) {
        String firebaseUid = (String) authentication.getPrincipal();

        User creator = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Goal goal = new Goal();
        goal.setCreatedBy(creator);

        goal.setTitle(createGoalRequest.getTitle());
        goal.setDescription(createGoalRequest.getDescription());
        goal.setTimezone(createGoalRequest.getTimezone());
        goal.setStartAt(createGoalRequest.getStartAt());
        goal.setFrequency(createGoalRequest.getFrequency());

        Set<Tag> tagEntities = createGoalRequest.getTagNames().stream()
                .map(name -> tagRepository.findByName(name).orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tagRepository.save(tag);
                }))
                .collect(Collectors.toSet());

        goal.setTags(tagEntities);

        Set<User> squadUsers = createGoalRequest.getSquadUserIds().stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId)))
                .collect(Collectors.toSet());
        
        goal.setSquad(squadUsers);

        Goal savedGoal = goalRepository.save(goal);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedGoal);
    }
}
