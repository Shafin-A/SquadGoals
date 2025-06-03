package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByIsPublicTrue();

    @Query("SELECT g FROM Goal g WHERE g.nextDueAt <= :now")
    List<Goal> findGoalsDueToday(@Param("now") LocalDateTime now);

}
