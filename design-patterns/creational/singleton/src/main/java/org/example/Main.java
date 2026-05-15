package org.example;

import org.example.doublelocking.DBConnectionDLocking;
import org.example.eager.DBConnectionEager;
import org.example.lazy.DBConnectionLazy;
import org.example.sync.DBConnectionSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws InterruptedException {

//        DBConnectionEager dbConnectionEager = DBConnectionEager.getInstance();

//        DBConnectionLazy instance = DBConnectionLazy.getInstance();

//        DBConnectionSync dbConnectionSync = DBConnectionSync.getInstance();

//        DBConnectionDLocking dbConnectionDLocking = DBConnectionDLocking.getInstance();

//        System.out.println("Available processors " + Runtime.getRuntime().availableProcessors());



        Runnable runnable = () -> {
            DBConnectionDLocking.getInstance();
        };

        int total = 10000;

        Thread[] list = new Thread[total];

        for(int i=0;i<total;i++) {
            list[i] = new Thread(runnable);
        }

        for (int i=0;i<total;i++) {
            list[i].start();
        }

        for(int i=0;i<total;i++) {
            list[i].join();
        }

        List<Integer> list1 = new ArrayList<>();
        list1.si


    }
}