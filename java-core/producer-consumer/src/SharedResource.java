import java.time.LocalDateTime;

public class SharedResource {

    private boolean resource;

    public synchronized void produce() {
        log("Inside Produce");
        this.resource = true;
        notifyAll();
//        notify();
        log("Produced and Notified All");
    }

    public synchronized void consume() {
        log("Inside consume");
        while (!resource) {
            try {
                log("Resource not available , Waiting");
                wait();
                log("Awake");
            } catch (InterruptedException e) {

            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.resource = false;
        log("Consumed");

    }

    private void log(String message) {
        System.out.println(String.format("[%s] [%s] : %s", LocalDateTime.now(), Thread.currentThread().getName(), message));
    }

}
