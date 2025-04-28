package com.samdev.videoOnDemand.Repository;

import com.samdev.videoOnDemand.Entity.UserRegistrationVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRegistrationVerificationRepository extends JpaRepository<UserRegistrationVerification, Long> {
    @Query("SELECT u FROM UserRegistrationVerification u WHERE u.username = :username ORDER BY u.id DESC")
    Optional<UserRegistrationVerification> findByUsername(String username);
}
