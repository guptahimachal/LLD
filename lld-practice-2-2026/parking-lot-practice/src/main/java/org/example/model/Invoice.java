package org.example.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Invoice {

    private Money cost;
    private String bookingId;
    private String vehicleNumber;
    private String invoiceId;




}
