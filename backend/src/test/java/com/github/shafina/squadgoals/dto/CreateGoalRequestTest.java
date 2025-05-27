package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.enums.Frequency;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateGoalRequestTest {

    @Test
    void testTitleGetterSetter() {
        CreateGoalRequest req = new CreateGoalRequest();
        req.setTitle("My Goal");
        assertEquals("My Goal", req.getTitle());
    }

    @Test
    void testDescriptionGetterSetter() {
        CreateGoalRequest req = new CreateGoalRequest();
        req.setDescription("Description here");
        assertEquals("Description here", req.getDescription());
    }

    @Test
    void testTimezoneGetterSetter() {
        CreateGoalRequest req = new CreateGoalRequest();
        req.setTimezone("UTC");
        assertEquals("UTC", req.getTimezone());
    }

    @Test
    void testStartAtGetterSetter() {
        CreateGoalRequest req = new CreateGoalRequest();
        LocalDateTime now = LocalDateTime.now();
        req.setStartAt(now);
        assertEquals(now, req.getStartAt());
    }

    @Test
    void testFrequencyGetterSetter() {
        CreateGoalRequest req = new CreateGoalRequest();
        req.setFrequency(Frequency.DAILY);
        assertEquals(Frequency.DAILY, req.getFrequency());
    }

    @Test
    void testTagNamesGetterSetter() {
        CreateGoalRequest req = new CreateGoalRequest();
        Set<String> tags = Set.of("health", "fitness");
        req.setTagNames(tags);
        assertEquals(tags, req.getTagNames());
    }

    @Test
    void testSquadUserIdsGetterSetter() {
        CreateGoalRequest req = new CreateGoalRequest();
        Set<Long> ids = Set.of(1L, 2L, 3L);
        req.setSquadUserIds(ids);
        assertEquals(ids, req.getSquadUserIds());
    }
}
