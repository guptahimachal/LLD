package org.example.models.parkingspot;

import org.example.models.vehicle.VehicleType;

public class HeavyLoadParkingSpot extends ParkingSpot {

    @Override
    public ParkingSpotType getParkingSpotType() {
        return ParkingSpotType.HEAVY_LOAD;
    }

}
