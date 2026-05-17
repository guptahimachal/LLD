package org.example;

import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws InterruptedException {

        understandSynchronized();


    }

    private static void understandSynchronized() {
        Test testObj = new Test();

        // 1. Create a list to keep track of our threads
        List<Thread> threads = new ArrayList<>();

        threads.add(new Thread(()-> {
            testObj.method1();
        }));
        threads.add(new Thread(()-> {
            testObj.method1();
        }));
        threads.add(new Thread(()-> {
            testObj.method2();
        }));
        threads.add(new Thread(()-> {
            testObj.method2();
        }));


        for (Thread t : threads) {
            t.start();
        }
    }

    public static class Test {

        public synchronized void method1() {
            System.out.println(String.format("%s %s Inside method1", LocalDateTime.now(), Thread.currentThread().getName()));
            try { Thread.sleep(5000); } catch (InterruptedException e) {} // Simulate work
            System.out.println(String.format("%s %s Completed method1", LocalDateTime.now(), Thread.currentThread().getName()));
        }

        public synchronized void method3() {
            System.out.println(String.format("%s %s Inside method3", LocalDateTime.now(), Thread.currentThread().getName()));
            try { Thread.sleep(5000); } catch (InterruptedException e) {} // Simulate work
            System.out.println(String.format("%s %s Completed method3", LocalDateTime.now(), Thread.currentThread().getName()));
        }

        public void method2() {
            System.out.println(String.format("%s %s Inside method2", LocalDateTime.now(), Thread.currentThread().getName()));
        }

    }

}