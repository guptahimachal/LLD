import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Queue;

public class SharedQueue {

    private Queue<String> queue;
    private int maxSize;

    public SharedQueue(int size) {
        this.maxSize = size;
        queue = new ArrayDeque<>();
    }

    public synchronized void produce(String event) {
        log("Inside Produce, event : " + event);
        while (queue.size() == maxSize) {
            try {
                log("Queue Full Waiting");
                wait();
                log("Awake to produce");
            } catch (InterruptedException e) {

            }
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        queue.add(event);
        notifyAll();
        log("Produced : " + event);
    }

    public synchronized String consume() {
        log("Inside consume");
        while(queue.isEmpty()) {
            try {
                log("Queue Empty Waiting");
                wait();
                log("Awake to consume");
            } catch (InterruptedException e) {

            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String consumedEvent = queue.poll();
        notifyAll();
        log("Consumed : " + consumedEvent);
        return consumedEvent;
    }

    private void log(String message) {
        System.out.println(String.format("[%s] [%s] : %s", LocalDateTime.now(), Thread.currentThread().getName(), message));
    }

}
