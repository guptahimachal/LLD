package org.example.models.parkingspot;

import org.example.models.vehicle.VehicleType;

public class TwoWheelerParkingSpot extends ParkingSpot {

    @Override
    public ParkingSpotType getParkingSpotType() {
        return ParkingSpotType.TWO_WHEELER;
    }

}
