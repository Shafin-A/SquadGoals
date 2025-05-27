package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.enums.Frequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public class CreateGoalRequest {
    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    @NotNull(message = "StartAt is required")
    private LocalDateTime startAt;

    @NotNull(message = "Frequency is required")
    private Frequency frequency;

    private Set<String> tagNames;

    private Set<Long> squadUserIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Set<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(Set<String> tagNames) {
        this.tagNames = tagNames;
    }

    public Set<Long> getSquadUserIds() {
        return squadUserIds;
    }

    public void setSquadUserIds(Set<Long> squadUserIds) {
        this.squadUserIds = squadUserIds;
    }
}
