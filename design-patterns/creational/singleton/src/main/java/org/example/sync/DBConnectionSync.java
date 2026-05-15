package org.example.sync;

public class DBConnectionSync {

    private static DBConnectionSync dbConnectionSync;

    private DBConnectionSync() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DBConnection acquired by " + Thread.currentThread().getName());
    }


    // Very CPU intensive this CPU locking is
    synchronized public static DBConnectionSync getInstance() {
        if (dbConnectionSync == null) {
            dbConnectionSync = new DBConnectionSync();
        }
        return dbConnectionSync;
    }

}
