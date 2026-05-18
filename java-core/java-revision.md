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
  * **lockInterruptibly():** *How it works:* If a thread is stuck waiting for a lock, another thread can interrupt and kill it cleanly. **Critical rule:** Without this (i.e. using `synchronized` or `.lock()`), calling `Thread.interrupt()` is completely ignored while the thread is waiting in line!
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

  // NOTE: There is NO "optimistic write" in StampedLock. 
  // Writes mutate state and must always be exclusive.
  public void setValues(int newX, int newY) {
      long stamp = stampedLock.writeLock(); // Always pessimistic
      try {
          x = newX;
          y = newY;
      } finally {
          stampedLock.unlockWrite(stamp);
      }
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

### 5. Atomic Classes & CAS (DB-Style Optimistic Locking)
What you do in a Database with Optimistic Locking (`UPDATE ... WHERE version = expectedVersion`) is called **Compare-And-Swap (CAS)** in Java. These classes use hardware-level CPU instructions to achieve 100% lock-free concurrency.

#### 1. `AtomicInteger` & `AtomicLong`
*   **Senior Angle:** The most common mistake juniors make is using `synchronized` just to increment a counter or ID generator. `count++` is not thread-safe (it's 3 operations: Read, Add, Write). `AtomicInteger` does this in a single, non-blocking hardware instruction.
*   **When to Use:** Metrics aggregators (counting requests/errors), ID generators, or basic lock-free flags.
*   **When NOT to Use:** If you have extreme, ultra-high concurrency (e.g., thousands of threads hammering a single counter). The constant CAS retry loops will cause CPU contention. In those rare cases, use `LongAdder` instead.
*   **Code Snippet:**
    ```java
    AtomicInteger requestCount = new AtomicInteger(0);
    
    int current = requestCount.get();              // Read
    int newValue = requestCount.incrementAndGet(); // Atomically increments by 1
    int added = requestCount.addAndGet(5);         // Atomically adds 5
    
    // Compare-And-Swap directly:
    // "If the current value is exactly 5, change it to 10"
    boolean success = requestCount.compareAndSet(5, 10); 
    ```

#### 2. `AtomicReference`
*   **Senior Angle:** This is how you implement DB-style optimistic locking in Java memory. It allows you to atomically swap complex objects. Because CAS relies on exact reference matching (`==`), you must **always use immutable objects** inside an `AtomicReference`.
*   **When to Use:** Implementing lock-free concurrent data structures, or atomically updating a set of grouped variables (like swapping an `x` and `y` coordinate simultaneously without a lock).
*   **When NOT to Use:** When the update logic is extremely heavy/slow. If the computation takes 1 second, and other threads keep modifying the reference in the meantime, your thread will be stuck spinning in the CAS `while(true)` retry loop forever.
*   **Code Snippet:**
    ```java
    // 1. MUST use immutable objects!
    class Point { 
        final int x, y; 
        Point(int x, int y) { this.x=x; this.y=y; } 
    }
    
    AtomicReference<Point> currentPoint = new AtomicReference<>(new Point(0, 0));
    
    public void updatePointOptimistically(int newX, int newY) {
        // The Lock-Free "Retry Loop"
        while (true) {
            Point expected = currentPoint.get(); // 1. Read current state
            Point next = new Point(newX, newY);  // 2. Compute new state
            
            // 3. Atomically check if it's still 'expected'. 
            // If someone else updated it, this returns false and the loop retries!
            if (currentPoint.compareAndSet(expected, next)) {
                break; // Success!
            }
        }
    }
    ```

## 6. Deadlocks (Prevention, Avoidance, & Recovery)
* **What it is:** A lock-ordering problem (Circular Wait) where Thread A holds Lock 1 and waits for Lock 2, while Thread B holds Lock 2 and waits for Lock 1. Both freeze forever.
* **Senior details:** The core issue is always the *order* in which locks are acquired. To guarantee a deadlock in an interview, invert the acquisition order and add a `Thread.sleep()` in between to force the collision.

### 1. The `synchronized` Implementation (Unrecoverable)
```java
Thread t1 = new Thread(() -> {
    synchronized(lockA) { 
        Thread.sleep(50); // Guarantee T2 has time to grab lockB
        synchronized(lockB) { /* Do work */ } 
    }
});
Thread t2 = new Thread(() -> {
    synchronized(lockB) { 
        Thread.sleep(50); 
        synchronized(lockA) { /* Do work */ } 
    }
});
```

### 2. The `ReentrantLock` Implementation (Nested Idiom)
*Always use nested `try-finally` blocks to prevent `IllegalMonitorStateException`!*
```java
Thread t1 = new Thread(() -> {
    lockA.lock();
    try {
        Thread.sleep(50);
        lockB.lock();
        try {
            // Do work
        } finally { lockB.unlock(); }
    } finally { lockA.unlock(); }
});
```

### Senior Handling Strategies (Interview Talking Points)
1. **Prevention (Strict Lock Ordering - Best Approach):** 
   Guarantee threads always acquire locks in the exact same global order. If acquiring dynamic resources, sort them by a unique identifier (like DB ID or `hashCode()`) first.
   ```java
   Lock firstLock = (acc1.id < acc2.id) ? acc1.lock : acc2.lock;
   Lock secondLock = (acc1.id < acc2.id) ? acc2.lock : acc1.lock;
   firstLock.lock(); secondLock.lock(); // Deadlock is mathematically impossible
   ```
2. **Avoidance (Try-Lock with Random Backoff):**
   Instead of blocking blindly, use `.tryLock(timeout)`. If the second lock fails, you must `unlock()` your first lock, sleep for a **random amount of time** (to prevent a *Livelock* where threads constantly collide), and try the whole process again.
   ```java
   while (true) {
       lockA.lock();
       try {
           // Try to get Lock B for 50ms
           if (lockB.tryLock(50, TimeUnit.MILLISECONDS)) {
               try { 
                   return; // Success! Transaction complete.
               } finally { lockB.unlock(); }
           }
       } finally { 
           lockA.unlock(); // Failed to get B. Drop A so other threads can proceed!
       }
       
       // Sleep randomly between 0-100ms to desync threads and prevent Livelock
       Thread.sleep(new Random().nextInt(100)); 
   }
   ```
3. **Recovery (The Watchdog Strategy):**
   Use `lockInterruptibly()`. A background Daemon thread periodically runs `ThreadMXBean.findDeadlockedThreads()`. If a cycle is detected, it selects a "victim" thread and calls `victimThread.interrupt()`, forcing it to drop its lock and let others proceed.
   ```java
   // 1. Worker Threads MUST wait interruptibly
   try {
       lockA.lockInterruptibly(); 
       // ... wait for lockB
   } catch (InterruptedException e) {
       System.out.println("Watchdog detected deadlock! I am the victim. Giving up.");
       Thread.currentThread().interrupt();
   }
   
   // 2. Background Watchdog Thread Logic
   ThreadMXBean bean = ManagementFactory.getThreadMXBean();
   long[] deadlockedIds = bean.findDeadlockedThreads();
   
   if (deadlockedIds != null) {
       // Deadlock detected! Pick the first one as the victim
       long victimId = deadlockedIds[0]; 
       
       // (Assume we keep a map of active threads: Map<Long, Thread> activeThreads)
       Thread victimThread = activeThreads.get(victimId);
       victimThread.interrupt(); // Break the deadlock!
   }
   ```

## 7. Concurrent Collections

A critical part of high-performance backend engineering is knowing how to make standard Java collections thread-safe without destroying application throughput. 

### The Collection Mapping Cheat Sheet
*Based on underlying locking mechanisms:*

| Standard Collection | Concurrent Collection | Locking Mechanism |
| :--- | :--- | :--- |
| **PriorityQueue** | `PriorityBlockingQueue` | `ReentrantLock` |
| **LinkedList / ArrayDeque** | `ConcurrentLinkedDeque` | Compare-And-Swap (CAS) |
| **Queue Interface** | `ConcurrentLinkedQueue` | Compare-And-Swap (CAS) |
| **ArrayList** | `CopyOnWriteArrayList` | `ReentrantLock` (Writes only) |
| **HashSet** | `ConcurrentHashMap.newKeySet()` | Lock Striping / `Synchronized` (Internal bins) |
| **TreeSet** | `Collections.synchronizedSortedSet()` | `Synchronized` wrapper |
| **LinkedHashSet** | `Collections.synchronizedSet()` | `Synchronized` wrapper |

### The Senior Backend Engineer Angle
In a professional backend, you **never** use legacy `Vector` or `Hashtable`. Furthermore, you avoid `Collections.synchronizedList()` or `synchronizedSet()` whenever possible. 

**Why?** The `Collections.synchronized*` wrappers use a **"Monolithic Lock"** (they `synchronized(this)` on the entire collection object). If 10,000 threads try to read a value, 9,999 have to wait in line. 

Instead, senior engineers use modern classes from `java.util.concurrent` because they avoid full locks:
1. **Lock-Free / CAS:** Collections like `ConcurrentLinkedQueue` don't use OS locks at all. They use hardware-level CPU instructions (Compare-And-Swap) to achieve blazing fast, non-blocking concurrency.
2. **Lock Striping:** `ConcurrentHashMap` divides the map into segments (bins). Two threads can write to the map at the exact same time as long as they are writing to different bins!
3. **Copy-On-Write:** `CopyOnWriteArrayList` makes reads 100% lock-free. When a write happens, it literally copies the entire underlying array, adds the item, and swaps the pointer. 

### 1. `PriorityBlockingQueue` (Thread-Safe PriorityQueue)
*   **Senior Angle:** Uses a single `ReentrantLock` for all mutations and a `Condition` to block threads if the queue is empty. Because it uses a monolithic lock, it is not lock-free, but it guarantees absolute ordering priority.
*   **When to Use:** Building a Task Scheduler where high-priority background jobs must jump the queue.
*   **When NOT to Use:** If tasks don't have priority. Use `ConcurrentLinkedQueue` instead for better throughput.
*   **Code Snippet:**
    ```java
    PriorityBlockingQueue<Integer> pq = new PriorityBlockingQueue<>();
    pq.put(10); // Thread-safe insert
    pq.put(1);  // Automatically sorts to the front
    Integer top = pq.take(); // Blocks if empty, returns 1
    ```

### 2. `ConcurrentLinkedQueue` & `Deque` (Thread-Safe LinkedList/ArrayDeque)
*   **Senior Angle:** The holy grail of non-blocking data structures. It uses the Michael-Scott algorithm (Compare-And-Swap) and a dummy node. It has **no locks**, meaning zero thread suspension overhead. 
*   **When to Use:** High-throughput producer-consumer scenarios (e.g., passing 10,000s of websocket messages between thread pools).
*   **When NOT to Use:** If you need to frequently call `.size()`. Because it's lock-free, `.size()` is `O(N)` and has to traverse the entire list!
*   **Code Snippet:**
    ```java
    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    queue.offer("Task1");       // Lock-free CAS insert
    String task = queue.poll(); // Lock-free CAS retrieve and remove
    ```

### 3. `CopyOnWriteArrayList` (Thread-Safe ArrayList)
*   **Senior Angle:** Operates on the "immutability" principle. Every single mutation creates a deep copy of the underlying array. Reads never block; they just read the current snapshot.
*   **When to Use:** "Read-Heavy, Write-Rare" scenarios like caching configs or maintaining Event Listener/Observer lists.
*   **When NOT to Use:** If you write frequently. Copying a large array on every `add()` will cause catastrophic GC pauses and CPU spikes.
*   **Code Snippet:**
    ```java
    List<String> list = new CopyOnWriteArrayList<>();
    list.add("ListenerA");     // SLOW: Locks, copies array, adds, swaps reference
    String item = list.get(0); // FAST: 100% Lock-free read
    ```

### 4. `ConcurrentHashMap.newKeySet()` (Thread-Safe HashSet)
*   **Senior Angle:** Java doesn't have a `ConcurrentHashSet`. Instead, we use `ConcurrentHashMap` backed by a dummy boolean value. It inherits CHM's "Lock Striping", meaning it locks only the specific array bin being written to, allowing massive concurrent writes.
*   **When to Use:** Tracking a massive pool of unique elements (e.g., active user sessions, visited URLs in a web crawler).
*   **When NOT to Use:** When memory footprint is highly constrained (CHM nodes use more memory than a simple array).
*   **Code Snippet:**
    ```java
    Set<String> activeUsers = ConcurrentHashMap.newKeySet();
    activeUsers.add("User123");                       // Thread-safe, locks only the specific bucket
    boolean exists = activeUsers.contains("User123"); // Fully concurrent read
    ```

### 5. `Collections.synchronizedSortedSet()` (Thread-Safe TreeSet)
*   **Senior Angle:** It's just a standard `TreeSet` wrapped in a monolithic `synchronized(this)` lock. It has terrible concurrency throughput because every read and write locks the entire tree.
*   **When to Use:** You absolutely *must* have a thread-safe, sorted, unique collection, and concurrency is extremely low.
*   **When NOT to Use:** High-traffic backend services. Instead, use `ConcurrentSkipListSet`, which is the true concurrent (lock-free CAS) alternative to `TreeSet`.
*   **Code Snippet:**
    ```java
    Set<Integer> sortedSet = Collections.synchronizedSortedSet(new TreeSet<>());
    sortedSet.add(5);
    // CRITICAL: MUST synchronize manually when iterating!
    synchronized (sortedSet) {
        for (Integer i : sortedSet) { System.out.println(i); }
    }
    ```

### 6. `Collections.synchronizedSet()` (Thread-Safe LinkedHashSet)
*   **Senior Angle:** Like the TreeSet wrapper, this uses a monolithic lock. We only use this when we specifically need `LinkedHashSet`'s guarantee of **insertion order** preservation, as there is no `ConcurrentLinkedHashSet` in Java.
*   **When to Use:** You need a thread-safe set that remembers the exact order items were added (e.g., building an LRU Cache skeleton).
*   **When NOT to Use:** Standard unordered uniqueness checks. Always use `ConcurrentHashMap.newKeySet()` instead.
*   **Code Snippet:**
    ```java
    Set<String> orderedSet = Collections.synchronizedSet(new LinkedHashSet<>());
    orderedSet.add("First");
    // CRITICAL: MUST synchronize manually when iterating!
    synchronized (orderedSet) {
        orderedSet.forEach(System.out::println); 
    }
    ```
