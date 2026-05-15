package org.example.doublelocking;

import org.example.sync.DBConnectionSync;

public class DBConnectionDLocking {

    private static DBConnectionDLocking dbConnectionDLocking;

    public DBConnectionDLocking () {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DBConnection acquired by " + Thread.currentThread().getName());
    }

    public static DBConnectionDLocking getInstance() {
        if (dbConnectionDLocking == null) {
            synchronized (DBConnectionDLocking.class) {
                if (dbConnectionDLocking == null) {
                    dbConnectionDLocking = new DBConnectionDLocking();
                }
            }
        }
        return dbConnectionDLocking;
    }

}
