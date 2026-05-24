package org.example.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpot {

    private Boolean isEmpty;
    private String id;
    private Boolean isChargable;
    private ParkingSpotType parkingSpotType;

    public enum ParkingSpotType {
        SMALL,
        MID,
        LARGE
    }

    public boolean isVehicleCompatible(Vehicle vehicle) {
        if (vehicle.getIsElectric() && !isChargable) {
            return false;
        }

        switch (vehicle.getVehicleType()) {
            case CAR -> {
                return ParkingSpotType.MID.equals(parkingSpotType);
            }
            case TWO_WHEELER -> {
                return ParkingSpotType.SMALL.equals(parkingSpotType);
            }
            case TRUCK -> {
                return ParkingSpotType.LARGE.equals(parkingSpotType);
            }
        }
        return false;
    }

}
