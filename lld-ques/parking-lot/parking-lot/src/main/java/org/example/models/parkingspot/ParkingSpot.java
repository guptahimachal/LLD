package org.example.models.parkingspot;

import org.example.models.vehicle.Vehicle;

public abstract class ParkingSpot {

    private Integer id;

    protected Vehicle vehicle;

    public abstract ParkingSpotType getParkingSpotType();

    public void parkVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void removeVehicle() {
        this.vehicle = null;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

}
