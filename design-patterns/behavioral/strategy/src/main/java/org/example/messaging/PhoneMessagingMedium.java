package org.example.messaging;

import java.util.List;

public class PhoneMessagingMedium implements MessagingMedium {

    private List<String> phoneNumbers;

    public PhoneMessagingMedium(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public void send(String message) {
        for(String phoneNumber : phoneNumbers) {
            System.out.printf("Sending message %s to %s%n", message, phoneNumber);
        }
    }
}
