package org.example;

public class Counter {

    private int cnt;

    public void increment() {
        System.out.println("Method accessed by " + Thread.currentThread().getName());
        synchronized (this) {
            cnt++;
        }
    }

    public int getCnt() {
        return cnt;
    }


}
