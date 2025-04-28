package com.samdev.videoOnDemand.Controller;
import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import com.samdev.videoOnDemand.Service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SuppressWarnings("unused")
@RestController
@RequestMapping("/apps/api/v1/")
public class UserProfileController {

    private final UserProfileService userProfileService;

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }


    @GetMapping("home/")
    public ResponseEntity<String> Homepage(){

        return new ResponseEntity<>("This is the homepage", HttpStatus.OK);
    }

    @GetMapping("user-profile/")
    public ResponseEntity<UserDTO> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {

        return userProfileService.getUserDetails(userDetails);
    }
}
