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

## 8. CompletableFuture + ThreadPoolExecutor Cheat Sheet

This section is a practical "what to use, when to use it" guide for async programming in Java backend systems.

---

### Part A. `CompletableFuture` Mental Model

`CompletableFuture` is used when:
* You want to run tasks asynchronously without blocking the current thread.
* You want to chain multiple dependent steps.
* You want to combine results from multiple independent async calls.
* You want to handle exceptions in async flows cleanly.

Think of it as:
* `Future` + callback chaining + result transformation + composition.

---

### Part B. How to Create a `CompletableFuture`

#### 1. `completedFuture(value)`
```java
CompletableFuture<String> cf = CompletableFuture.completedFuture("done");
```
* **Use Case:** You already have the value, but your method signature must return `CompletableFuture`.
* **Typical scenario:** Service method returns cached data immediately.

#### 2. `runAsync(Runnable)`
```java
CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> sendMetric());
```
* **Use Case:** Fire-and-forget background work.
* **Return type:** `CompletableFuture<Void>`
* **Use when:** You do not need any result.
* **Examples:** Audit log, analytics push, notification trigger.

#### 3. `supplyAsync(Supplier<T>)`
```java
CompletableFuture<User> cf = CompletableFuture.supplyAsync(() -> fetchUser());
```
* **Use Case:** Async task that produces a value.
* **Use when:** Calling DB, remote API, cache layer, file read, heavy computation.

#### 4. Async with custom executor
```java
ExecutorService ioPool = Executors.newFixedThreadPool(20);
CompletableFuture<User> cf =
    CompletableFuture.supplyAsync(() -> fetchUser(), ioPool);
```
* **Use Case:** Very important in backend code.
* **Use when:** You do not want work to run on the common pool.
* **Rule:** For production code, prefer passing your own executor for I/O-heavy or critical workloads.

---

### Part C. Transforming Results

#### 1. `thenApply(fn)`
```java
CompletableFuture<String> name =
    userFuture.thenApply(user -> user.getName());
```
* **Use Case:** Transform result from one type to another.
* **Think:** Sync map operation.
* **Example:** `User -> userId`, `Response -> DTO`

#### 2. `thenAccept(consumer)`
```java
userFuture.thenAccept(user -> System.out.println(user));
```
* **Use Case:** Consume result, return nothing.
* **Think:** End of pipeline where you only need side effect.

#### 3. `thenRun(runnable)`
```java
userFuture.thenRun(() -> System.out.println("done"));
```
* **Use Case:** Run next step after completion, but without using previous result.

---

### Part D. Chaining Dependent Async Calls

#### `thenCompose(fn)`
```java
CompletableFuture<Order> orderFuture =
    userFuture.thenCompose(user -> fetchLatestOrder(user.getId()));
```
* **Use Case:** Second async call depends on first result.
* **Think:** `flatMap` for async.
* **Use when:** First call gives `userId`, second call fetches order/profile/recommendations.

**Rule of Thumb:**
* `thenApply()` -> transforms `T -> U`
* `thenCompose()` -> transforms `T -> CompletableFuture<U>`

---

### Part E. Combining Independent Async Tasks

#### 1. `thenCombine(other, fn)`
```java
CompletableFuture<User> userFuture = fetchUserAsync();
CompletableFuture<Account> accountFuture = fetchAccountAsync();

CompletableFuture<UserSummary> summaryFuture =
    userFuture.thenCombine(accountFuture, (user, account) ->
        new UserSummary(user, account));
```
* **Use Case:** Two independent async tasks, combine both results.
* **Examples:** User data + account data, product data + pricing data.

#### 2. `allOf(f1, f2, f3...)`
```java
CompletableFuture<Void> all =
    CompletableFuture.allOf(f1, f2, f3);
```
* **Use Case:** Wait for all tasks to complete.
* **Examples:** Fan-out API calls, preload caches, bulk processing.
* **Important:** `allOf()` returns `CompletableFuture<Void>`, so you still extract values from original futures.

#### 3. `anyOf(f1, f2, f3...)`
```java
CompletableFuture<Object> first =
    CompletableFuture.anyOf(f1, f2, f3);
```
* **Use Case:** Proceed with whichever task finishes first.
* **Examples:** Multi-region fallback, fastest mirror/server wins.

---

### Part F. Async vs Non-Async Methods

Examples:
* `thenApply()`
* `thenApplyAsync()`
* `thenApplyAsync(fn, executor)`

**Difference:**
* `thenApply()` may run in the same thread that completed the previous stage.
* `thenApplyAsync()` schedules the next stage asynchronously.
* `thenApplyAsync(fn, executor)` schedules it on your chosen pool.

**Use Case Guidance:**
* Use non-async variants for lightweight transformations.
* Use async variants for heavy work or when you want thread isolation.
* Prefer async with custom executor in production-critical code.

---

### Part G. Error Handling Methods

#### 1. `exceptionally(fn)`
```java
CompletableFuture<String> safe =
    riskyFuture.exceptionally(ex -> "fallback");
```
* **Use Case:** Return fallback value on failure.
* **Examples:** Default config, empty list, cached stale response.

#### 2. `handle((result, ex) -> ...)`
```java
CompletableFuture<String> handled =
    riskyFuture.handle((res, ex) -> ex == null ? res : "fallback");
```
* **Use Case:** Always run, whether success or failure.
* **Use when:** You need both result and exception in one place.

#### 3. `whenComplete((result, ex) -> ...)`
```java
riskyFuture.whenComplete((res, ex) -> log.info("completed"));
```
* **Use Case:** Side effects like logging, metrics, tracing.
* **Important:** It does not transform the result by itself.

**Rule of Thumb:**
* `exceptionally()` -> recover from failure
* `handle()` -> inspect and transform success/failure
* `whenComplete()` -> observe completion, usually for logging

---

### Part H. Blocking Methods (Use Carefully)

#### 1. `join()`
```java
String result = future.join();
```
* **Use Case:** Wait and get result.
* **Difference:** Throws unchecked `CompletionException`.
* **Use when:** Simpler code in internal layers or aggregation boundary.

#### 2. `get()`
```java
String result = future.get();
```
* **Use Case:** Same as `join()`, but checked exception style.
* **Difference:** Throws checked exceptions like `ExecutionException`, `InterruptedException`.

#### 3. Timeout methods
```java
future.orTimeout(2, TimeUnit.SECONDS);
future.completeOnTimeout(defaultValue, 2, TimeUnit.SECONDS);
```
* **`orTimeout()` Use Case:** Fail fast if dependency is too slow.
* **`completeOnTimeout()` Use Case:** Return fallback value after timeout.

**Senior Rule:** Avoid blocking in request threads unless this is the final aggregation boundary. If everything calls `.join()` too early, async design loses its benefit.

---

### Part I. `CompletableFuture` Use Cases Cheat Sheet

| Situation | Method / Pattern |
| :--- | :--- |
| Already have value, but API returns future | `completedFuture()` |
| Run background task, no return value | `runAsync()` |
| Run async task that returns value | `supplyAsync()` |
| Transform result | `thenApply()` |
| Use result and finish | `thenAccept()` |
| Run next step without result | `thenRun()` |
| Dependent async call | `thenCompose()` |
| Merge two independent futures | `thenCombine()` |
| Wait for all tasks | `allOf()` |
| Use fastest task | `anyOf()` |
| Fallback on exception | `exceptionally()` |
| Inspect both success and failure | `handle()` |
| Logging/cleanup after completion | `whenComplete()` |
| Fail if too slow | `orTimeout()` |
| Return default if too slow | `completeOnTimeout()` |

---

### Part J. Thread Pool Basics

Java thread pools are usually managed through:
* `Executor`
* `ExecutorService`
* `ScheduledExecutorService`
* `ThreadPoolExecutor`

`ThreadPoolExecutor` is the real configurable implementation behind many executors.

Constructor shape:
```java
new ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    timeUnit,
    workQueue,
    threadFactory,
    rejectedExecutionHandler
);
```

---

### Part K. ThreadPoolExecutor Parameters and When They Matter

#### 1. `corePoolSize`
* Minimum number of worker threads kept ready.
* **Use Case:** Baseline concurrency level for steady traffic.

#### 2. `maximumPoolSize`
* Max number of threads pool can grow to.
* **Use Case:** Burst handling when queue is full.

#### 3. `keepAliveTime`
* Extra non-core threads die after being idle.
* **Use Case:** Save memory/resources after traffic spike.

#### 4. `workQueue`
Common queue choices:

##### `LinkedBlockingQueue`
* Can queue many tasks.
* **Use Case:** Stable background workload where queueing is acceptable.
* **Risk:** Unbounded queue can hide overload and increase memory usage.

##### `ArrayBlockingQueue`
* Fixed-size bounded queue.
* **Use Case:** You want backpressure and controlled memory.
* **Best for:** Production systems where overload must be visible.

##### `SynchronousQueue`
* No storage. Task must be handed directly to a thread.
* **Use Case:** Short-lived bursty tasks, aggressive thread scaling.
* **Used by:** Cached thread pool style behavior.

##### `PriorityBlockingQueue`
* Tasks are ordered by priority.
* **Use Case:** Priority job scheduling.

#### 5. `ThreadFactory`
* Customize thread names, daemon flag, priority, uncaught exception handler.
* **Use Case:** Better debugging and observability.

#### 6. `RejectedExecutionHandler`
When pool is saturated and queue is full:

* `AbortPolicy`
  * Throws exception.
  * **Use Case:** Fail fast. Good when task loss is unacceptable.

* `CallerRunsPolicy`
  * Calling thread runs task.
  * **Use Case:** Natural backpressure.
  * **Best for:** Slowing producers when consumers are overloaded.

* `DiscardPolicy`
  * Silently drops task.
  * **Use Case:** Rare. Only for non-critical best-effort work.

* `DiscardOldestPolicy`
  * Drops oldest queued task.
  * **Use Case:** Rare. Sometimes useful in stale-work systems.

---

### Part L. Types of Thread Pools and When to Use Them

#### 1. Fixed Thread Pool
```java
ExecutorService pool = Executors.newFixedThreadPool(10);
```
* **Backed by:** `ThreadPoolExecutor`
* **Behavior:** Fixed number of threads, extra tasks wait in queue.
* **Use Case:** Stable throughput, controlled concurrency.
* **Examples:** DB calls, API processing, worker consumers.
* **Good when:** You know the safe concurrency limit.

#### 2. Cached Thread Pool
```java
ExecutorService pool = Executors.newCachedThreadPool();
```
* **Backed by:** `ThreadPoolExecutor` + `SynchronousQueue`
* **Behavior:** Creates threads as needed, reuses idle ones.
* **Use Case:** Many short-lived async tasks.
* **Risk:** Can create too many threads under heavy load.
* **Avoid when:** Traffic is unbounded or external dependency is slow.

#### 3. Single Thread Executor
```java
ExecutorService pool = Executors.newSingleThreadExecutor();
```
* **Behavior:** Exactly one worker thread, tasks execute sequentially.
* **Use Case:** Ordering must be preserved.
* **Examples:** Event stream processing, file writes, ordered updates.

#### 4. Scheduled Thread Pool
```java
ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);
```
* **Use Case:** Delay or periodic execution.
* **Examples:** Polling, heartbeat, token refill, cleanup jobs.

Important methods:
```java
pool.schedule(task, 5, TimeUnit.SECONDS);
pool.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS);
pool.scheduleWithFixedDelay(task, 0, 10, TimeUnit.SECONDS);
```

* `schedule()` -> run once after delay
* `scheduleAtFixedRate()` -> fixed cadence, next run based on start time
* `scheduleWithFixedDelay()` -> next run starts after previous run finishes + delay

**Rule:**
* Use `scheduleAtFixedRate()` for heartbeat/metrics cadence.
* Use `scheduleWithFixedDelay()` when task duration may vary and overlap must be avoided.

#### 5. Work Stealing Pool
```java
ExecutorService pool = Executors.newWorkStealingPool();
```
* **Backed by:** `ForkJoinPool`
* **Use Case:** CPU-heavy tasks split into smaller subtasks.
* **Examples:** Parallel computation, recursive divide-and-conquer.
* **Avoid for:** Blocking I/O work.

#### 6. Custom `ThreadPoolExecutor`
```java
ExecutorService pool = new ThreadPoolExecutor(
    10,
    20,
    60,
    TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),
    Executors.defaultThreadFactory(),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```
* **Use Case:** Real backend systems where you need explicit limits, backpressure, queue control, and rejection policy.
* **Best choice when:** Interviewer asks for production-ready thread-pool design.

---

### Part M. Which Thread Pool Should I Use?

| Use Case | Best Choice |
| :--- | :--- |
| Strict ordering, one-by-one processing | `newSingleThreadExecutor()` |
| Stable worker concurrency | `newFixedThreadPool()` |
| Delayed or periodic tasks | `newScheduledThreadPool()` |
| CPU-bound recursive parallel work | `newWorkStealingPool()` / `ForkJoinPool` |
| Bursty short async tasks with caution | `newCachedThreadPool()` |
| Production-safe bounded execution | Custom `ThreadPoolExecutor` |

---

### Part N. CPU-Bound vs I/O-Bound Pool Sizing

#### CPU-Bound
* **Examples:** Parsing, encryption, image processing, rule evaluation.
* **Pool size rule:** Around number of CPU cores.
* **Why:** Too many threads cause context switching overhead.

#### I/O-Bound
* **Examples:** DB calls, HTTP calls, Kafka/network waits, file reads.
* **Pool size rule:** Usually larger than CPU cores, because threads spend time waiting.
* **Why:** While one thread is blocked on I/O, another can run.

**Interview answer:**
* CPU-bound -> small pool near core count
* I/O-bound -> larger pool based on wait time vs compute time

---

### Part O. Important ExecutorService Methods

#### Task submission
```java
pool.execute(runnable);          // no return value
Future<String> f = pool.submit(callable); // returns Future
```

* `execute()` -> fire task, no `Future`
* `submit()` -> use when result/exception tracking is needed

#### Bulk submission
```java
pool.invokeAll(tasks);
pool.invokeAny(tasks);
```

* `invokeAll()` -> run all, wait for all
* `invokeAny()` -> return one successful result, cancel rest if possible

#### Shutdown
```java
pool.shutdown();
pool.shutdownNow();
pool.awaitTermination(10, TimeUnit.SECONDS);
```

* `shutdown()` -> stop accepting new tasks, finish queued tasks
* `shutdownNow()` -> attempt to interrupt running tasks
* `awaitTermination()` -> wait for graceful stop

---

### Part P. Senior Backend Rules of Thumb

1. Do not use the default common pool blindly for business-critical async work.
2. Separate CPU-bound and I/O-bound workloads into different executors.
3. Prefer bounded queues in production to avoid silent memory blowups.
4. Always think about rejection policy. Overload behavior is a design choice.
5. Use `CallerRunsPolicy` when you want simple backpressure.
6. Use `CompletableFuture.thenCompose()` for dependent async calls, not nested futures.
7. Use `allOf()` for fan-out aggregation, but extract results carefully from original futures.
8. Use timeout methods for remote calls so stuck dependencies do not stall the whole pipeline.
9. Do not call `.join()` too early in the flow or you lose non-blocking benefit.
10. Name your pool threads clearly in production for debugging.
