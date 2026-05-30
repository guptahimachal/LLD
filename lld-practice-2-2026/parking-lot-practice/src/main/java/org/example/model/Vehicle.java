package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    private String number;
    private VehicleType vehicleType;
    private Boolean isElectric;

    public enum VehicleType {
        TWO_WHEELER,
        CAR,
        TRUCK;
    }

}
