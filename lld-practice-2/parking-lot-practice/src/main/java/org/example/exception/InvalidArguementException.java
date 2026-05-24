package org.example.exception;

import lombok.Builder;

@Builder
public class InvalidArguementException extends RuntimeException {

    private String message;

    @Override
    public String toString() {
        return message + super.toString();
    }
}

