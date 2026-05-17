package locks.readwrite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedResource {

    private List<String> items = new ArrayList<>();

    public void add(String item, ReadWriteLock readWriteLock) {
        log("Acquiring Write lock for : " + item);
        readWriteLock.writeLock().lock();
        log("Acquired Write lock for : " + item);
        try {
            Thread.sleep(1000);
            items.add(item);
        } catch (Exception ex) {

        } finally {
            readWriteLock.writeLock().unlock();
            log("Released Write lock for : " + item);
        }
    }

    public String getLatest(ReadWriteLock readWriteLock) {
        log("Acquiring READ lock");
        readWriteLock.readLock().lock();
        try {
            Thread.sleep(2000);
            String item = items.isEmpty() ? "" : items.getLast();
            log("Item : " + item);
            return item;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            readWriteLock.readLock().unlock();
            log("Released READ lock");
        }
    }


    private void log(String message) {
        System.out.println(String.format("[%s] [%s] : %s", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) , Thread.currentThread().getName(), message));
    }

}
