package org.example.services.parking;

import org.example.models.entrance.Entrance;
import org.example.models.parkingspot.ParkingSpot;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {

    private List<ParkingSpot> parkingSpots;

    private List<Entrance> entranceList;

    public ParkingLot() {
        this.parkingSpots = new ArrayList<>();
        this.entranceList = new ArrayList<>();
    }

    protected List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    protected List<Entrance> getEntranceList() {
        return entranceList;
    }

}
