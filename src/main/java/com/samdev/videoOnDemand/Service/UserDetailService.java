package com.samdev.videoOnDemand.Service;

import com.samdev.videoOnDemand.Entity.User;
import com.samdev.videoOnDemand.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserDetailService.class);

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.info("This is the username, {}", username);

        var user = userRepository.findByUsername(username);
        logger.info("This is the user {}", user);
        return user;

    }
}
