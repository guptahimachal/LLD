package org.example.notification;

import org.example.messaging.MessagingMedium;

public class Notification {

    private String content;

    public Notification(String content) {
        this.content = content;
    }

    // Without Strategy Pattern
    public void send(String medium, String ...metaData) {

        switch (medium) {
            case "EMAIL"  : {
                for (String emailId : metaData) {
                    System.out.println("Sending notification " + content + " to eamil " + emailId);
                }
                break;
            }
            case "MESSAGE" : {
                for (String emailId : metaData) {
                    System.out.println("Sending notification " + content + " to phone " + emailId);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported medium" + medium);
            }
        }

    }

    // With Strategy Pattern
    public void send(MessagingMedium messagingMedium) {
        messagingMedium.send(content);
    }



}
