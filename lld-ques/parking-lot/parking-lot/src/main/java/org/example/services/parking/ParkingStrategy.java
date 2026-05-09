package org.example.services.parking;

import org.example.models.entrance.Entrance;
import org.example.models.parkingspot.ParkingSpot;
import org.example.models.vehicle.Vehicle;

public interface ParkingStrategy {

    void initialize(ParkingLot parkingLot);

    void addSpot(ParkingSpot parkingSpot);

    void addEntrance(Entrance entrance);

    void removeSpot(ParkingSpot parkingSpot);

    ParkingSpot findParkingSpot(Vehicle vehicle, Entrance entrance);

    void EmptyParkingSpot(ParkingSpot parkingSpot);

}
