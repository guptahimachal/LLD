package org.example;

import org.example.messaging.EmailMessagingMedium;
import org.example.messaging.MessagingMedium;
import org.example.messaging.PhoneMessagingMedium;
import org.example.notification.Notification;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        Notification notification = new Notification("This is the notification message");

        MessagingMedium emailMessagingMedium = new EmailMessagingMedium(Arrays.asList("abc@gmail.com", "def@gmail.com"));
        MessagingMedium phoneMessagingMedium = new PhoneMessagingMedium(Arrays.asList("9789898989", "5659898989"));

        notification.send(phoneMessagingMedium);

        notification.send("EMAIL" , "abc@gmail.com", "def@gmail.com");


    }
}