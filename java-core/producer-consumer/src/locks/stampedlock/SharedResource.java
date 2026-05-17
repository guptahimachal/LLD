package locks.stampedlock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.StampedLock;

public class SharedResource {

    private int x, y;

    public void setValues(int x, int y, StampedLock stampedLock) {
        long stamp = stampedLock.writeLock();
        try {
            this.x = x;
            this.y = y;
            log(String.format("Set values | x : %s , y : %s", x, y));
        } catch (Exception ex) {

        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    public int getSum(StampedLock stampedLock) {
        long initialStamp = stampedLock.tryOptimisticRead();
        int currentX = x;
        int currentY = y;

        log(String.format("Tried optimistic %s | currentX : %s , currentY : %s", initialStamp, currentX, currentY));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!stampedLock.validate(initialStamp)) {
            try {
                initialStamp = stampedLock.readLock();
                currentY = y;
                currentX = x;
                log(String.format("Optimistic Failed %s | currentX : %s , currentY : %s", initialStamp, currentX, currentY));
            } finally {
                stampedLock.unlockRead(initialStamp);
            }
        } else {
            log("Optimistic SUCCESS");
        }

        log(String.format("Sum : %s", currentX + currentY));
        return currentX + currentY;
    }

    private void log(String message) {
        System.out.println(String.format("[%s] [%s] : %s", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) , Thread.currentThread().getName(), message));
    }

}