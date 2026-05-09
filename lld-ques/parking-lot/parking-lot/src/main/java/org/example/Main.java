package org.example;

import org.example.models.entrance.Entrance;
import org.example.models.parkingspot.FourWheelerParkingSpot;
import org.example.models.parkingspot.HeavyLoadParkingSpot;
import org.example.models.parkingspot.TwoWheelerParkingSpot;
import org.example.models.receipt.Receipt;
import org.example.models.ticket.Ticket;
import org.example.models.vehicle.Vehicle;
import org.example.models.vehicle.VehicleType;
import org.example.services.manager.ParkingLotService;
import org.example.services.parking.DefaultParkingStrategy;
import org.example.services.parking.ParkingLot;
import org.example.services.parking.ParkingStrategy;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        ParkingLot parkingLot = new ParkingLot();

        Entrance entrance = new Entrance();

        ParkingStrategy parkingStrategy = new DefaultParkingStrategy();

        ParkingLotService parkingLotService = new ParkingLotService(parkingLot, parkingStrategy);
        parkingLotService.addParkingSpot(new HeavyLoadParkingSpot());
        parkingLotService.addParkingSpot(new HeavyLoadParkingSpot());
        parkingLotService.addParkingSpot(new HeavyLoadParkingSpot());
        parkingLotService.addParkingSpot(new TwoWheelerParkingSpot());
        parkingLotService.addParkingSpot(new TwoWheelerParkingSpot());
        parkingLotService.addParkingSpot(new FourWheelerParkingSpot());
        parkingLotService.addParkingSpot(new FourWheelerParkingSpot());
        parkingLotService.addParkingSpot(new FourWheelerParkingSpot());
        parkingLotService.addParkingSpot(new FourWheelerParkingSpot());

        parkingLotService.addEntrance(entrance);


        Ticket ticket1 = parkingLotService.parkVehicle(new Vehicle("123", VehicleType.FOUR_WHEELER), entrance);
        Ticket ticket2 = parkingLotService.parkVehicle(new Vehicle("456", VehicleType.FOUR_WHEELER), entrance);
        Ticket ticket3 = parkingLotService.parkVehicle(new Vehicle("789", VehicleType.FOUR_WHEELER), entrance);
        Ticket ticket4 = parkingLotService.parkVehicle(new Vehicle("abc", VehicleType.FOUR_WHEELER), entrance);

        Receipt receipt = parkingLotService.unParkVehicle(ticket3);

        Ticket ticket5 = parkingLotService.parkVehicle(new Vehicle("abc", VehicleType.FOUR_WHEELER), entrance);




    }
}