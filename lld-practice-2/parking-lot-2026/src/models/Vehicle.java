package models;

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
