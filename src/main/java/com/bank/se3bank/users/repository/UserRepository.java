package com.bank.se3bank.users.repository;

import com.bank.se3bank.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByNationalId(String nationalId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNationalId(String nationalId);
}