package com.github.shafina.squadgoals.model;

import com.github.shafina.squadgoals.enums.Frequency;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GoalTest {

    @Test
    void testIdGetterSetter() {
        Goal goal = new Goal();
        goal.setId(42L);
        assertEquals(42L, goal.getId());
    }

    @Test
    void testTitleGetterSetter() {
        Goal goal = new Goal();
        goal.setTitle("Test Goal");
        assertEquals("Test Goal", goal.getTitle());
    }

    @Test
    void testDescriptionGetterSetter() {
        Goal goal = new Goal();
        goal.setDescription("A description");
        assertEquals("A description", goal.getDescription());
    }

    @Test
    void testCreatedByGetterSetter() {
        Goal goal = new Goal();
        User user = Mockito.mock(User.class);
        goal.setCreatedBy(user);
        assertEquals(user, goal.getCreatedBy());
    }

    @Test
    void testStartAtGetterSetter() {
        Goal goal = new Goal();
        LocalDateTime now = LocalDateTime.now();
        goal.setStartAt(now);
        assertEquals(now, goal.getStartAt());
    }

    @Test
    void testFrequencyGetterSetter() {
        Goal goal = new Goal();
        goal.setFrequency(Frequency.WEEKLY);
        assertEquals(Frequency.WEEKLY, goal.getFrequency());
    }

    @Test
    void testSquadGetterSetter() {
        Goal goal = new Goal();
        User user1 = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        Set<User> squad = new HashSet<>();
        squad.add(user1);
        squad.add(user2);
        goal.setSquad(squad);
        assertEquals(squad, goal.getSquad());
    }

    @Test
    void testTagsGetterSetter() {
        Goal goal = new Goal();
        Tag tag1 = Mockito.mock(Tag.class);
        Tag tag2 = Mockito.mock(Tag.class);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);
        goal.setTags(tags);
        assertEquals(tags, goal.getTags());
    }

    @Test
    void testTimezoneGetterSetter() {
        Goal goal = new Goal();
        goal.setTimezone("UTC");
        assertEquals("UTC", goal.getTimezone());
    }

    @Test
    void testCreatedAtGetterSetter() {
        Goal goal = new Goal();
        LocalDateTime created = LocalDateTime.now();
        goal.setCreatedAt(created);
        assertEquals(created, goal.getCreatedAt());
    }

    @Test
    void testUpdatedAtGetterSetter() {
        Goal goal = new Goal();
        LocalDateTime updated = LocalDateTime.now();
        goal.setUpdatedAt(updated);
        assertEquals(updated, goal.getUpdatedAt());
    }

    @Test
    void testDefaultSquadAndTagsAreNotNullAndEmpty() {
        Goal goal = new Goal();
        assertNotNull(goal.getSquad());
        assertTrue(goal.getSquad().isEmpty());
        assertNotNull(goal.getTags());
        assertTrue(goal.getTags().isEmpty());
    }
}