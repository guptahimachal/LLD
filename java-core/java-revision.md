# Java Revision

## 1. Synchronized Keyword in Java

You can use the `synchronized` keyword in **4 different places** in Java. Here is a simple, short explanation of each:

### 1. On an Instance Method
```java
public synchronized void doSomething() { ... }
```
* **What it locks:** The current object instance (`this`).
* **Meaning:** Only one thread can execute *any* synchronized instance method on this specific object at a time. If Thread A is in `method1()`, Thread B cannot enter `method1()` OR `method2()` on the same object.

### 2. On a Static Method
```java
public static synchronized void doSomething() { ... }
```
* **What it locks:** The Class object itself (e.g., `Test.class`).
* **Meaning:** Because static methods belong to the class (not an instance), the lock is applied globally across all instances of the class. Only one thread can execute this static method, regardless of how many objects of this class exist.

### 3. As a Block inside an Instance Method
```java
public void doSomething() {
    synchronized(this) { ... } // or synchronized(customLockObject)
}
```
* **What it locks:** Whatever object you pass inside the parentheses (usually `this` or a dedicated `lock` object).
* **Meaning:** More precise than locking the whole method. Threads can run the rest of the method simultaneously, but they must wait in line to enter this specific block.

### 4. As a Block inside a Static Method
```java
public static void doSomething() {
    synchronized(Test.class) { ... } 
}
```
* **What it locks:** Usually the Class object (`Test.class`).
* **Meaning:** Similar to a static synchronized method, but allows you to lock only a specific section of the static method globally across all threads.

---

**Summary Rule of Thumb:** 
* `synchronized` method = "Lock the whole thing."
* `synchronized` block = "Lock only the specific lines of code that need it" (better for performance).

<br>

## 2. wait() and notify()

When you call `notify()` from inside a `synchronized` method (or block), it does **not** notify all threads in the JVM, nor does it notify all threads waiting on the class.

Here is exactly what happens:

1. **`notify()` wakes up EXACTLY ONE thread** that is currently waiting on the **monitor of that specific object**.
2. If multiple threads are waiting on that exact same object, the JVM picks one arbitrarily to wake up.
3. **`notifyAll()`** wakes up **ALL** threads waiting on that exact same object's monitor.

### Example:
```java
public class SharedResource {
    public synchronized void waitForData() throws InterruptedException {
        // Thread A calls this and waits. 
        // It releases the lock on THIS object and goes to sleep.
        this.wait(); 
    }

    public synchronized void dataReady() {
        // Thread B calls this.
        // It wakes up ONE thread (e.g., Thread A) that is waiting on THIS object.
        this.notify(); 
    }
}
```

* **If it's an instance method:** It only wakes up threads waiting on `this` specific instance of `SharedResource`. It has zero effect on threads waiting on a different instance of `SharedResource`.
* **If it's a static method:** It only wakes up threads waiting on the `SharedResource.class` object. 

**Important:** A thread that is woken up by `notify()` doesn't immediately start running. It must first re-acquire the lock before it can proceed!

### FAQ: Two Synchronized Methods
**Q:** If a class has two synchronized methods (`method1` and `method2`), and a thread in `method1` is waiting. If another thread calls `notifyAll()` from `method2`, what happens?

**A:** The thread waiting in `method1` **will be notified** (woken up). 

**Here's why:** 
* Both `method1` and `method2` are synchronized on the **same object instance** (`this`). 
* When `notifyAll()` is called inside `method2`, it wakes up *all* threads that are waiting on `this`. 
* Since the thread in `method1` called `wait()` on `this`, it is in the waiting pool for that specific object, so it receives the notification and wakes up.
