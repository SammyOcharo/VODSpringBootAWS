package com.samdev.videoOnDemand.Repository;

import com.samdev.videoOnDemand.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
