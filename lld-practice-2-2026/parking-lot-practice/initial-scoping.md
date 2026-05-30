## USE CASES
1. Create Parking lot
2. Add Floors to parking lot
3. Add Parking spot to Floors
4. Create Vehicle
5. Create Booking
6. Update/Close Booking
7. Create invoice
8. Create Parking spot allocation Strategy
9. Create Billing Strategy

## Core entities
1. ParkingLot
2. Floors
3. ParkingSpot (Abstract class)
    - Compact
    - Large
    - Handicapped
    - EV

4. Vehicle (Interface)
    - Car
    - Bike
    - Truck

5. Booking
6. Invoice
7. ParkingSpotAllocationStrategy
    - Defalt
    - Nearest
8. BillingStrategy
    - Hourly
    - Flat
    - Surcharge

## Responsibilitises and what the entiteis would hold
1. ParkingLot
    - Floors
    - ParkingSpotAllocationStrategy
    - BillingStratgy

2. Floors
    - ParkingSpots

3. ParkingSpot (Abstract class)
    - id
    - Floor ref
    - Vehicle

4. Vehicle (Interface)
    - number

5. Booking
    - Vehicle
    - ParkingSpot
    - Long startTimeEpoch

6. Invoice
    - Booking
    - endTime
    - Long cost

7. ParkingSpotAllocationStrategy (Interface)
    - ParkingSpot allocateParkingSpot(Vehicle);


8. BillingStrategy
    - Invoice bill(Booking)

## Relationship
The relationship is also defined above , along with
1. ParkingSpotAllocationStrategy
    - has ParkingLot


## APIs
1.
POST /v1/parkinglot - Create ParkingLot
- Needs
  i. name
  ii. Num of floors
  iii. Default Parking Spot Allocation Strategy
  iv. Default BillingStrategy

GET  /v1/parkinglot - Get all ParkingLot
GET  /v1/parkinglot/{p1} - Get details of p1 parkingLot

POST /v1/parkinglot/{p1}/floor - Create a floor
- Needs
  i. name
  ii. List of ParkingSpots with their details like type

GET  /v1/parkinglot/{p1}/floor/{f1} - Get floor details

POST /v1/parkinglot/{p1}/floor/{f1}/spot       - Create a spot
GET  /v1/parkinglot/{p1}/floor/{f1}/spot/{sp1} - Get details of sp1 spot


POST /v1/parkinglot/{p1}/book - Park the Vehicle
GET  /v1/parkinglot/{p1}/book/{bookingID} - Get booking details
DELETE /v1/parkinglot/{p1}/book/{bookingID} - Unbook and return invoice


## flow
### User hits book API with say ParkingStrategy Default
1. ParkingManager -> Get ParkingLot
2. ParkingLot.park(Vehicel, Strategy)
3. returns Booking or throw exception if no parkingSpot found

### Unbook  BillingStrategy Default
1. ParkingManager -> GetParkingLot
2. ParkingLot.unbook(Booking, BillingStrategy)
3. Generate Invoice and mark spot as empty and return




- How to decide what to keep and interface and what as an abstract class ? like i was getting confuse in ParkingSpot and Vehicle
- Also I have a confusion why keep different types of vehicle , Why does parking spot care care about vehicle, Whicle creating a booking a user should just pass a parking spot type
  If we want that data can be stored as metadata
- I have Defined ParkingSpotAllocationStrategy interface Now i am facing issue that diff startegy would require diff inputs so how to deal with that Eg. Nearest strategy might require Floor as input
- 


