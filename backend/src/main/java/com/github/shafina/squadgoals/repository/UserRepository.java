package com.github.shafina.squadgoals.repository;

import com.github.shafina.squadgoals.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByFirebaseUid(String firebaseUid);

    Optional<User> findByFirebaseUid(String firebaseUid);

    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String namePart, String emailPart);
}
