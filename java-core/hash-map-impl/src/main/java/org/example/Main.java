package org.example;

import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class Main {
    public static void main(String[] args) {

        MyHashMap<String, String> myMap = new MyHashMap<>();

//        System.out.println(myMap);
        myMap.put("1 ","a");
        myMap.put("2","b");

        System.out.println("Normal print");
        System.err.println("Error print");

        WeakReference<MyHashMap> myHashMapWeakReference = new WeakReference<>(new MyHashMap<Integer, Integer>());
        SoftReference<MyHashMap> myHashMapSoftReference = new SoftReference<>(new MyHashMap<Integer, Integer>());



//        System.out.println(myMap);





    }
}