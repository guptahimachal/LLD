package locks.deadlock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    /**
     *
     * Dead lock using synchronized, NO RECOVERY, Only prevention i.e. to acquire all locks in a sorted order
     *
     * @param args
     */
    public static void main(String args[]) {

        Object ob1 = new Object();
        Object ob2 = new Object();

        Thread t1 = new Thread(()-> {
            synchronized (ob1) {
                log("Acquired ob1, Trying ob2");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (ob2) {
                    log("Acquired ob2 and ob1");
                }
            }
        });

        Thread t2 = new Thread(()-> {
            synchronized (ob2) {
                log("Acquired ob2, Trying ob1");
                synchronized (ob1) {
                    log("Acquired ob1 and ob2");
                }
            }
        });

        t1.start(); t2.start();



    }

    private static void log(String message) {
        System.out.println(String.format("[%s] [%s] : %s", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) , Thread.currentThread().getName(), message));
    }


}
