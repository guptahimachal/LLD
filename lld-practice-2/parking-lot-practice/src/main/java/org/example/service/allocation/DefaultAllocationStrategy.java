package org.example.service.allocation;

import org.example.exception.ParkingNotFoundException;
import org.example.model.*;

import java.util.Optional;

public class DefaultAllocationStrategy implements AllocationStrategy {

    private ParkingLot parkingLot;

    public DefaultAllocationStrategy(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    @Override
    public ParkingSpot suggestParkingSpot(Vehicle vehicle, Entrance entrance) {

        Optional<ParkingSpot> parkingSpotOpt = parkingLot.getFloors().stream()
                .flatMap(floor -> floor.getParkingSpots().stream())
                .filter(parkingSpot -> parkingSpot.getIsEmpty() && parkingSpot.isVehicleCompatible(vehicle))
                .findFirst();

        return parkingSpotOpt.orElseThrow(() -> ParkingNotFoundException.builder()
                .message(String.format("ParkingSpot not found for %s", vehicle.toString()))
                .build());
    }

}
