package com.hubt.assistant.identity.user.repository;

import com.hubt.assistant.identity.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {

    boolean existsByEmailIgnoreCase(
            String email
    );

    Optional<User>
    findByEmailIgnoreCaseAndDeletedAtIsNull(
            String email
    );

    boolean existsByPhoneAndIdNot(
            String phone,
            UUID id
    );
}