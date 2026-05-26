package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Floor {

    // FIX: Initialized the list to prevent NullPointerException when adding spots
    List<ParkingSpot> parkingSpots = new ArrayList<>();

}
