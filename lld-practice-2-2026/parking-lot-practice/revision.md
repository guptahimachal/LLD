# Parking Lot LLD - Quick Revision & Learnings

## 1. Initial Implementation: What Went Well
* **Clear Domain Modeling:** Successfully identified the core entities (`ParkingLot`, `Floor`, `ParkingSpot`, `Vehicle`, `Booking`, `Invoice`).
* **Design Patterns:** Effectively used the **Strategy Pattern** (`AllocationStrategy`, `BillingStrategy`) which makes the system open for extension and closed for modification (OCP).
* **Builder Pattern:** Used `@Builder` via Lombok to cleanly construct objects like `Booking` and `Invoice`.
* **Functional Programming:** Good use of Java Streams and `Optional` to filter available spots.

## 2. What Was Improved (Key Fixes)
* **Dependency Injection (Coupling):** The initial `ParkingLotManager` hardcoded `new DefaultAllocationStrategy()` in its constructor. This was removed to force Dependency Injection, allowing for easier unit testing and swapping of strategies.
* **Initialization Bugs:** `List<Floor>` in `ParkingLot` and `List<ParkingSpot>` in `Floor` were initially null. They were instantiated with `= new ArrayList<>()` to prevent `NullPointerException`.
* **Billing Logic:** The initial billing logic (`Duration.toHours() - 1`) ignored fractional hours. It was updated to use `Math.ceil(minutes / 60.0)` for accurate rounding.
* **Thread Safety:** The initial code was vulnerable to **double-booking** race conditions. This was completely overhauled using explicit locks.

## 3. Concurrency Models & Locking Strategies
A Parking Lot is a highly concurrent system. Multiple gates can try to book a spot simultaneously.

### Optimistic Locking (Highly Preferred for Parking Lots)
* **Concept:** Assume collisions are rare. Don't block threads while searching. Only lock at the exact microsecond of updating.
* **Implementation:** Used Java's **`StampedLock`**. 
  * Call `tryOptimisticRead()` to get a stamp.
  * Read the state (`isEmpty`).
  * `validate(stamp)` to ensure nobody changed it while reading.
  * `tryConvertToWriteLock(stamp)` to instantly upgrade to a write lock and book it.
* **Why it's best here:** Parking lots are heavily **Read-Intensive** (display boards, allocation searches). Optimistic locking allows infinite parallel reads without blocking, and since spotting collisions are rare (thousands of spots), it scales incredibly well.

### Pessimistic Locking
* **Concept:** Assume collisions will happen. Block everything until you are done.
* **Implementation:** Used **`ReentrantLock`** on the specific `ParkingSpot` (Fine-Grained Locking) rather than the whole `ParkingLotManager` (Global Lock).
* **When to use:** Only if the lot is constantly 99.9% full and high contention is guaranteed. Otherwise, the OS context-switching overhead of putting threads to sleep makes it a bottleneck.

## 4. Addressing Initial Scoping Doubts

### A. Interface vs Abstract Class (e.g., `Vehicle` & `ParkingSpot`)
* **Your implementation used Enums (`VehicleType`, `ParkingSpotType`)** instead of Interfaces/Abstract classes. **This was actually the right choice!** 
* You only need an Interface if different vehicles have completely different *behaviors*. If the only difference is data (size, weight, rate), Enums or a single concrete class are much cleaner than a "Subclass Explosion" (e.g., creating `Car`, `Truck`, `Bike` classes).
* **Rule of thumb:** Interface = Defines a contract of *Behavior*. Abstract Class = Shares core *State/Fields* + Behavior.

### B. Why does the Parking Spot care about the Vehicle?
* In your code, you put `isVehicleCompatible(Vehicle)` inside `ParkingSpot`. This is actually a great example of **Domain-Driven Design (DDD)** (the "Tell, Don't Ask" principle). 
* However, in a strict microservice/anemic model, the `ParkingSpot` should just be a dumb data object, and the **Strategy** (or a separate `CompatibilityService`) should compare the Spot's constraints against the Vehicle's metadata. Both approaches are valid in an interview, just be ready to justify yours!

### C. How to handle Strategy Interfaces needing different inputs?
* You rightly noticed in your scoping: *"Nearest strategy might require Floor as input, but Default doesn't."*
* **The Solution:** You used `suggestParkingSpot(Vehicle, Entrance)`. To make this even more future-proof, you should use a **Context Object** pattern:
  ```java
  public ParkingSpot suggestParkingSpot(ParkingContext context);
  ```
  The `ParkingContext` class would contain `Vehicle`, `Entrance`, `Timestamp`, `PreferredFloor`, etc. This way, if a new strategy requires a new piece of data tomorrow, you just add it to the `ParkingContext` without breaking the `AllocationStrategy` interface signature!

## 5. Cheat Sheet / Templates for Future Interviews

### 1. The Optimistic Lock Template (`StampedLock`)
Use this when **reads heavily outnumber writes**, and contention/collisions are rare (e.g., Booking a Movie Seat out of 200, Reserving a Parking Spot out of 1000). 

**Why it's better for read-heavy operations:** `tryOptimisticRead()` does not acquire a real lock and does not block *any* threads. Millions of threads can read the state simultaneously at full CPU speed without ever putting each other to sleep (no OS context-switching overhead). It only enforces mutual exclusion during the rare, split-second an actual write occurs.
```java
import java.util.concurrent.locks.StampedLock;

public class Resource {
    private boolean isAvailable = true;
    private final StampedLock lock = new StampedLock();

    public boolean bookOptimistically() {
        // 1. Get an optimistic read stamp (Does NOT block any threads!)
        long stamp = lock.tryOptimisticRead();
        
        // 2. Read shared state into a local thread variable
        boolean currentStatus = isAvailable;

        // 3. Check if it looks available
        if (currentStatus) {
            // 4. Validate that nobody changed the state while we were reading
            if (lock.validate(stamp)) {
                // 5. Try to instantly upgrade to an exclusive Write Lock
                long writeStamp = lock.tryConvertToWriteLock(stamp);
                if (writeStamp != 0L) { // Non-zero means we won the race!
                    try {
                        isAvailable = false; 
                        return true;
                    } finally {
                        lock.unlockWrite(writeStamp); // Always unlock in finally
                    }
                }
            }
        }
        return false; // Someone else beat us to it, let the caller retry or fail
    }
}
```

### 2. The Pessimistic Lock Template (`ReentrantLock`)
Use this when contention is guaranteed to be extremely high, or the cost of a collision is unacceptable (e.g., Deducting money from a shared bank account balance).

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Resource {
    private boolean isAvailable = true;
    
    // Fine-grained lock for this specific resource instance
    private final Lock lock = new ReentrantLock();

    public boolean bookPessimistically() {
        // 1. Immediately block ANY other thread from interacting with this resource
        lock.lock(); 
        try {
            // 2. Check state safely (no other thread can be here)
            if (isAvailable) {
                isAvailable = false; 
                return true;
            }
            return false;
        } finally {
            // 3. Always release the lock in a finally block to prevent Deadlocks!
            lock.unlock(); 
        }
    }
}
```

**When is Pessimistic actually better than Optimistic?**
1. **Resource Pooling (e.g., DB Connections):** If resources are held for only a few milliseconds, waiting in a queue (sleeping) is better than instant failure. By the time the thread wakes up, the resource is usually free again and the operation succeeds.
2. **CPU Protection (Extreme Contention):** If 50,000 threads try to book the exact same concert ticket, Optimistic retries would cause 100% CPU starvation ("Spin Death"). Pessimistic safely puts 49,999 threads to sleep (0% CPU usage) so the server can survive and process them systematically.
