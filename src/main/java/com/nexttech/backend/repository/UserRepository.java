package com.nexttech.backend.repository;

import com.nexttech.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    User findByEmail(String email);

    Optional<User> findById(Long id);
}