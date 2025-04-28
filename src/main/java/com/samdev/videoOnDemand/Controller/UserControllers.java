package com.samdev.videoOnDemand.Controller;

import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import com.samdev.videoOnDemand.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/apps/api/v1/auth/")
public class UserControllers {

    private final UserService userService;

    public UserControllers(UserService userService) {
        this.userService = userService;
    }

    //create parent user.
    @PostMapping("register-user/")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO){
        return userService.registerUser(userDTO);
    }

    //activate parent user
    @PostMapping("activate-user/")
    public ResponseEntity<UserDTO> activateParentUser(@RequestBody UserDTO userDTO) {
        return userService.activateParentUser(userDTO);
    }
    @PostMapping("login/")
    public ResponseEntity<UserDTO> loginParentUser(@RequestBody UserDTO userDTO){
        return userService.loginParentUser(userDTO);
    }

    @GetMapping("login/")
    public ResponseEntity<UserDTO> loginCheckParentUser(@RequestBody UserDTO userDTO){
        return userService.loginParentUser(userDTO);
    }

    @PostMapping("forgot-password-parent/")
    public ResponseEntity<UserDTO> forgotPasswordParentUser(@RequestBody UserDTO userDTO){
        return userService.forgotPasswordParentUser(userDTO);
    }

    @PostMapping("forgot-password-verifyOtp-parent/")
    public ResponseEntity<UserDTO> forgotPasswordVerifyOtpParentUser(@RequestBody UserDTO userDTO){
        return userService.forgotPasswordVerifyOtpParentUser(userDTO);
    }

    @PostMapping("set-new-password-parent/")
    public ResponseEntity<UserDTO> setNewPasswordParentUser(@RequestBody UserDTO userDTO){
        return userService.setNewPasswordParentUser(userDTO);
    }



}
