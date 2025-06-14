package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.enums.InvitationStatus;
import com.github.shafina.squadgoals.model.Invitation;

public record InvitationDTO(
        Long id,
        GoalDTO goal,
        String inviterName,
        InvitationStatus status
) {
    public static InvitationDTO from(Invitation invitation) {
        return new InvitationDTO(
                invitation.getId(),
                GoalDTO.from(invitation.getGoal()),
                invitation.getInviter().getName(),
                invitation.getStatus()
        );
    }
}

