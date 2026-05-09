package org.example.models.receipt;

import java.time.Duration;

public class Receipt {

    private Duration parkingTime;

    private long amount;

    public Receipt(Duration parkingTime, long amount) {
        this.parkingTime = parkingTime;
        this.amount = amount;
    }

    public Duration getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(Duration parkingTime) {
        this.parkingTime = parkingTime;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
