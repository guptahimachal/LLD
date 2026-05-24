package org.example.model;

import lombok.Builder;

@Builder
public class Invoice {

    private Money cost;
    private String bookingId;
    private String vehicleNumber;
    private String invoiceId;




}
