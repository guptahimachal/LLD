package org.example.service.billing;

import org.example.exception.InvalidArguementException;
import org.example.model.Booking;
import org.example.model.Invoice;
import org.example.model.Money;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class DefaultBilling implements BillingStrategy {

    private Money firstHourRate;
    private Money ratePerHour;

    @Override
    public Invoice bill(Booking booking) {
        LocalDateTime bookedAt = booking.getBookedAt();
        LocalDateTime currentTime = LocalDateTime.now();

        Duration parkingDuration = Duration.between(bookedAt, currentTime);

        Money cost = firstHourRate.clone();

        if (parkingDuration.compareTo(Duration.ofHours(1)) > 0) {
            cost.setValue(cost.getValue()
                    .add(ratePerHour.getValue()
                            .multiply(
                                    BigDecimal.valueOf(parkingDuration.toHours() - 1)
                            )
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
