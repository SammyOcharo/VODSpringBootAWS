package com.samdev.videoOnDemand.CustomExceptions;

public class IncompleteMobileNumberException extends RuntimeException{
    public IncompleteMobileNumberException(String message) {
        super(message);
    }
}
