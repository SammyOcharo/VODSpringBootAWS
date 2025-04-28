package com.samdev.videoOnDemand.Service.Impl;

import com.samdev.videoOnDemand.CustomExceptions.UserNotAuthenticatedException;
import com.samdev.videoOnDemand.CustomExceptions.UserNotFoundException;
import com.samdev.videoOnDemand.Entity.User;
import com.samdev.videoOnDemand.Repository.UserProfileRepository;
import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import com.samdev.videoOnDemand.Service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    UserDTO userDTO = new UserDTO();

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public ResponseEntity<UserDTO> getUserDetails(UserDetails userDetails) {
        User user = userProfileRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(()-> new UserNotFoundException("User not found!"));

        if(userDetails == null)
            throw new UserNotAuthenticatedException("Request is unauthenticated!");
        userDTO.setResponseMessage("User profile");
        userDTO.setStatusCode(200);
        userDTO.setUsername(userDetails.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
}
