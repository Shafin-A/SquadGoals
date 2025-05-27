package com.github.shafina.squadgoals.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateUserRequestTest {

    @Test
    void testNameGetterSetter() {
        CreateUserRequest req = new CreateUserRequest();
        req.setName("Alice");
        assertEquals("Alice", req.getName());
    }

    @Test
    void testEmailGetterSetter() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("alice@example.com");
        assertEquals("alice@example.com", req.getEmail());
    }

    @Test
    void testTimezoneGetterSetter() {
        CreateUserRequest req = new CreateUserRequest();
        req.setTimezone("America/New_York");
        assertEquals("America/New_York", req.getTimezone());
    }

    @Test
    void testCreatedAtGetterSetter() {
        CreateUserRequest req = new CreateUserRequest();
        LocalDateTime now = LocalDateTime.now();
        req.setCreatedAt(now);
        assertEquals(now, req.getCreatedAt());
    }
}