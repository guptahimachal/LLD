package org.example.messaging;

import java.util.List;

public class EmailMessagingMedium implements MessagingMedium {

    private List<String> emailIds;

    public EmailMessagingMedium(List<String> emailIds) {
        this.emailIds = emailIds;
    }

    @Override
    public void send(String message) {
        for(String emailId : emailIds) {
            System.out.printf("Sending message %s to %s%n", message, emailId);
        }
    }
}
