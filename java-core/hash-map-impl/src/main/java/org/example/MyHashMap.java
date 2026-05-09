package org.example;

import java.util.Arrays;

public class MyHashMap<K,V> {

    private final int MAX_CAPACITY = 1<<30;
    private final int INITIAL_CAPACITY = 1<<4;
    private final float loadFactor = 0.75f;

    private Entry<K,V>[] table;
    int n;

    public MyHashMap(int capacity) {
        int tableSize = tableSizeFor(capacity);
        table = new Entry[tableSize];
    }

    public MyHashMap() {
        table = new Entry[INITIAL_CAPACITY];
    }

    public void put(K key, V val) {
        // To make hashes more uniform
        int hashCode = getHash(key);
        int index = hashCode & (table.length - 1);

        if (table[index] == null) {
            table[index] = new Entry<K,V>(key, val, hashCode);
        } else {
            Entry<K,V> curEntry = table[index];
            Entry<K,V> prevEntry = null;
            while(curEntry != null) {

                if (curEntry.key.equals(key)) {
                    curEntry.value = val;
                    return;
                }
                prevEntry = curEntry;
                curEntry = curEntry.next;
            }
            prevEntry.next = new Entry<K,V>(key, val, hashCode);
        }
        n++;

        if (n > loadFactor * table.length) {
            resize();
        }
    }

    private void resize() {
        Entry<K, V>[] oldTable = table;
        int newCapacity = oldTable.length << 1;
        Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[newCapacity];

        table = newTable;
        n = 0;

        for (Entry<K, V> oldEntry : oldTable) {
            while (oldEntry != null) {
                put(oldEntry.key, oldEntry.value);
                oldEntry = oldEntry.next;
            }
        }
    }

    private <K> int getHash(K key) {
        if (key == null) {
            return 0;
        }
        return key.hashCode() ^ (key.hashCode() >>> 16);
    }

    public V get(K key) {
        int hashCode = getHash(key);
        int index = hashCode & (table.length - 1);

        Entry<K,V> curEntry = table[index];
        while (curEntry != null) {
            if (curEntry.key.equals(key)) {
                return curEntry.value;
            }
            curEntry = curEntry.next;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Map has %s values :\n", n));
        for(Entry<K,V> entry : table) {
            if (entry != null) {
                sb.append(entry.toString());
            }
        }
        return sb.toString();
    }

    public V remove(K key) {
        int hashCode = getHash(key);
        int index = hashCode & (table.length - 1);

        Entry<K,V> curEntry = table[index];
        Entry<K,V> prevEntry = null;
        while (curEntry != null) {
            // curEntry needs to be removed
            if (curEntry.key.equals(key)) {
                V val = curEntry.value;
                if (prevEntry == null) {
                    table[index] = curEntry.next;
                } else {
                    prevEntry.next = curEntry.next;
                }
                n--;
                return val;
            }
            prevEntry = curEntry;
            curEntry = curEntry.next;
        }
        return null;
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return n==0;
    }


    /**
     * Calculates the number greater than or equal to capacity which is pow(2,n)
     * @param capacity capacity
     */
    private int tableSizeFor(int capacity) {
        capacity = capacity - 1;

//        int tableSize = 1;
//        while (capacity > 0) {
//            capacity = capacity >> 1;
//            tableSize = tableSize << 1;
//        }
//        return tableSize;

        int n = -1 >>> Integer.numberOfLeadingZeros(capacity);
        // n<0 in case of neg capacity, give 1 as capacity
        // n>= MAX_CAPACITY in case of capacity-1 is 01<some-combination-of-30-bits>
        return n < 0 ? 1 : n >= MAX_CAPACITY ? MAX_CAPACITY : n+1;
    }




    public class Entry<K,V> {

        private final K key;
        private V value;
        private int hash;
        private Entry<K,V> next;

        public Entry(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = null;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
             return String.format("\"%s\" : \"%s\"\n", key.toString(), value.toString()) + ((next == null) ? "" : next.toString());
        }
    }


}
