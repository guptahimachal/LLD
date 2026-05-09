package org.example.services.manager;

import org.example.models.entrance.Entrance;
import org.example.models.parkingspot.ParkingSpot;
import org.example.models.receipt.Receipt;
import org.example.models.ticket.Ticket;
import org.example.models.vehicle.Vehicle;
import org.example.models.vehicle.VehicleType;
import org.example.services.parking.ParkingLot;
import org.example.services.parking.ParkingStrategy;

import java.time.Duration;

public class ParkingLotService {

    private ParkingLot parkingLot;

    private ParkingStrategy parkingStrategy;

    public ParkingLotService(ParkingLot parkingLot, ParkingStrategy parkingStrategy) {
        this.parkingLot = parkingLot;
        this.parkingStrategy = parkingStrategy;
        parkingStrategy.initialize(parkingLot);
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        parkingStrategy.addSpot(parkingSpot);
    }

    public void removeParkingSpot(ParkingSpot parkingSpot) {
        parkingStrategy.removeSpot(parkingSpot);
    }

    public void addEntrance(Entrance entrance) {
        parkingStrategy.addEntrance(entrance);
    }

    public Ticket parkVehicle(Vehicle vehicle, Entrance entrance) {
        ParkingSpot parkingSpot = parkingStrategy.findParkingSpot(vehicle, entrance);
        parkingSpot.parkVehicle(vehicle);
        System.out.printf("Parked vehicle {}%n", vehicle.toString());
        return new Ticket(vehicle, parkingSpot);
    }

    public Receipt unParkVehicle(Ticket ticket) {
        ticket.getParkingSpot().removeVehicle();
        Duration durationOfParking = Duration.ofMillis(System.currentTimeMillis() - ticket.getIssuedAtTime());
        return new Receipt(durationOfParking, durationOfParking.toHoursPart() * 10);


    }






}
