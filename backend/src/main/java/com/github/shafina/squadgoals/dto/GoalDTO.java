package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.enums.Frequency;
import com.github.shafina.squadgoals.model.Goal;
import com.github.shafina.squadgoals.model.Tag;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record GoalDTO(
        Long id,
        String title,
        String description,
        UserDTO createdBy,
        String timezone,
        LocalDateTime startAt,
        Frequency frequency,
        boolean isPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime nextDueAt,
        Set<String> tags,
        Set<UserDTO> squad) {
    public static GoalDTO from(Goal goal) {
        return new GoalDTO(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                UserDTO.from(goal.getCreatedBy()),
                goal.getTimezone(),
                goal.getStartAt(),
                goal.getFrequency(),
                goal.getPublic(),
                goal.getCreatedAt(),
                goal.getUpdatedAt(),
                goal.getNextDueAt(),
                goal.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
                goal.getSquad().stream().map(UserDTO::from).collect(Collectors.toSet()));
    }
}