package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter();
        Runnable runnable = () -> {
            for (int i = 0; i < 100; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);

        t1.start();
        t2.start();

        t2.join();
        t1.join();

        System.out.println(counter.getCnt());




    }
}