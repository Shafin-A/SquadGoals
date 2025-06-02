package com.github.shafina.squadgoals.model;

import com.github.shafina.squadgoals.enums.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvitationTest {
    @Test
    void testIdGetterSetter() {
        Invitation invitation = new Invitation();
        invitation.setId(1L);
        assertEquals(1L, invitation.getId());
    }

    @Test
    void testGoalGetterSetter() {
        Invitation invitation = new Invitation();
        Goal g = new Goal();
        invitation.setGoal(g);
        assertEquals(g, invitation.getGoal());
    }

    @Test
    void testInvitedUserGetterSetter() {
        Invitation invitation = new Invitation();
        User invitedUser = new User();
        invitation.setInvitedUser(invitedUser);
        assertEquals(invitedUser, invitation.getInvitedUser());
    }

    @Test
    void testInviterGetterSetter() {
        Invitation invitation = new Invitation();
        User inviter = new User();
        invitation.setInviter(inviter);
        assertEquals(inviter, invitation.getInviter());
    }

    @Test
    void testStatusGetterSetter() {
        Invitation invitation = new Invitation();
        Status statusPending = Status.PENDING;
        invitation.setStatus(statusPending);
        assertEquals(statusPending, invitation.getStatus());
    }

    @Test
    void testCreatedAtGetterSetter() {
        Invitation invitation = new Invitation();
        LocalDateTime createdAt = LocalDateTime.now();
        invitation.setCreatedAt(createdAt);
        assertEquals(createdAt, invitation.getCreatedAt());
    }
}
