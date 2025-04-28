package com.samdev.videoOnDemand.Service;

import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserProfileService {

    ResponseEntity<UserDTO> getUserDetails(UserDetails userDetails);
}
