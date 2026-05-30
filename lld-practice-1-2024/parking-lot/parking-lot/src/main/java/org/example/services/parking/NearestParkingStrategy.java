package org.example.services.parking;

import org.example.exception.NoParkingSpotFoundException;
import org.example.models.entrance.Entrance;
import org.example.models.parkingspot.ParkingSpot;
import org.example.models.parkingspot.ParkingSpotType;
import org.example.models.vehicle.Vehicle;
import org.example.models.vehicle.VehicleType;

import java.util.*;

public class NearestParkingStrategy implements ParkingStrategy {

    private Map<Entrance, Deque<ParkingSpot>> parkingSpotByEntrance = new HashMap<>();

    @Override
    public void initialize(ParkingLot parkingLot) {
        // Initialize the parkingSpotByEntrance map with parking spots grouped by entrance
//        for (Entrance entrance : parkingLot.getEntranceList()) {
//            parkingSpotByEntrance.put(entrance, new LinkedList<>(parkingLot.getParkingSpots().stream()
//                    .sorted((spot1, spot2) -> Double.compare(spot1.getDistanceFromEntrance(entrance), spot2.getDistanceFromEntrance(entrance)))
//                    .collect(Collectors.toList())));
//        }
    }

    @Override
    public void addSpot(ParkingSpot parkingSpot) {
        // Add the parking spot to each entrance's deque
//        for (Map.Entry<Entrance, Deque<ParkingSpot>> entry : parkingSpotByEntrance.entrySet()) {
//            entry.getValue().add(parkingSpot);
//            entry.setValue(entry.getValue().stream()
//                    .sorted((spot1, spot2) -> Double.compare(spot1.getDistanceFromEntrance(entry.getKey()), spot2.getDistanceFromEntrance(entry.getKey())))
//                    .collect(Collectors.toCollection(LinkedList::new)));
//        }
    }

    @Override
    public void addEntrance(Entrance entrance) {
        parkingSpotByEntrance.put(entrance, new LinkedList<>());
    }

    @Override
    public void removeSpot(ParkingSpot parkingSpot) {
        // Remove the parking spot from each entrance's deque
//        for (Map.Entry<Entrance, Deque<ParkingSpot>> entry : parkingSpotByEntrance.entrySet()) {
//            entry.getValue().remove(parkingSpot);
//        }
    }

    @Override
    public ParkingSpot findParkingSpot(Vehicle vehicle, Entrance entrance) {
        Deque<ParkingSpot> parkingSpots = parkingSpotByEntrance.get(entrance);

        Optional<ParkingSpot> probableParkingSpot = parkingSpots.stream().filter(parkingSpot ->
                VehicleType.TWO_WHEELER.equals(vehicle.getVehicleType()) && ParkingSpotType.TWO_WHEELER.equals(parkingSpot.getParkingSpotType()) ||
                VehicleType.FOUR_WHEELER.equals(vehicle.getVehicleType()) && ParkingSpotType.FOUR_WHEELER.equals(parkingSpot.getParkingSpotType()) ||
                VehicleType.HEAVY_LOAD.equals(vehicle.getVehicleType()) && ParkingSpotType.HEAVY_LOAD.equals(parkingSpot.getParkingSpotType())).findFirst();

        return probableParkingSpot.orElseThrow(() -> new NoParkingSpotFoundException(String.format("No parking spot found for vehicle: %s", vehicle.toString())));
    }

    @Override
    public void EmptyParkingSpot(ParkingSpot parkingSpot) {

    }
}
