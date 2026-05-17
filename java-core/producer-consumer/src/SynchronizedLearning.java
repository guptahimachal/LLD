import java.time.LocalDateTime;

public class SynchronizedLearning {

    public synchronized void method1() {
        System.out.println(String.format("%s %s [Start] instance method1", LocalDateTime.now(), Thread.currentThread().getName()));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(String.format("%s %s [End] instance method1", LocalDateTime.now(), Thread.currentThread().getName()));
    }

    public synchronized void method2() {
        System.out.println(String.format("%s %s [Start] instance method2", LocalDateTime.now(), Thread.currentThread().getName()));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(String.format("%s %s [End] instance method2", LocalDateTime.now(), Thread.currentThread().getName()));
    }

    public static synchronized void method3() {
        System.out.println(String.format("%s %s [Start] static method3", LocalDateTime.now(), Thread.currentThread().getName()));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(String.format("%s %s [End] static method3", LocalDateTime.now(), Thread.currentThread().getName()));
    }

    public static synchronized void method4() {
        System.out.println(String.format("%s %s [Start] static method4", LocalDateTime.now(), Thread.currentThread().getName()));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(String.format("%s %s [End] static method4", LocalDateTime.now(), Thread.currentThread().getName()));
    }


    public void method5() {
        System.out.println(String.format("%s %s [Start] instance method5", LocalDateTime.now(), Thread.currentThread().getName()));
        synchronized (this) {
            System.out.println(String.format("%s %s [Start] execution method5", LocalDateTime.now(), Thread.currentThread().getName()));
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(String.format("%s %s [End] execution method5", LocalDateTime.now(), Thread.currentThread().getName()));
        }
        System.out.println(String.format("%s %s [End] instance method5", LocalDateTime.now(), Thread.currentThread().getName()));
    }

    public void method6() {
        System.out.println(String.format("%s %s [Start] instance method6", LocalDateTime.now(), Thread.currentThread().getName()));
        synchronized (this) {
            System.out.println(String.format("%s %s [Start] execution method6", LocalDateTime.now(), Thread.currentThread().getName()));
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(String.format("%s %s [End] execution method6", LocalDateTime.now(), Thread.currentThread().getName()));
        }
        System.out.println(String.format("%s %s [End] instance method6", LocalDateTime.now(), Thread.currentThread().getName()));
    }

}
