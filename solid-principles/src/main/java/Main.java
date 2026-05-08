package src.main.java;

import src.main.java.single_responsibility.withoutSRP.User;

public class Main {


    public static void main(String args[]) {

        System.out.println("Runned");
        User user = new User("john_doe", "john@example.com");
        if (user.validate()) {
            user.save();
        } else {
            System.out.println("User validation failed.");
        }
        user.save();



    }


}
