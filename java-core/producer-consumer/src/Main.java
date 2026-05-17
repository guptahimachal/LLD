//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

//        SynchronizedLearning synchronizedLearning = new SynchronizedLearning();
//
//        Thread t1 = new Thread(()->{
//            synchronizedLearning.method1();
//        });
//
//        Thread t2 = new Thread(()->{
//            synchronizedLearning.method2();
//        });
//
//        Thread t3 = new Thread(()->{
//            SynchronizedLearning.method3();
//        });
//
//        Thread t4 = new Thread(()->{
//            SynchronizedLearning.method4();
//        });
//
//        Thread t5 = new Thread(()-> {
//            synchronizedLearning.method5();
//        });
//
//        Thread t6 = new Thread(()-> {
//            synchronizedLearning.method6();
//        });
//
//        t6.start();
//        t5.start();


        SharedResource sharedResource = new SharedResource();

        Thread t1 = new Thread(()-> {
            sharedResource.consume();
        }, "consmerThread1");

        Thread t2 = new Thread(()->{
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sharedResource.produce();
        }, "producerThread1");

        Thread t3 = new Thread(()-> {
            sharedResource.consume();
        }, "consmerThread2");

        t1.start();
        t2.start();
        t3.start();


    }
}