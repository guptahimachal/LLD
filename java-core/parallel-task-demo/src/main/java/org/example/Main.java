package org.example;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        SharedResource sr = new SharedResource();

        ExecutorService executorService = Executors.newFixedThreadPool(16);

        executorService.submit(() -> sr.process());

        for(int i=0 ; i<7 ; i++) {
            int finalI = i;
            executorService.submit(() -> sr.publish(finalI));
        }

//        while (true) {
//            System.out.println("Enter Input : ");
//            String line = scanner.nextLine();
//
//            if (line.charAt(0) == 'i') {
//                executorService.submit(() -> sr.publish(Integer.valueOf(line.substring(1))));
//            } else {
//                executorService.submit(() -> sr.consume());
//            }
//
//        }
    }
}