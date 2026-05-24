package org.example.service.allocation;

import org.example.model.Entrance;
import org.example.model.ParkingSpot;
import org.example.model.Vehicle;

public interface AllocationStrategy {

    ParkingSpot suggestParkingSpot(Vehicle vehicle, Entrance entrance);

}