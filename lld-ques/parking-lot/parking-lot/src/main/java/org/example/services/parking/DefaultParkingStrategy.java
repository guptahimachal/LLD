package org.example.services.parking;

import org.example.exception.NoParkingSpotFoundException;
import org.example.models.entrance.Entrance;
import org.example.models.parkingspot.ParkingSpot;
import org.example.models.parkingspot.ParkingSpotType;
import org.example.models.vehicle.Vehicle;
import org.example.models.vehicle.VehicleType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DefaultParkingStrategy implements ParkingStrategy {

    private List<ParkingSpot> parkingSpots;

    @Override
    public void initialize(ParkingLot parkingLot) {
        this.parkingSpots = parkingLot.getParkingSpots();
    }

    @Override
    public void addSpot(ParkingSpot parkingSpot) {
        this.parkingSpots.add(parkingSpot);
    }

    @Override
    public void addEntrance(Entrance entrance) {

    }

    @Override
    public void removeSpot(ParkingSpot parkingSpot) {
        this.parkingSpots.remove(parkingSpot);
    }

    @Override
    public ParkingSpot findParkingSpot(Vehicle vehicle, Entrance entrance) {
        Optional<ParkingSpot> probableParkingSpot = parkingSpots.stream()
                .filter(parkingSpot -> Objects.isNull(parkingSpot.getVehicle()))
                .filter(parkingSpot -> {
            return VehicleType.TWO_WHEELER.equals(vehicle.getVehicleType()) && ParkingSpotType.TWO_WHEELER.equals(parkingSpot.getParkingSpotType()) ||
                    VehicleType.FOUR_WHEELER.equals(vehicle.getVehicleType()) && ParkingSpotType.FOUR_WHEELER.equals(parkingSpot.getParkingSpotType()) ||
                    VehicleType.HEAVY_LOAD.equals(vehicle.getVehicleType()) && ParkingSpotType.HEAVY_LOAD.equals(parkingSpot.getParkingSpotType());
        }).findFirst();

        return probableParkingSpot.orElseThrow(() -> new NoParkingSpotFoundException(String.format("No parking spot found for vehicle : %s", vehicle.toString())));

    }

    @Override
    public void EmptyParkingSpot(ParkingSpot parkingSpot) {

    }
}
