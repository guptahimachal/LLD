package locks.reentrant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class SharedResource {

    private int cnt = 0;

    public void incrementAndGet(ReentrantLock reentrantLock) {
        try {
//            log("Trying to acquire lock");
            reentrantLock.lock();
            log("Acquired Lock");
            incrementAndGet();
        } catch (Exception ex) {

        } finally {
            log("Lock Released");
            reentrantLock.unlock();
        }
    }

    public int incrementAndGet() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        cnt++;
        return cnt;
    }

    public int get() {
        return cnt;
    }

    private void log(String message) {
        System.out.println(String.format("[%s] [%s] : %s", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) , Thread.currentThread().getName(), message));
    }

}
