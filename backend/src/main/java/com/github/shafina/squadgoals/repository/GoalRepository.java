package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByIsPublicTrue();
}
