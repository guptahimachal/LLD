package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParkingLot {

    // FIX: Initialized the list to prevent NullPointerException when adding floors
    private List<Floor> floors = new ArrayList<>();
    private String id;

}
