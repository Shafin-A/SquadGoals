package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.enums.InvitationStatus;
import com.github.shafina.squadgoals.model.Invitation;
import com.github.shafina.squadgoals.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Page<Invitation> findAllByInvitedUserAndStatus(User user, InvitationStatus status, Pageable pageable);
}
