package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByFirebaseUid(String firebaseUid);

    Optional<User> findByFirebaseUid(String firebaseUid);

    @Query("""
            SELECT u FROM User u
            WHERE
                (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))
                AND u.id <> :excludedUserId
            """)
    List<User> searchUsersExcludingCurrent(
            @Param("query") String query,
            @Param("excludedUserId") Long excludedUserId,
            Pageable pageable);
}
