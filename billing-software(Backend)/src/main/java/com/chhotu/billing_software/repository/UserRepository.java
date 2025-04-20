package com.chhotu.billing_software.repository;

import com.chhotu.billing_software.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity>  findByUserId(String userId);
}
