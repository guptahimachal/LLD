package locks.deadlockwithlocks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String args[]) {

        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();

        Thread t1 = new Thread(()-> {
            try {
                lock1.lock();
                log("Acquired 1");

                Thread.sleep(100);

                lock2.lock();
                log("Acquired 1 and 2");



            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock1.unlock();
                lock2.unlock();
            }

        });

//        Thread t2 = new Thread(()-> {
//            try {
//
//                Thread.sleep(100);
//
//                lock2.lock();
//                log("Acquired 2");
//
//                lock1.lock();
//                log("Acquired 2 and 1");
//
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                lock1.unlock();
//                lock2.unlock();
//            }
//
//        });

        Thread t2 = new Thread(()-> {
            try {

                Thread.sleep(100);

                lock2.lock();
                log("Acquired 2");


                if (lock1.tryLock(10, TimeUnit.SECONDS)) {
                    try {
                        log("Acquired 2 and 1");
                    } finally {
//                        lock1.unlock();
                    }
                } else {
                    log("Deadlock detected, releasing prev lock");
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (lock1.isLocked())
                    lock1.unlock();
                if (lock2.isLocked())
                    lock2.unlock();
            }

        });

        t1.start();
        t2.start();








    }


    private static void log(String message) {
        System.out.println(String.format("[%s] [%s] : %s", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) , Thread.currentThread().getName(), message));
    }

}
