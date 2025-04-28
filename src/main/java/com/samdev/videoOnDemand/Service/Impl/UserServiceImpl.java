package com.samdev.videoOnDemand.Service.Impl;

import com.samdev.videoOnDemand.CustomExceptions.OtpMissMatch;
import com.samdev.videoOnDemand.CustomExceptions.RecordNotFound;
import com.samdev.videoOnDemand.CustomExceptions.UserNotFoundException;
import com.samdev.videoOnDemand.Entity.User;
import com.samdev.videoOnDemand.Entity.UserForgotPasswordOtp;
import com.samdev.videoOnDemand.Entity.UserRegistrationVerification;
import com.samdev.videoOnDemand.Repository.UserForgotPasswordOtpRepository;
import com.samdev.videoOnDemand.Repository.UserRegistrationVerificationRepository;
import com.samdev.videoOnDemand.Repository.UserRepository;
import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import com.samdev.videoOnDemand.Service.JWTService;
import com.samdev.videoOnDemand.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRegistrationVerificationRepository userRegistrationVerificationRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailservice;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserForgotPasswordOtpRepository userForgotPasswordOtpRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    public UserServiceImpl(UserRepository userRepository, UserRegistrationVerificationRepository userRegistrationVerificationRepository,
                           PasswordEncoder encoder, EmailService emailservice, AuthenticationManager authenticationManager, JWTService jwtService, UserForgotPasswordOtpRepository userForgotPasswordOtpRepository) {
        this.userRepository = userRepository;
        this.userRegistrationVerificationRepository = userRegistrationVerificationRepository;
        this.encoder = encoder;
        this.emailservice = emailservice;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userForgotPasswordOtpRepository = userForgotPasswordOtpRepository;
    }

    @Override
    public ResponseEntity<UserDTO> registerUser (UserDTO userDTO) {

        UserDTO userDTO1 = new UserDTO();
        User user = new User();
        UserRegistrationVerification userRegistrationVerification = new UserRegistrationVerification();

        if(userRepository.existsByEmail(userDTO.getEmail()) || userRepository.existsByUsername(userDTO.getUsername())){
            userDTO1.setResponseMessage("User already exists");
            userDTO1.setStatusCode(400);
            return new ResponseEntity<>(userDTO1, HttpStatus.BAD_REQUEST);
        }
        try{
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(encoder.encode(userDTO.getPassword()));

            user.setRole("basic_user");

            userRepository.save(user);

            //todo: implement activation of account via otp
            Random random = new Random();
            Integer otp = random.nextInt(9000) + 1000;


            userRegistrationVerification.setUsername(userDTO.getUsername());
            userRegistrationVerification.setOtp(otp);

            userRegistrationVerification.setExpired(new Date(System.currentTimeMillis() + 30*60*1000));

            userRegistrationVerification.setUsed(Boolean.FALSE);

            userRegistrationVerificationRepository.save(userRegistrationVerification);

            String subject = "Activate account OTP";
            String body = "Use this otp " + otp + " to activate your account";

            emailservice.sendEmail(userDTO.getEmail(), subject,  body);


            userDTO1.setStatusCode(201);
            userDTO1.setResponseMessage("User created successfully");

            return new ResponseEntity<>(userDTO1, HttpStatus.CREATED);

        }catch(Exception e){
            userDTO1.setStatusCode(500);
            userDTO1.setResponseMessage("An error occurred");

            return new ResponseEntity<>(userDTO1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserDTO> activateParentUser(UserDTO userDTO) {

        UserDTO userDTO1 = new UserDTO();

        if(userDTO.getOtp() == null){
            userDTO1.setResponseMessage("otp field is null");
            userDTO1.setStatusCode(400);
            return new ResponseEntity<>(userDTO1, HttpStatus.BAD_REQUEST);
        }
        try{
            String username = userDTO.getUsername();
            User user = userRepository.findByUsername(username);
            if(user.getIsActive() == 1){
                userDTO1.setResponseMessage("User already active");
                userDTO1.setStatusCode(400);
                return new ResponseEntity<>(userDTO1, HttpStatus.BAD_REQUEST);
            }
            if(!userRepository.existsByUsername(username)){
                userDTO1.setResponseMessage("User does not exists");
                userDTO1.setStatusCode(404);
                return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
            }
            UserRegistrationVerification userRegistrationVerification = userRegistrationVerificationRepository.findByUsername(username)
                    .orElseThrow(()-> new RecordNotFound("Record not found"));


            if(!Objects.equals(userRegistrationVerification.getOtp(), userDTO.getOtp())){
                userDTO1.setResponseMessage("Otp does not match");
                userDTO1.setStatusCode(400);
                return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
            }
            System.out.println(userRegistrationVerification.getExpired());
            System.out.println(userRegistrationVerification.getExpired().after(new Date()));

            if(userRegistrationVerification.getExpired().before(new Date())){
                userDTO1.setResponseMessage("Otp has expired request another");
                userDTO1.setStatusCode(400);
                return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
            }
            userRegistrationVerification.setUsed(Boolean.TRUE);
            userRegistrationVerificationRepository.save(userRegistrationVerification);

            user.setIsActive(1);
            userRepository.save(user);

            userDTO1.setStatusCode(200);
            userDTO1.setResponseMessage("account activated successfully");
            return new ResponseEntity<>(userDTO1, HttpStatus.OK);

        }catch(RecordNotFound e){
            userDTO1.setStatusCode(404);
            userDTO1.setResponseMessage(String.valueOf(e));

            return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
        }catch (HttpMessageNotReadableException e) {
            userDTO1.setResponseMessage("otp value is not an integer");
            userDTO1.setStatusCode(400);
            return new ResponseEntity<>(userDTO1, HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            userDTO1.setStatusCode(500);
            userDTO1.setResponseMessage("An error occurred!");

            return new ResponseEntity<>(userDTO1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserDTO> loginParentUser(UserDTO userDTO) {

        logger.info("Attempting to log in user: {}", userDTO.getUsername());


        UserDTO userDTO1 = new UserDTO();

        try{


            User user = userRepository.findByUsername(userDTO.getUsername());

            if (user == null) {
                userDTO1.setResponseMessage("user does not exist!");
                userDTO1.setStatusCode(404);
                return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
            }
            if(user.getIsActive() == 0){
                userDTO1.setResponseMessage("Account is not active");
                userDTO1.setStatusCode(400);
                return new ResponseEntity<>(userDTO1, HttpStatus.BAD_REQUEST);
            }


            logger.info("We are here..");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(),userDTO.getPassword()));
            String token = jwtService.GenerateToken(user);

            userDTO1.setToken(token);
            userDTO1.setStatusCode(200);
            userDTO1.setResponseMessage("Login successful");

            return new ResponseEntity<>(userDTO1, HttpStatus.OK);

        }catch(Exception e){
            userDTO1.setResponseMessage("Internal Server Error occurred!" + e.getMessage());
            userDTO1.setStatusCode(500);
            return new ResponseEntity<>(userDTO1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UserDTO> forgotPasswordParentUser(UserDTO userDTO) {
        UserDTO userDTO1 = new UserDTO();
        UserForgotPasswordOtp userForgotPasswordOtp = new UserForgotPasswordOtp();

        try{
            User user = userRepository.findByUsername(userDTO.getUsername());

            if(user == null){

                throw new UserNotFoundException("User with provided email does not exist");
            }
            Random random = new Random();
            Integer otp = random.nextInt(9000) + 1000;


            userForgotPasswordOtp.setUsername(userDTO.getUsername());
            userForgotPasswordOtp.setOtp(otp);

            userForgotPasswordOtp.setExpired(new Date(System.currentTimeMillis() + 30*60*1000));

            userForgotPasswordOtp.setUsed(Boolean.FALSE);

            userForgotPasswordOtpRepository.save(userForgotPasswordOtp);

            String subject = "Forgot Password OTP";
            String body = "Use this otp " + otp + " to recover password your account";

            emailservice.sendEmail(user.getEmail(), subject,  body);

            //todo: send an email with otp to confirm that the email is with the actual user.
            userDTO1.setResponseMessage("Otp sent successfully");
            userDTO1.setStatusCode(200);

            return new ResponseEntity<>(userDTO1, HttpStatus.OK);

        }catch (UserNotFoundException e){
            userDTO1.setResponseMessage("User Not Found");
            userDTO1.setStatusCode(404);
            return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<UserDTO> forgotPasswordVerifyOtpParentUser(UserDTO userDTO) {
        UserDTO userDTO1 = new UserDTO();

        try{
            if(userDTO.getUsername() == null){
                throw new UserNotFoundException("User with provided username does not exist");
            }
            if(userDTO.getOtp() == null){
                throw new UserNotFoundException("otp is null");
            }

            UserForgotPasswordOtp userForgotPasswordOtp1 =
                    userForgotPasswordOtpRepository.findTopByUsernameOrderByIdDesc(userDTO.getUsername())
                            .orElseThrow(()-> new UserNotFoundException("User with provided email does not exist"));

            System.out.println("Checking OTP for username: " + userDTO.getUsername());

            System.out.println(userDTO.getOtp());
            System.out.println(userForgotPasswordOtp1.getOtp());

            if(userForgotPasswordOtp1.getExpired().before(new Date())){
                userDTO1.setResponseMessage("Otp has expired.");
                userDTO1.setStatusCode(400);
                return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
            }
            if(!Objects.equals(userDTO.getOtp(), userForgotPasswordOtp1.getOtp())){
                throw new OtpMissMatch("Otp does not match");
            }

            if(userForgotPasswordOtp1.getUsed() == Boolean.TRUE){
                userDTO1.setResponseMessage("Otp has been used.");
                userDTO1.setStatusCode(400);
                return new ResponseEntity<>(userDTO1, HttpStatus.BAD_REQUEST);
            }
            userForgotPasswordOtp1.setUsed(Boolean.TRUE);
            userForgotPasswordOtpRepository.save(userForgotPasswordOtp1);

            userDTO1.setStatusCode(200);
            userDTO1.setResponseMessage("Otp verified successfully");
            return new ResponseEntity<>(userDTO1, HttpStatus.OK);



        }catch (UserNotFoundException e) {
            userDTO1.setResponseMessage(e.getMessage());
            userDTO1.setStatusCode(404);
            return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
        } catch (OtpMissMatch e) {
            userDTO1.setResponseMessage("Otp does not match");
            userDTO1.setStatusCode(400);
            return new ResponseEntity<>(userDTO1, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<UserDTO> setNewPasswordParentUser(UserDTO userDTO) {
        UserDTO userDTO1 = new UserDTO();

        try{
            if(userDTO.getUsername() == null){
                throw new UserNotFoundException("User with provided username does not exist");
            }
            if(userDTO.getPassword() == null){
                throw new UserNotFoundException("password field is null");
            }

            User parent = userRepository.findByUsername(userDTO.getUsername());
            parent.setPassword(encoder.encode(userDTO.getPassword()));

            userRepository.save(parent);

            userDTO1.setStatusCode(200);
            userDTO1.setResponseMessage("Password changed successfully!");
            return new ResponseEntity<>(userDTO1, HttpStatus.OK);

        }catch (UserNotFoundException e) {
            userDTO1.setResponseMessage(e.getMessage());
            userDTO1.setStatusCode(404);
            return new ResponseEntity<>(userDTO1, HttpStatus.NOT_FOUND);
        }
    }
}
