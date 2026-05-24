package models;


public class ParkingSpot {

    private Boolean isEmpty;
    private String id;
    private Boolean isChargable;

    public enum ParkingSpotType {
        SMALL,
        MID,
        LARGE
    }

}
