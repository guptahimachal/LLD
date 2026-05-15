package org.example.lazy;

public class DBConnectionLazy {

    private static DBConnectionLazy dbConnectionLazy;

    private DBConnectionLazy() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DBConnection acquired by " + Thread.currentThread().getName());
    }

    public static DBConnectionLazy getInstance() {
        if(dbConnectionLazy == null) {
            dbConnectionLazy = new DBConnectionLazy();
        }
        return dbConnectionLazy;
    }

}
