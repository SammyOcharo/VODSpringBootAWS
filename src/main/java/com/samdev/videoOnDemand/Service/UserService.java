package com.samdev.videoOnDemand.Service;

import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseEntity<UserDTO> registerUser(UserDTO userDTO);

    ResponseEntity<UserDTO> activateParentUser(UserDTO userDTO);

    ResponseEntity<UserDTO> loginParentUser(UserDTO userDTO);

//    ResponseEntity<UserDTO> forgotPasswordParentUser(UserDTO userDao);

    ResponseEntity<UserDTO> forgotPasswordVerifyOtpParentUser(UserDTO userDTO);

    ResponseEntity<UserDTO> setNewPasswordParentUser(UserDTO userDTO);

//    ResponseEntity<UserDTO> activateParentUser(UserDTO userDao) throws RecordNotFound;

//    ResponseEntity<UserDTO> loginParentUser(UserDTO userDao);

    ResponseEntity<UserDTO> forgotPasswordParentUser(UserDTO userDTO);

//    ResponseEntity<UserDTO> forgotPasswordVerifyOtpParentUser(UserDTO userDao);

//    ResponseEntity<UserDTO> setNewPasswordParentUser(UserDTO userDao);

//    ResponseEntity<UserDTO> activateParentUser(UserDTO userDao) throws RecordNotFound;
}
