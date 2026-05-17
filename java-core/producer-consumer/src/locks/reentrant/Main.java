package locks.reentrant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String args[]) {

//        normalUsage();


        SharedResource sharedResource = new SharedResource();
        List<Thread> threads = new ArrayList<>();
        ReentrantLock reentrantLock = new ReentrantLock(true);

        for (int i=0;i<3;i++) {
            threads.add(new Thread(()->{

                for(int j=0 ; j<4 ; j++) {
                    sharedResource.incrementAndGet(reentrantLock);
                }

            }, "thread-"+ i));
        }

        startAndWait(threads);
        System.out.println("Value after " + sharedResource.get());
    }

    private static void normalUsage() {
        SharedResource sharedResource = new SharedResource();

        List<Thread> threads = new ArrayList<>();
        ReentrantLock reentrantLock = new ReentrantLock(true);

        for(int i=0;i<3;i++) {
            threads.add(new Thread(()->{
//                sharedResource.incrementAndGet();
                sharedResource.incrementAndGet(reentrantLock);
            }, "thread-" + Integer.valueOf(i).toString()));
        }

        startAndWait(threads);
        System.out.println("Value after " + Integer.valueOf(sharedResource.get()));
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
