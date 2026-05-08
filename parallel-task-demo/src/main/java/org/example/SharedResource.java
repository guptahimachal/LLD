package org.example;

import java.util.LinkedList;
import java.util.Queue;

public class SharedResource {

    private Queue<Integer> inputQueue;
    private Queue<Integer> outputQueue;

    public SharedResource() {
        inputQueue = new LinkedList<>();
        outputQueue = new LinkedList<>();
    }

    public synchronized void publish(Integer num) {
        System.out.println(String.format("[%s] : Adding number [%s]", Thread.currentThread().getName(), num));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        inputQueue.add(num);
        System.out.println(String.format("[%s] : Added number [%s]", Thread.currentThread().getName(), num));
        notifyAll();
    }

    public synchronized Integer consume() {
        System.out.println(String.format("[%s] : Starting consuming", Thread.currentThread().getName()));
        while(outputQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Integer ans = outputQueue.poll();
        System.out.println(String.format("[%s] : Popped number [%s]", Thread.currentThread().getName(), ans));
        notifyAll();
        return ans;
    }

    public synchronized void process() {

        while(inputQueue.size() < 6) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(String.format("[%s] : Processing start", Thread.currentThread().getName()));


        while(!inputQueue.isEmpty()) {
            Integer top = inputQueue.poll();
            System.out.println(String.format("[%s] : Processing element %s", Thread.currentThread().getName(), top));

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            outputQueue.add(top*top);
            System.out.println(String.format("[%s] : Processed element %s", Thread.currentThread().getName(), top));
        }

        System.out.println(String.format("[%s] : Processing end", Thread.currentThread().getName()));
    }


}
