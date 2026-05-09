package org.example.models.parkingspot;

import org.example.models.vehicle.VehicleType;

public class FourWheelerParkingSpot extends ParkingSpot {

    @Override
    public ParkingSpotType getParkingSpotType() {
        return ParkingSpotType.FOUR_WHEELER;
    }

}
