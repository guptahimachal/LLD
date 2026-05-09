package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class MyHashMapTest {

    private MyHashMap<String, Integer> map;

    @BeforeEach
    public void setUp() {
        this.map = new MyHashMap<>(16);
    }

    @Test
    public void testPutAndGet() {
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        assertEquals(1, map.get("one"));
        assertEquals(2, map.get("two"));
        assertEquals(3, map.get("three"));
    }

    @Test
    public void testUpdateValue() {
        map.put("one", 1);
        map.put("one", 10);

        assertEquals(10, map.get("one"));
    }

    @Test
    public void testGetNonExistentKey() {
        assertNull(map.get("nonExistent"));
    }

    @Test
    public void testRemove() {
        map.put("one", 1);
        map.put("two", 2);

        assertEquals(1, map.remove("one"));
        assertNull(map.get("one"));
        assertEquals(2, map.get("two"));
    }

    @Test
    public void testRemoveNonExistentKey() {
        assertNull(map.remove("nonExistent"));
    }

    @Test
    public void testHashCollision() {
        // Create keys that have the same hash code
        map.put("AaAa", 1);
        map.put("BBBB", 2);

        assertEquals(1, map.get("AaAa"));
        assertEquals(2, map.get("BBBB"));

        // Update values to check for correct handling
        map.put("AaAa", 10);
        map.put("BBBB", 20);

        assertEquals(10, map.get("AaAa"));
        assertEquals(20, map.get("BBBB"));
    }

    @Test
    public void testLoadFactorAndResize() {
        // Initially, the capacity is 16, and the load factor is 0.75
        // Insert enough elements to trigger a resize
        for (int i = 0; i < 13; i++) {
            map.put("key" + i, i);
        }

        for (int i = 0; i < 13; i++) {
            assertEquals(i, map.get("key" + i));
        }
    }

//    @Test
    public void testNullKey() {
        map.put(null, 1);
        assertEquals(1, map.get(null));

        map.put(null, 10);
        assertEquals(10, map.get(null));

        assertEquals(10, map.remove(null));
        assertNull(map.get(null));
    }



}