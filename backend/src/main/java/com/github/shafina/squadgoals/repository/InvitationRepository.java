package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.enums.Status;
import com.github.shafina.squadgoals.model.Invitation;
import com.github.shafina.squadgoals.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByInvitedUserAndStatus(User user, Status status);
}
