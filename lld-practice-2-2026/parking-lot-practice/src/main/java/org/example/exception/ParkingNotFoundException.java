package org.example.exception;

import lombok.Builder;

@Builder
public class ParkingNotFoundException extends RuntimeException {

    private String message;

    @Override
    public String toString() {
        return super.toString();
    }
}
