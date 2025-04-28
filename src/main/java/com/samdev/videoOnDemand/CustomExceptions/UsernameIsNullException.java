package com.samdev.videoOnDemand.CustomExceptions;

public class UsernameIsNullException extends RuntimeException{

    public UsernameIsNullException(String message) {
        super(message);
    }
}
