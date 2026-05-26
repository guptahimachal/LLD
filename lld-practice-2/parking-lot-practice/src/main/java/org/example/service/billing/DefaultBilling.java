package org.example.service.billing;

import org.example.model.Booking;
import org.example.model.Invoice;
import org.example.model.Money;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class DefaultBilling implements BillingStrategy {

    private final Money firstHourRate;
    private final Money ratePerHour;

    // FIX: Added constructor to initialize the rates to prevent NullPointerException
    public DefaultBilling(Money firstHourRate, Money ratePerHour) {
        this.firstHourRate = firstHourRate;
        this.ratePerHour = ratePerHour;
    }

    @Override
    public Invoice bill(Booking booking) {
        LocalDateTime bookedAt = booking.getBookedAt();
        LocalDateTime currentTime = LocalDateTime.now();

        Duration parkingDuration = Duration.between(bookedAt, currentTime);

        Money cost = firstHourRate.clone();
        
        long totalMinutes = parkingDuration.toMinutes();

        // FIX: Replaced `toHours() - 1` with a logic that correctly rounds up fractional hours 
        // using total minutes so 1 hr 5 mins charges for 2 hours instead of 1.
        if (totalMinutes > 60) {
            long extraMinutes = totalMinutes - 60;
            long extraHoursToBill = (long) Math.ceil(extraMinutes / 60.0);
            
            cost.setValue(cost.getValue()
                    .add(ratePerHour.getValue()
                            .multiply(BigDecimal.valueOf(extraHoursToBill))
                    )
            );
        }

        return Invoice.builder()
                .invoiceId(UUID.randomUUID().toString())
                .bookingId(booking.getId())
                .vehicleNumber(booking.getVehicle().getNumber())
                .cost(cost)
                .build();

    }
}
