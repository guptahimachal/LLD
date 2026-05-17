package locks.stampedlock;

import java.util.concurrent.locks.StampedLock;

public class Main {

    public static void main(String args[]) {

        StampedLock stampedLock = new StampedLock();
        SharedResource sharedResource = new SharedResource();

        Thread t1 = new Thread(()-> {
            // Write
            sharedResource.setValues(1,2, stampedLock);
        }, "write-1");

        Thread t2 = new Thread(()-> {
            // Write
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            sharedResource.setValues(3,5, stampedLock);
        }, "write-2");

        Thread t3 = new Thread(()->{
            // Read

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            sharedResource.getSum(stampedLock);
        }, "read-1");

        t1.start();
        t2.start();
        t3.start();
        // Only start t1 an t3 for optimistic success



    }

}
