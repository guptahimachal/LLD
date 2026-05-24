package org.example.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Booking {

    private String id;
    private Vehicle vehicle;
    private ParkingSpot parkingSpot;
    private LocalDateTime bookedAt;
    private LocalDateTime exitAt;

}
