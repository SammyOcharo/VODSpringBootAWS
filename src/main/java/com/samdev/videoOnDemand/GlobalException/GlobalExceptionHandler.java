package com.samdev.videoOnDemand.GlobalException;
import com.samdev.videoOnDemand.CustomExceptions.UserNotAuthenticatedException;
import com.samdev.videoOnDemand.CustomExceptions.UserNotFoundException;
import com.samdev.videoOnDemand.RequestDTO.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice("com.samdev.ScriptRift")
public class GlobalExceptionHandler {

    UserDTO userDTO = new UserDTO();

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<UserDTO> handleExpiredJwtException(ExpiredJwtException ex) {
        userDTO.setResponseMessage("JWT token has expired: "+ ex.getMessage());
        userDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(userDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<UserDTO> UserNotAuthenticatedException(UserNotAuthenticatedException userNotAuthenticatedException) {
        userDTO.setResponseMessage(userNotAuthenticatedException.getMessage());
        userDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(userDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<UserDTO> MalformedJwtException(MalformedJwtException malformedJwtException) {
        userDTO.setResponseMessage("Invalid authentication token");
        userDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(userDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<UserDTO> NoSuchElementException(NoSuchElementException noSuchElementException) {
        userDTO.setResponseMessage(noSuchElementException.getMessage());
        userDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(userDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserDTO> UserNotFoundException(UserNotFoundException userNotFoundException) {
        userDTO.setResponseMessage(userNotFoundException.getMessage());
        userDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(userDTO, HttpStatus.NOT_FOUND);
    }
}
