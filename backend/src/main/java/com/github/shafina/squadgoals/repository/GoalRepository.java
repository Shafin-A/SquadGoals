package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
