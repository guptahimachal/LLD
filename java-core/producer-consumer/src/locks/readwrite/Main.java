package locks.readwrite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    public static void main(String args[]) {

        SharedResource sharedResource = new SharedResource();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        List<Thread> threads = new ArrayList<>();

        for(int i=0;i<3;i++) {
            int finalI = i;
            threads.add(new Thread(()->{
                sharedResource.add(Integer.valueOf(finalI).toString(), readWriteLock);
            }, "writer-thread-" + i));

            threads.add(new Thread(()->{
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sharedResource.getLatest(readWriteLock);
            }, "reader-thread-" + i));
        }

        startAndWait(threads);
    }

    private static void startAndWait(List<Thread> threads) {
        for(Thread thread : threads) {
            thread.start();
        }

        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
