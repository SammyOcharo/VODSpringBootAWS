package com.samdev.videoOnDemand.Controller;

import com.samdev.videoOnDemand.Entity.User;
import com.samdev.videoOnDemand.Entity.UserForgotPasswordOtp;
import com.samdev.videoOnDemand.Entity.UserRegistrationVerification;
import com.samdev.videoOnDemand.Repository.UserForgotPasswordOtpRepository;
import com.samdev.videoOnDemand.Repository.UserRegistrationVerificationRepository;
import com.samdev.videoOnDemand.Repository.UserRepository;
import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import com.samdev.videoOnDemand.Service.Impl.EmailService;
import com.samdev.videoOnDemand.Service.Impl.UserServiceImpl;
import com.samdev.videoOnDemand.Service.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserControllersTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRegistrationVerificationRepository userRegistrationVerificationRepository;
    @Mock
    private UserForgotPasswordOtpRepository userForgotPasswordOtpRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private JWTService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDTO userDTO;
    private User user;
    private UserForgotPasswordOtp forgotPasswordOtp;

    @BeforeEach
    void setUp(){
        userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setOtp(1234);

        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@gmail.com");
        user.setPassword("password123");
        user.setIsActive(1);

        forgotPasswordOtp = new UserForgotPasswordOtp();
        forgotPasswordOtp.setUsername("testUser");
        forgotPasswordOtp.setOtp(1234);
        forgotPasswordOtp.setUsed(Boolean.FALSE);
        Date expiryDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        forgotPasswordOtp.setExpired(expiryDate);

    }


    @Test
    @DisplayName("Should return 201 CREATED when USER is created successfully.")
    void registerUser_Success() {
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        ResponseEntity<UserDTO> response = userService.registerUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when USER already exists.")
    void testRegisterUser_UserAlreadyExists(){
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        ResponseEntity<UserDTO> response = userService.registerUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 500 INTERNAL SERVER ERROR when an exception occurs")
    void testRegisterUser_ExceptionHandling(){
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        doThrow(new RuntimeException("Database error")).when(userRepository).save(any(User.class));

        ResponseEntity<UserDTO> response = userService.registerUser(userDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", Objects.requireNonNull(response.getBody()).getResponseMessage());

    }

    @Test
    @DisplayName("Should return 400 Bad Request when otp is null")
    void testActivateUser_OtpNull() {
        userDTO.setOtp(null);
        ResponseEntity<UserDTO> response = userService.activateParentUser(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("otp field is null", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when user is already active")
    void testActivateUser_AlreadyActive(){
        User user1 = new User();
        user1.setUsername(userDTO.getUsername());
        user1.setIsActive(1);
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);

        ResponseEntity<UserDTO> response = userService.activateParentUser(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already active", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 404 NotFound when user does not exist")
    void testActiveUser_UserNotFound(){
        user.setIsActive(0);
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        ResponseEntity<UserDTO> response = userService.activateParentUser(userDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User does not exists", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 404 Not Found when OTP does not match")
    void testActivateUser_OtpMismatch(){
        user.setUsername(user.getUsername());
        user.setIsActive(0);
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        UserRegistrationVerification verification = new UserRegistrationVerification();
        verification.setOtp(123456);

        when(userRegistrationVerificationRepository.findByUsername(userDTO.getUsername()))
                .thenReturn(Optional.of(verification));
        userDTO.setOtp(654321);

        ResponseEntity<UserDTO> response = userService.activateParentUser(userDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Otp does not match", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 404 Not Found when OTP has expired")
    void testActivateUser_OtpExpired(){
        user.setIsActive(0);
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        UserRegistrationVerification verification = new UserRegistrationVerification();
        verification.setOtp(123456);
        verification.setExpired(new Date(System.currentTimeMillis()-10000));
        when(userRegistrationVerificationRepository.findByUsername(userDTO.getUsername()))
                .thenReturn(Optional.of(verification));

        userDTO.setOtp(123456);

        ResponseEntity<UserDTO> response = userService.activateParentUser(userDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Otp has expired request another", Objects.requireNonNull(response.getBody()).getResponseMessage());

    }

    @Test
    @DisplayName("Should return 200 when account activation is successful")
    void testActivateUser_Success(){
        user.setIsActive(0);
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(true);

        UserRegistrationVerification verification = new UserRegistrationVerification();
        verification.setOtp(123456);
        verification.setExpired(new Date(System.currentTimeMillis()+10000));
        when(userRegistrationVerificationRepository.findByUsername(userDTO.getUsername()))
                .thenReturn(Optional.of(verification));

        userDTO.setOtp(123456);

        ResponseEntity<UserDTO> response = userService.activateParentUser(userDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("account activated successfully", Objects.requireNonNull(response.getBody()).getResponseMessage());

    }


    @Test
    @DisplayName("Should return 200 OK when login is successful")
    void loginParentUser_LoginSuccess() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.GenerateToken(user)).thenReturn("user-token");

        ResponseEntity<UserDTO> response = userService.loginParentUser(userDTO);

        assertNotNull(response);
        assertEquals("Login successful", Objects.requireNonNull(response.getBody()).getResponseMessage());
        assertNotNull(response.getBody().getToken());

    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when user not found")
    void testLoginUser_UserNotFound(){
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);
        ResponseEntity<UserDTO> response = userService.loginParentUser(userDTO);

        log.info("This is the response: {}", response);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("user does not exist!", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when account not active")
    void testLoginUser_AccountNotActivate(){
        user.setIsActive(0);
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(user);
        ResponseEntity<UserDTO> response = userService.loginParentUser(userDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Account is not active", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 500 INTERNAL SERVER ERROR when an exception occurs")
    void testLoginUser_InternalServerError(){
        when(userRepository.findByUsername(userDTO.getUsername())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<UserDTO> response = userService.loginParentUser(userDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getResponseMessage().contains("Internal Server Error occurred!"));
    }


    @Test
    @DisplayName("Should return 200 OK forgot password is successfully sent")
    void forgotPasswordParentUser_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(userForgotPasswordOtpRepository.save(any(UserForgotPasswordOtp.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        ResponseEntity<UserDTO> response = userService.forgotPasswordParentUser(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Otp sent successfully", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when user does not exist")
    void testForgotPasswordParentUser_UserNotFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        ResponseEntity<UserDTO> response = userService.forgotPasswordParentUser(userDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User Not Found", Objects.requireNonNull(response.getBody()).getResponseMessage());
    }

    @Test
    void forgotPasswordVerifyOtpParentUser() {
    }

    @Test
    void setNewPasswordParentUser() {
    }
}