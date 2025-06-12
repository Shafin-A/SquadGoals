package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.model.Invitation;

public record InvitationDTO(
        Long id,
        GoalDTO goal,
        String inviterName
) {
    public static InvitationDTO from(Invitation invitation) {
        return new InvitationDTO(
                invitation.getId(),
                GoalDTO.from(invitation.getGoal()),
                invitation.getInviter().getName()
        );
    }
}

