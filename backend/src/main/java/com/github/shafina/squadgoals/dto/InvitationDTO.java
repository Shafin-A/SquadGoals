package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.model.Invitation;

public record InvitationDTO(
        Long id,
        Long goalId,
        String goalTitle,
        String inviterName
) {
    public static InvitationDTO from(Invitation invitation) {
        return new InvitationDTO(
                invitation.getId(),
                invitation.getGoal().getId(),
                invitation.getGoal().getTitle(),
                invitation.getInviter().getName()
        );
    }
}

