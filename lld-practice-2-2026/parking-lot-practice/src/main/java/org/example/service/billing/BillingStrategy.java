package org.example.service.billing;

import org.example.model.Booking;
import org.example.model.Invoice;

public interface BillingStrategy {

    Invoice bill(Booking booking);

}
