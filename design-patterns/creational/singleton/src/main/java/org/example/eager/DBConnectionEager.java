package org.example.eager;

public class DBConnectionEager {

    private static final DBConnectionEager DB_CONNECTION_EAGER = new DBConnectionEager();

    private DBConnectionEager() {
    }

    public static DBConnectionEager getInstance() {
        return DB_CONNECTION_EAGER;
    }

}
