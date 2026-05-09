package org.example.models.ticket;

import org.example.models.parkingspot.ParkingSpot;
import org.example.models.vehicle.Vehicle;

public class Ticket {

    private Vehicle vehicle;

    private long issuedAtTime;

    private ParkingSpot parkingSpot;

    public Ticket(Vehicle vehicle, ParkingSpot parkingSpot) {
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.issuedAtTime = System.currentTimeMillis();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public long getIssuedAtTime() {
        return issuedAtTime;
    }
}
