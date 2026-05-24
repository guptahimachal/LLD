package org.example.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class ParkingLot {

    private List<Floor> floors;
    private String id;


}
