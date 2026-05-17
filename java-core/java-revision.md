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

<br>

## 3. Daemon Threads

A **Daemon Thread** is a low-priority background thread that provides asynchronous services to user threads (e.g., the Garbage Collector). 

**Key Characteristics:**
* **Lifespan tied to User Threads:** The JVM does **not** wait for daemon threads to finish. As soon as all "user" (non-daemon) threads terminate, the JVM immediately shuts down and forcefully kills any running daemon threads.
* **Background execution:** They run asynchronously in the background, typically performing continuous maintenance tasks like monitoring, logging, or memory management.
* **Creation:** You must make a thread daemon *before* starting it by calling `thread.setDaemon(true)`. If you call it after `start()`, it throws an `IllegalThreadStateException`.
* **Inheritance:** A thread created by a daemon thread is automatically a daemon thread. A thread created by a user thread is automatically a user thread (like the `main` thread).
* **Don't use for I/O:** Because the JVM kills them abruptly without executing `finally` blocks or releasing resources, you should **never** use daemon threads for critical tasks like file I/O or database operations.

<br>

## 4. Types of Locks in Java (Senior SDE Context)

Beyond basic `synchronized` blocks, Java gives us powerful explicit locks in `java.util.concurrent.locks`.

### 1. ReentrantLock
* **What it is:** An advanced lock that works just like `synchronized`, but gives us explicit control over locking and unlocking, plus advanced features like fairness.
* **Senior details:** 
  * **Fairness:** You can pass `true` in the constructor. *How it works:* It keeps a queue. The thread waiting the longest gets the lock next. (Avoids thread starvation).
  * **tryLock():** *How it works:* A thread tries to get the lock. If it's busy, the thread doesn't get stuck waiting; it just skips the work or does something else.
  * **lockInterruptibly():** *How it works:* If a thread is stuck waiting for a lock, another thread can interrupt and kill it cleanly. `synchronized` cannot do this!
* **LLD Use Case:** Design a **Thread-Safe Bounded Blocking Queue** or a **Task Scheduler** (where you might need `lockInterruptibly` to cancel waiting tasks).
* **Usage (Protecting a Counter):**
  ```java
  ReentrantLock lock = new ReentrantLock(true);
  int count = 0; // Shared state
  
  public void increment() {
      lock.lock(); 
      try {
          count++; // Needs lock because count++ is not atomic
      } finally {
          lock.unlock(); 
      }
  }
  ```
* **Usage (Interruptible Wait):**
  ```java
  public void cancelableTask() {
      try {
          lock.lockInterruptibly(); // Thread can be killed while waiting in line!
          try {
              // perform long task
          } finally {
              lock.unlock(); 
          }
      } catch (InterruptedException e) {
          System.out.println("Task cancelled before getting the lock!");
          Thread.currentThread().interrupt(); 
      }
  }
  ```

### 2. ReadWriteLock (ReentrantReadWriteLock)
* **What it is:** A lock split into two parts: a shared "Read" lock and an exclusive "Write" lock. It massively improves performance when you read often but write rarely.
* **Senior details:** 
  * **How it works:** Reading data doesn't change it, so *multiple* threads are allowed to hold the Read lock at the same exact time. 
  * But, if a thread wants to write, it must ask for the Write lock. The Write lock waits until ALL readers are done, locks everyone out, does the write, and then lets people read again.
  * **Why it matters:** If your app reads data 90% of the time and writes 10% of the time, this is *much* faster than a normal lock because readers don't block other readers.
* **LLD Use Case:** Design an **In-Memory Cache (LRU/LFU)** or a **Configuration Manager** (where config values are read constantly but updated rarely).
* **Usage (Shared List):**
  ```java
  ReadWriteLock rwLock = new ReentrantReadWriteLock();
  List<String> list = new ArrayList<>(); // Not thread-safe!
  
  public String readFirstItem() {
      rwLock.readLock().lock(); // Multiple threads can read safely
      try { return list.get(0); } finally { rwLock.readLock().unlock(); }
  }
  
  public void addItem(String item) {
      rwLock.writeLock().lock(); // Blocks everyone else from reading or writing
      try { list.add(item); } finally { rwLock.writeLock().unlock(); }
  }
  ```

### 3. StampedLock
* **What it is:** A faster, Java 8 alternative to `ReadWriteLock`. It introduces an "optimistic reading" mode that lets us read data without actually locking, avoiding lock overhead entirely when writes are rare.
* **Senior details:** 
  * **How it works ("Optimistic Read"):** Instead of actually locking the door to read, it takes a "stamp" (a long number) and reads the data freely. After reading, it calls `validate(stamp)`.
  * **What `validate()` returns:** It returns `true` if no other thread acquired the write lock since you got the stamp. It returns `false` if someone *did* write to the data while you were reading.
  * If it returns `true`, great! Your read was safe without the slowness of a real lock. If `false`, your read data might be corrupted (e.g. half-written), so you throw it away, acquire a *real* blocking read lock, and try again.
  * **Why it matters:** If writes are very rare, this avoids locking overhead almost entirely. **Note: It is NOT reentrant!**
* **LLD Use Case:** Design a **High-Frequency Trading Engine** (order book matching) or an **In-Memory Database** where microsecond read latency is critical.
* **Usage (Reading X and Y together):**
  ```java
  StampedLock stampedLock = new StampedLock();
  int x = 0, y = 0; // Shared state
  
  public int getSum() {
      long stamp = stampedLock.tryOptimisticRead(); 
      
      int currentX = x; 
      int currentY = y; // Without lock, a writer could change 'y' right after we read 'x'
      
      if (!stampedLock.validate(stamp)) { // Returns false if a writer jumped in!
          stamp = stampedLock.readLock(); // Fall back to strict block
          try { 
              currentX = x; 
              currentY = y; 
          } finally { stampedLock.unlockRead(stamp); }
      }
      return currentX + currentY;
  }
  ```

### 4. Semaphore
* **What it is:** A signaling mechanism with a fixed number of permits. Instead of locking out everyone but one, it allows exactly **N** threads to access a resource at the same time.
* **Senior details:** 
  * **How it works:** A normal lock only allows **1** thread inside. A Semaphore is initialized with **N** "permits". It allows exactly **N** threads inside concurrently. If thread N+1 arrives, it waits until someone leaves.
  * **Why it matters:** This is the core mechanism used to build **Rate Limiters** (e.g., only 5 API calls per second) and **Connection Pools** (e.g., we only have 10 database connections to share).
* **LLD Use Case:** Design a **Rate Limiter** (e.g., Token Bucket algorithm), a **Database Connection Pool**, or a **Parking Lot** system (where spots = permits).
* **Usage (Limiting downloads):**
  ```java
  Semaphore semaphore = new Semaphore(3); // Only 3 concurrent downloads
  
  public void downloadFile() {
      try {
          semaphore.acquire(); // 4th thread waits here until a permit is freed
          // perform file download
      } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
      } finally {
          semaphore.release(); 
      }
  }
  ```
