package com.github.shafina.squadgoals.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testIdGetterSetter() {
        User user = new User();
        user.setId(10L);
        assertEquals(10L, user.getId());
    }

    @Test
    void testFirebaseUidGetterSetter() {
        User user = new User();
        user.setFirebaseUid("firebase-uid-123");
        assertEquals("firebase-uid-123", user.getFirebaseUid());
    }

    @Test
    void testNameGetterSetter() {
        User user = new User();
        user.setName("Alice");
        assertEquals("Alice", user.getName());
    }

    @Test
    void testSquadGoalsGetterSetter() {
        User user = new User();
        Goal goal1 = Mockito.mock(Goal.class);
        Goal goal2 = Mockito.mock(Goal.class);
        Set<Goal> squadGoals = new HashSet<>();
        squadGoals.add(goal1);
        squadGoals.add(goal2);
        user.setSquadGoals(squadGoals);
        assertEquals(squadGoals, user.getSquadGoals());
    }

    @Test
    void testEmailGetterSetter() {
        User user = new User();
        user.setEmail("alice@example.com");
        assertEquals("alice@example.com", user.getEmail());
    }

    @Test
    void testTimezoneGetterSetter() {
        User user = new User();
        user.setTimezone("America/New_York");
        assertEquals("America/New_York", user.getTimezone());
    }

    @Test
    void testCreatedAtGetterSetter() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        assertEquals(now, user.getCreatedAt());
    }
}