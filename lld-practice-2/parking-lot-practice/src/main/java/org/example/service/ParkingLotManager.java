package org.example.service;

import org.example.exception.InvalidArguementException;
import org.example.model.*;
import org.example.service.allocation.AllocationStrategy;
import org.example.service.allocation.DefaultAllocationStrategy;
import org.example.service.billing.BillingStrategy;
import org.example.service.billing.DefaultBilling;

import java.time.LocalDateTime;
import java.util.UUID;

public class ParkingLotManager {

    private final ParkingLot parkingLot;
    private final AllocationStrategy allocationStrategy;
    private final BillingStrategy billingStrategy;

    public ParkingLotManager(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        this.allocationStrategy = new DefaultAllocationStrategy(parkingLot);
        this.billingStrategy = new DefaultBilling();
    }

    public ParkingLotManager(ParkingLot parkingLot, AllocationStrategy allocationStrategy, BillingStrategy billingStrategy) {
        this.parkingLot = parkingLot;
        this.allocationStrategy = allocationStrategy;
        this.billingStrategy = billingStrategy;
    }

    public void addFloor(Floor floor) {
        parkingLot.getFloors().add(floor);
    }

    public void addParkingSpot(Floor floor, ParkingSpot parkingSpot) {
        floor.getParkingSpots().add(parkingSpot);
    }

    public Booking parkVehicle(Vehicle vehicle, Entrance entrance) {
        ParkingSpot parkingSpot = allocationStrategy.suggestParkingSpot(vehicle, entrance);
        parkingSpot.setIsEmpty(false);
        return Booking.builder()
                .id(UUID.randomUUID().toString())
                .bookedAt(LocalDateTime.now())
                .vehicle(vehicle)
                .parkingSpot(parkingSpot)
                .build();
    }

    public Invoice unPark(Booking booking) {
        if (booking.getParkingSpot().getIsEmpty()) {
            throw InvalidArguementException.builder()
                    .message(String.format("Invalid Booking : %s", booking.getId()))
                    .build();
        }
        Invoice bill = billingStrategy.bill(booking);
        booking.getParkingSpot().setIsEmpty(true);
        return bill;
    }

}
