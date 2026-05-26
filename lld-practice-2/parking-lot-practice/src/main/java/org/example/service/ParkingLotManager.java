package org.example.service;

import org.example.exception.InvalidArguementException;
import org.example.model.*;
import org.example.service.allocation.AllocationStrategy;
import org.example.service.billing.BillingStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

public class ParkingLotManager {

    private final ParkingLot parkingLot;
    private final AllocationStrategy allocationStrategy;
    private final BillingStrategy billingStrategy;

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

    // ---------------------------------------------------------
    // APPROACH 1: OPTIMISTIC LOCKING (Using StampedLock)
    // ---------------------------------------------------------
    public Booking parkVehicleOptimistic(Vehicle vehicle, Entrance entrance) {
        ParkingSpot parkingSpot = allocationStrategy.suggestParkingSpot(vehicle, entrance);
        
        // Relies on StampedLock's optimisticRead -> convertToWriteLock internally
        if (parkingSpot.bookOptimistic()) {
            return Booking.builder()
                    .id(UUID.randomUUID().toString())
                    .bookedAt(LocalDateTime.now())
                    .vehicle(vehicle)
                    .parkingSpot(parkingSpot)
                    .build();
        } else {
            throw new RuntimeException("Spot collision detected using Optimistic Lock. Please retry.");
        }
    }

    // ---------------------------------------------------------
    // APPROACH 2: PESSIMISTIC LOCKING (Using ReentrantLock)
    // ---------------------------------------------------------
    public Booking parkVehiclePessimistic(Vehicle vehicle, Entrance entrance) {
        ParkingSpot parkingSpot = allocationStrategy.suggestParkingSpot(vehicle, entrance);
            
        // Relies on a traditional ReentrantLock to block/acquire before checking state
        if (parkingSpot.bookPessimistic()) {
            return Booking.builder()
                    .id(UUID.randomUUID().toString())
                    .bookedAt(LocalDateTime.now())
                    .vehicle(vehicle)
                    .parkingSpot(parkingSpot)
                    .build();
        } else {
            throw new RuntimeException("Unexpected error during pessimistic locking: spot already taken.");
        }
    }

    public Invoice unParkOptimistic(Booking booking) {
        if (booking.getParkingSpot().getIsEmpty()) {
            throw InvalidArguementException.builder()
                    .message(String.format("Invalid Booking : %s", booking.getId()))
                    .build();
        }
        Invoice bill = billingStrategy.bill(booking);
        booking.getParkingSpot().freeOptimistic();
        return bill;
    }
    
    public Invoice unParkPessimistic(Booking booking) {
        if (booking.getParkingSpot().getIsEmpty()) {
            throw InvalidArguementException.builder()
                    .message(String.format("Invalid Booking : %s", booking.getId()))
                    .build();
        }
        Invoice bill = billingStrategy.bill(booking);
        booking.getParkingSpot().freePessimistic();
        return bill;
    }

}
