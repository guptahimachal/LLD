package org.example;

import org.example.model.*;
import org.example.service.ParkingLotManager;
import org.example.service.allocation.AllocationStrategy;
import org.example.service.allocation.DefaultAllocationStrategy;
import org.example.service.billing.BillingStrategy;
import org.example.service.billing.DefaultBilling;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {

        // FIX: Added driver code to setup and test the Parking Lot system

        // 1. Setup ParkingLot
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setId("PL-1");

        // 2. Setup Strategies
        AllocationStrategy allocationStrategy = new DefaultAllocationStrategy(parkingLot);
        
        Money firstHourRate = Money.builder().value(BigDecimal.valueOf(50)).currency(Money.Currency.IND).build();
        Money ratePerHour = Money.builder().value(BigDecimal.valueOf(20)).currency(Money.Currency.IND).build();
        BillingStrategy billingStrategy = new DefaultBilling(firstHourRate, ratePerHour);

        // 3. Initialize Manager with strategies (Dependency Injection)
        ParkingLotManager manager = new ParkingLotManager(parkingLot, allocationStrategy, billingStrategy);

        // 4. Create Floors and Spots
        Floor floor1 = new Floor();
        ParkingSpot spot1 = new ParkingSpot(true, "S1", true, ParkingSpot.ParkingSpotType.MID);
        manager.addFloor(floor1);
        manager.addParkingSpot(floor1, spot1);

        // 5. Test Parking
        Vehicle car = new Vehicle("DL-1234", Vehicle.VehicleType.CAR, false);
        Entrance entrance = new Entrance(); // Simple dummy entrance
        
        System.out.println("Parking vehicle...");
        Booking booking = manager.parkVehicleOptimistic(car, entrance);
        System.out.println("Vehicle parked! Booking ID: " + booking.getId() + ", Spot ID: " + booking.getParkingSpot().getId());

        // 6. Test Unparking & Billing
        System.out.println("Unparking vehicle...");
        Invoice invoice = manager.unParkOptimistic(booking);
        System.out.println("Vehicle unparked! Invoice ID: " + invoice.getInvoiceId() + ", Total Cost: " + invoice.getCost().getValue() + " " + invoice.getCost().getCurrency());

    }
}