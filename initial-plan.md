# 🎯 LLD Revision Plan — SDE2 / Senior Backend Engineer
> **Experience**: 4.7 years | **Context**: Revising after ~1.5 years gap  
> **Today's Date**: 12 May 2026  
> **Estimated Total Time Needed**: ~2 weeks (focused, ~2–3 hrs/day)

---

## ✅ Current Progress Snapshot

| Topic | Status |
|---|---|
| OOP Fundamentals | ✅ Done |
| SOLID Principles | ✅ Done (`SOLID_REVISION.md`) |
| Creational Design Patterns (5/5) | ✅ Done (`DESIGN_PATTERNS_REVISION.md`) |
| Structural Design Patterns (7/7) | ✅ Done (`DESIGN_PATTERNS_REVISION.md`) |
| Behavioral Design Patterns (10) | 🔄 Stubs only — needs code + senior angle |
| LLD Questions (Parking Lot, Splitwise, Elevator, etc.) | 🔄 Folders exist — needs implementation |
| Concurrency in LLD | ❌ Not started |
| Timed Mock Rounds | ❌ Not started |

---

## 🗺️ Revised Preparation Phases

```
Phase 0 (DONE)   → OOP + SOLID + Creational + Structural
Phase 1 (NOW)    → Behavioral Patterns          [2–3 days]
Phase 2          → Concurrency Crash Course     [1 day]
Phase 3          → LLD Questions (Tiered)       [7–8 days]
Phase 4          → Mock + Articulation Drill    [2 days]
```

---

# 📅 WEEK 1 — Complete the Revision Foundation

## Day 1–2 → Behavioral Patterns (HIGH PRIORITY)

> You have stubs in `DESIGN_PATTERNS_REVISION.md`. Now add code + senior angle to each.

### Priority Tier 1 — MUST KNOW (appear in LLD questions directly)

| Pattern | Why It Matters for Senior |
|---|---|
| **Strategy** | Eliminates switch/if-else. Payment methods, pricing, sorting. Core to OCP. |
| **Observer** | Foundation of event-driven systems, Kafka concepts, notification systems. |
| **State** | FSM for Order/Vending Machine/Elevator lifecycle. Interviewers love this. |
| **Command** | Task queues, undo/redo, async job processing. Very backend-relevant. |
| **Chain of Responsibility** | Middleware, Spring Security filters, request pipelines. |

### Priority Tier 2 — GOOD TO KNOW

| Pattern | Why It Matters |
|---|---|
| **Template Method** | IoC / Hollywood Principle. Used in Spring JdbcTemplate. |
| **Iterator** | Uniform traversal. Already solid from Java usage. |
| **Mediator** | Event buses, Chat Room design. |
| **Memento** | Undo/redo, state snapshots. |
| **Visitor** | AST traversal, compiler design problems. |

### ✅ Action for Each Behavioral Pattern:
1. Write a **real-world Java code example** (same quality as Creational/Structural notes)
2. Add `💡 Why it fits & Senior Angle` block
3. Add `🌍 Real-World Example` (Java core / Spring / a backend system)
4. Note **when NOT to use** it

---

## Day 3 → Concurrency in LLD (SDE2 MUST HAVE)

> This is the upgrade that separates SDE1 from SDE2 answers.

### What to Revise:

```
✅ Thread safety fundamentals
✅ synchronized vs ReentrantLock vs ReadWriteLock
✅ volatile keyword (you know this from Singleton DCL)
✅ ConcurrentHashMap, CopyOnWriteArrayList
✅ Atomic classes (AtomicInteger, AtomicReference)
✅ Producer-Consumer pattern (BlockingQueue)
✅ Deadlock — how to identify + prevent
```

### Key LLD Concurrency Scenarios to Prepare:

| Scenario | Pattern Used |
|---|---|
| Multiple users booking same parking spot | ReentrantLock / synchronized block |
| Vending machine concurrent purchases | synchronized state transitions |
| Rate Limiter (token bucket) | AtomicInteger + scheduled reset |
| Elevator concurrent floor requests | BlockingQueue / PriorityQueue |
| Chat room message broadcasting | Observer + synchronized list |

> **Senior Signal**: Don't just say "use synchronized." Explain *what* race condition exists, *why* that lock scope is correct, and what the *cost* is (e.g., throughput, latency).

---

# 📅 WEEK 2 — LLD Problem Practice (Tiered Approach)

> **Goal**: For each problem, don't just code — narrate. Practice speaking your design aloud.

## LLD Question Solving Framework (Use Every Time)

```
1. Clarify Requirements   → Ask 2–3 scoping questions
2. Identify Entities      → Core nouns = classes
3. Define Relationships   → Composition / Aggregation / Association
4. Define Interfaces      → What varies? What's stable?
5. Apply Patterns         → Which patterns naturally fit?
6. Write Core Classes     → Focus on contracts, not syntax
7. Handle Concurrency     → "What if 2 users hit this simultaneously?"
8. Discuss Extensibility  → "How would you add feature X?"
```

---

## 🎯 LLD Problem Master List — Market-Informed (Day 4–9)

> Problems selected based on: (a) current market frequency at top Indian/global tech companies, (b) design pattern coverage, (c) concurrency exposure, and (d) senior signal value.

---

### 📊 Problem Frequency Map by Company

| Problem | Amazon | Flipkart | Swiggy/Zomato | Uber/Ola | Razorpay/PhonePe | Zepto/Meesho | Difficulty |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Parking Lot | ✅ | ✅ | – | – | – | – | Medium |
| Movie Ticket Booking (BookMyShow) | ✅ | ✅ | – | – | – | – | Medium-Hard |
| Uber / Ride Sharing | – | – | – | ✅ | – | – | Hard |
| Food Delivery (Swiggy) | – | ✅ | ✅ | – | – | ✅ | Hard |
| Elevator System | ✅ | ✅ | – | – | – | – | Medium |
| Splitwise | – | ✅ | – | – | ✅ | – | Medium |
| LRU/LFU Cache | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | Medium |
| Rate Limiter | ✅ | – | ✅ | ✅ | ✅ | ✅ | Medium |
| Notification System | – | ✅ | ✅ | ✅ | ✅ | ✅ | Medium |
| Vending Machine | ✅ | – | – | – | – | – | Easy-Medium |
| Library Management | ✅ | – | – | – | – | – | Easy-Medium |
| Chess / Tic-Tac-Toe | ✅ | ✅ | – | – | – | – | Easy |
| Wallet / Payment Gateway | – | – | – | – | ✅ | – | Medium |
| ATM Machine | – | ✅ | – | – | ✅ | – | Medium |
| Logging Framework | ✅ | – | – | – | – | – | Easy-Medium |

---

## 🏆 Tier 1 — MUST SOLVE (Extremely High Frequency, Maximum ROI)

> These 5 cover ~80% of what you'll face. Non-negotiable.

---

### 1. 🅐 Parking Lot *(folder exists)*
> **Frequency**: 🔴 Very High | **Time**: 60–75 mins | **Asked at**: Amazon, Flipkart, Microsoft, Oracle

**Why it's the best starter**: Covers every OOP fundamental — inheritance (Vehicle types), encapsulation (Spot state), composition (Floor has Spots), and polymorphism. Forces Factory + Strategy.

**Core Entities**: `ParkingLot`, `Floor`, `ParkingSpot` (Compact/Large/EV/Handicapped), `Vehicle` (Car/Truck/Bike), `Ticket`, `PricingStrategy`

**Patterns to Apply**:
- **Factory** → `VehicleFactory` creates correct vehicle type
- **Strategy** → `HourlyPricingStrategy` vs `FlatRatePricingStrategy`
- **Singleton** → `ParkingLotManager` — one instance manages the lot
- **Observer** → `DisplayBoard` updates when spot count changes

**Concurrency Scenario**:
> "Two cars enter simultaneously — both see 1 remaining spot. How do you prevent double-booking?"
→ `synchronized` on `assignSpot()` or `ReentrantLock` on spot assignment per floor

**Senior Extension Questions**:
- Add EV charging spots with a charge-time queue
- Add dynamic surge pricing (Strategy swap at runtime)
- Add reservation system (book before arrival)

---

### 2. 🅑 Movie Ticket Booking (BookMyShow) *(NEW — not in workspace)*
> **Frequency**: 🔴 Very High | **Time**: 75–90 mins | **Asked at**: Amazon, Flipkart, Myntra, PhonePe

**Why it's critical**: The single most commonly asked *concurrency-heavy* LLD problem. Seat booking under contention is what separates SDE1 from SDE2 answers.

**Core Entities**: `Movie`, `Theatre`, `Screen`, `Show`, `Seat` (Silver/Gold/Platinum), `Booking`, `User`, `Payment`

**Patterns to Apply**:
- **Factory** → `SeatFactory` creates seat types
- **Strategy** → `SeatSelectionStrategy` (random vs best-available vs specific)
- **Observer** → Notify user on booking confirmation/cancellation
- **Command** → `BookingCommand` (supports undo = cancellation)
- **Singleton** → `BookingManager` or `TheatreRegistry`

**Concurrency Scenario**:
> "1000 users try to book seat A5 for a superhit movie at the same moment. How do you handle this?"
→ **Optimistic locking**: CAS on seat status (`AVAILABLE → LOCKED → BOOKED`)
→ **Pessimistic locking**: `synchronized` block on the specific seat object
→ TTL-based lock: seat locked for 10 mins during payment, released if timeout

**Senior Extension Questions**:
- Add seat lock timeout (auto-release after 10 mins if unpaid)
- Add waitlist system when all seats booked
- Support partial cancellation (refund N seats from a group booking)

---

### 3. 🅒 Elevator System *(folder exists)*
> **Frequency**: 🔴 High | **Time**: 60–75 mins | **Asked at**: Amazon, Flipkart, Microsoft, Walmart

**Why it's important**: The classic State Machine + Scheduling Strategy problem. Tests if you can model lifecycle transitions cleanly.

**Core Entities**: `ElevatorSystem`, `Elevator`, `ElevatorState` (IDLE/MOVING_UP/MOVING_DOWN/DOOR_OPEN), `Request` (internal/external), `Scheduler`

**Patterns to Apply**:
- **State** → `IdleState`, `MovingUpState`, `MovingDownState`, `DoorOpenState`
- **Strategy** → `FCFSScheduler` vs `SCANScheduler` (Disk Scheduling analogy)
- **Observer** → Floor display panels update on elevator arrival
- **Command** → Each floor request is a `Command` in a queue

**Concurrency Scenario**:
> "Multiple floors press Up simultaneously — how do you dispatch without skipping requests?"
→ `PriorityQueue<Request>` with lock on enqueue/dequeue
→ Each `Elevator` runs its own thread, polls from its request queue

**Senior Extension Questions**:
- Multiple elevators with smart dispatch (assign nearest idle elevator)
- VIP/Emergency mode — elevator jumps to priority floor
- Weight sensor — reject request if overloaded

---

### 4. 🅓 LRU Cache / LFU Cache *(NEW — not in workspace)*
> **Frequency**: 🔴 Very High | **Time**: 45–60 mins | **Asked at**: EVERY major company

**Why it's critical**: Asked at almost every top company. Tests data structure knowledge inside OOP. Deceptively simple but rich in design decisions.

**Core Entities**: `Cache<K,V>`, `Node`, `DoublyLinkedList`, `HashMap`, `EvictionPolicy`

**Patterns to Apply**:
- **Strategy** → `EvictionPolicy` interface: `LRUEvictionPolicy` vs `LFUEvictionPolicy`
- **Factory** → `CacheFactory.create(type, capacity)` returns correct impl
- **Decorator** → `TTLCache` wraps any cache with expiry logic

**LRU Implementation**:
```
HashMap<K, Node> + DoublyLinkedList
- get(key)  → O(1): move node to head
- put(key)  → O(1): add to head, evict tail if over capacity
```

**LFU Implementation**:
```
HashMap<K, Node> + HashMap<freq, DoublyLinkedList> + minFreq counter
- get/put both update frequency buckets
```

**Concurrency Scenario**:
> "Multiple threads reading/writing cache simultaneously?"
→ `ReadWriteLock` — concurrent reads allowed, exclusive write lock
→ Or use `ConcurrentHashMap` + synchronized list operations

**Senior Extension**:
- TTL-based expiry (add timestamp to node, lazy eviction on get)
- Thread-safe wrapper using `ReadWriteLock`
- Distributed cache discussion (Redis equivalent)

---

### 5. 🅔 Splitwise *(folder exists)*
> **Frequency**: 🟠 High | **Time**: 60–75 mins | **Asked at**: Flipkart, Razorpay, Swiggy, PhonePe

**Core Entities**: `User`, `Group`, `Expense`, `Split` (Equal/Exact/Percentage/Share), `Balance`, `Transaction`

**Patterns to Apply**:
- **Strategy** → `SplitStrategy`: `EqualSplit`, `ExactSplit`, `PercentageSplit`
- **Observer** → Notify users when added to an expense
- **Command** → `SettleUpCommand` (encapsulates a settlement transaction)

**The Hard Part** (senior signal):
> "How do you minimize the number of transactions needed to settle a group?"
→ Graph simplification: net balance per person → creditor list + debtor list → greedy matching
→ This is the actual algorithm that Splitwise uses

**Concurrency Scenario**:
> "Two people add expenses to the same group simultaneously"
→ `ReentrantLock` on group-level balance map, or optimistic locking with version counter

---

## 🥈 Tier 2 — HIGH VALUE (Frequently Asked, Strong Differentiators)

> Solve at least 3 from this tier. Pick based on the companies you're targeting.

---

### 6. 🅕 Notification System / Event Bus *(NEW)*
> **Frequency**: 🟠 High | **Time**: 45–60 mins | **Asked at**: Swiggy, Uber, Flipkart, Meesho, PhonePe

**Why it's hot right now**: Every modern backend system has notifications. Tests Observer + Factory + Chain of Responsibility deeply.

**Core Entities**: `NotificationService`, `NotificationChannel` (Email/SMS/Push/WhatsApp), `User`, `NotificationTemplate`, `EventType`

**Patterns to Apply**:
- **Observer** → `EventPublisher` notifies all registered `EventSubscriber`s
- **Factory** → `ChannelFactory` returns correct `NotificationChannel` impl
- **Chain of Responsibility** → Retry → RateLimit → DeliveryLog pipeline
- **Strategy** → Choose channel based on user preference or event type

**Senior Angle**: Discuss async delivery (queue-based), retry with backoff, idempotency, and delivery receipts.

---

### 7. 🅖 Rate Limiter *(folder exists)*
> **Frequency**: 🟠 High | **Time**: 45–60 mins | **Asked at**: Uber, Razorpay, Amazon, Swiggy

**Core Entities**: `RateLimiter`, `RateLimitRule`, `RequestContext`, `TokenBucket` / `SlidingWindowCounter`

**Algorithms to Know** (all as Strategy):
| Algorithm | Pros | Cons |
|---|---|---|
| Fixed Window | Simple | Boundary burst problem |
| Sliding Window Log | Accurate | High memory |
| Sliding Window Counter | Balanced | Approximate |
| Token Bucket | Allows bursts, smooth | Slightly complex |
| Leaky Bucket | Strict rate | No burst allowance |

**Patterns to Apply**:
- **Strategy** → Each algorithm is a `RateLimitStrategy` implementation
- **Decorator** → `RateLimitedService` wraps any `Service` transparently
- **Singleton** → Single `RateLimiterRegistry` per app

**Concurrency**: `AtomicInteger` for counters, `volatile` for window timestamps, `ConcurrentHashMap` for per-user buckets

---

### 8. 🅗 Ride Sharing / Uber LLD *(NEW)*
> **Frequency**: 🟠 High | **Time**: 75–90 mins | **Asked at**: Uber, Ola, Swiggy, Rapido

**Core Entities**: `Driver`, `Rider`, `Trip`, `TripState` (REQUESTED/DRIVER_ASSIGNED/IN_PROGRESS/COMPLETED), `Location`, `PricingEngine`, `MatchingService`

**Patterns to Apply**:
- **State** → `Trip` FSM: Requested → Assigned → InProgress → Completed/Cancelled
- **Strategy** → `PricingStrategy` (standard/surge), `MatchingStrategy` (nearest/highest-rated)
- **Observer** → Rider notified on driver assignment; driver notified on new request
- **Factory** → `RideFactory` creates `PoolRide`, `PremiumRide`, `AutoRide`

**Senior Angle**: Surge pricing logic, driver location tracking (not DB — in-memory grid), ETA estimation

---

### 9. 🅘 Wallet / Payment Gateway *(folder exists)*
> **Frequency**: 🟠 High | **Time**: 60 mins | **Asked at**: Razorpay, PhonePe, Paytm, Amazon Pay

**Core Entities**: `Wallet`, `Transaction`, `PaymentMethod` (UPI/Card/NetBanking), `TransactionStatus`, `IdempotencyKey`

**The Critical Senior Concept — Idempotency**:
> "What happens if the network drops after deducting money but before confirming?"
→ Each transaction has an `idempotencyKey` (UUID)
→ On retry, check if key already processed → return cached result, no double-debit

**Patterns to Apply**:
- **Strategy** → Each `PaymentMethod` is a strategy
- **Chain of Responsibility** → Auth → FraudCheck → BalanceCheck → Execute
- **Command** → `DebitCommand` / `CreditCommand` (supports rollback)
- **Observer** → Notify user on transaction status change

---

### 10. 🅙 ATM Machine *(folder exists)*
> **Frequency**: 🟡 Medium | **Time**: 45–60 mins | **Asked at**: Flipkart, TCS, Infosys, Razorpay

**Patterns to Apply**:
- **State** → `IdleState`, `CardInsertedState`, `PINEnteredState`, `TransactionState`, `DispensingState`
- **Chain of Responsibility** → CardValidation → PINValidation → BalanceCheck → Dispense
- **Command** → Each operation (withdraw/deposit/check balance) is a `Command`
- **Singleton** → `ATMContext` (one machine, one state at a time)

---

## 🥉 Tier 3 — GOOD TO KNOW (Quick Drills, 30–45 mins each)

> These are simpler problems — good for warm-up or when a company asks "an easy design question." Don't spend more than 45 mins on any of these.

| Problem | Primary Pattern | Quick Insight |
|---|---|---|
| **Chess** | Strategy (movement rules per piece), Composite (board) | Each piece type = separate strategy class |
| **Tic-Tac-Toe** *(folder exists)* | Strategy (win condition), Command (move) | Clean FSM: PLAYING → WON/DRAW |
| **Coffee Machine** *(folder exists)* | State, Factory, Builder | State: Idle→Brewing→Dispensing. Builder for recipe |
| **Snake and Ladder** | Command (dice roll + move), Observer (event triggers) | Board is Composite; snakes/ladders are effects |
| **Library Management** | Strategy (search), Observer (fine notification) | Classic CRUD-heavy OOP problem |
| **Logging Framework** | Singleton (logger), Chain of Responsibility (log levels), Observer (appenders) | Build a mini-Log4j: `Logger → Handler → Formatter → Appender` |
| **Car Rental** *(folder exists)* | Strategy (pricing), Factory (vehicle), State (AVAILABLE/RENTED/UNDER_MAINTENANCE) | Focus on reservation + return lifecycle |

---

## 📅 Recommended Day-wise Execution (Day 4–9)

| Day | Problem | Focus Area | Time |
|---|---|---|---|
| **Day 4** | Parking Lot | OOP fundamentals, Factory, Strategy, basic concurrency | 75 mins design + code |
| **Day 5** | Movie Ticket Booking | Concurrency-heavy, Command, Observer, seat-locking strategies | 90 mins design + code |
| **Day 6** | LRU/LFU Cache | Data structure inside OOP, Strategy for eviction, ReadWriteLock | 60 mins design + code |
| **Day 7** | Elevator System | State machine, Strategy (scheduler), multi-elevator dispatch | 75 mins design + code |
| **Day 8** | Splitwise + Rate Limiter | Strategy deep dive, graph-based settle-up algo, AtomicInteger | 45 min each |
| **Day 9** | Notification System OR Ride Sharing | Observer + Chain, OR State + Strategy — pick based on target company | 75 mins design + code |

> **Day 9 Choice Guide**:  
> → Targeting **Swiggy/Uber/Meesho** → Do **Ride Sharing**  
> → Targeting **Razorpay/PhonePe/Flipkart** → Do **Notification System + Wallet**  
> → Targeting **Amazon/Microsoft** → Do **Library Management + Logging Framework**

---

## Day 10 → Articulation Drill (Speak Your Design)

> The biggest gap for experienced engineers is **saying it well**, not knowing it.

### Practice These Out Loud:

1. **"Walk me through your class hierarchy for Parking Lot"**
   - Don't dump all classes — explain the *reasoning*

2. **"Why did you use Strategy pattern here?"**
   - Lead with the problem: "Without it, I'd have a switch statement violating OCP..."

3. **"What would change if we added multi-currency?"**
   - Show you designed for change, not just today's requirement

4. **"How would you make this thread-safe?"**
   - Identify the specific shared mutable state, then propose the minimal lock scope

---

## Day 11–14 → Timed Mock Rounds

> Simulate real interview conditions.

### Mock Format (45 mins):
```
[0–5 min]   → Requirements gathering (ask questions)
[5–15 min]  → Entity modeling + relationships (talk aloud)
[15–35 min] → Core code — interfaces, key classes, patterns
[35–45 min] → Extensibility + concurrency discussion
```

### Problems to Mock (in order):
1. Design a **Library Management System** (new — untried)
2. Design a **Hotel Booking System** (new — untried)
3. Design a **Notification System** (Event-driven, Observer heavy)
4. Revisit **Parking Lot** under time pressure

> **Rule**: After each mock, write down one thing you explained well and one thing that was unclear.

---

# ⚠️ ANALYSIS: What to Change From Your Previous Approach

| Previous Approach | Revised Approach (Senior Level) |
|---|---|
| Learn patterns → watch videos → practice questions | ✅ You've already done the learning. Skip to practice faster. |
| Study all 23 GoF patterns deeply | Focus on 15 high-ROI patterns with code + senior angle |
| Practice questions after full revision | **Start Tier 1 questions NOW** (Day 4) — don't wait for perfection |
| Focus on class design only | Add concurrency discussion to every answer |
| Solo practice | Articulate out loud — interviews are verbal, not written |

---

# ❌ WHAT NOT TO DO (Senior-Specific Traps)

1. **Don't re-watch OOP/SOLID theory videos** — You know it. Time is better on questions.
2. **Don't memorize question solutions** — Interviewers change one requirement. Design the *thinking*, not the answer.
3. **Don't over-engineer** — Applying all 5 patterns to a Parking Lot signals you don't understand tradeoffs.
4. **Don't skip the "why"** — Saying "I used Strategy" is SDE1. Saying "I used Strategy because the pricing logic needs to vary independently of the billing flow, respecting OCP" is SDE2.
5. **Don't ignore the verbal component** — Practice narrating your design. Silence = uncertainty in interviews.

---

# 🧠 Senior-Level Design Mindset Checklist

Before finalizing any design, ask:

```
□ What is the single responsibility of each class?
□ Which parts of this design will change most often?
□ Am I depending on abstractions, not concretions?
□ Where is the shared mutable state? How do I protect it?
□ Can I add a new feature without modifying existing classes?
□ Would another engineer understand this without my explanation?
```

---

# 📊 Revised Timeline Summary

| Day | Focus | Output |
|---|---|---|
| Day 1 | Behavioral Patterns — Tier 1 (Strategy, Observer, State, Command, CoR) | Code + senior angle in `DESIGN_PATTERNS_REVISION.md` |
| Day 2 | Behavioral Patterns — Tier 2 (Template, Iterator, Mediator, Memento, Visitor) | Code + senior angle in `DESIGN_PATTERNS_REVISION.md` |
| Day 3 | Concurrency Crash Course | Notes in `/concurrency/CONCURRENCY_LLD.md` |
| Day 4 | LLD: Parking Lot (full design + code) | `/lld-ques/parking-lot/` |
| Day 5 | LLD: Vending Machine (refine existing) | `/lld-ques/vending-machine-state-design/` |
| Day 6 | LLD: Elevator System | `/lld-ques/elevator-design/` |
| Day 7 | LLD: Splitwise | `/lld-ques/splitwise/` |
| Day 8 | LLD: Payment Gateway | `/lld-ques/payment-gateway/` |
| Day 9 | LLD: Rate Limiter + ATM | `/lld-ques/rate-limiter/` + `/lld-ques/atm/` |
| Day 10 | Articulation Drill — Speak every design out loud | No new code — verbal only |
| Day 11–12 | Mock 1: Library System + Hotel Booking | Timed 45 min each |
| Day 13–14 | Mock 2: Notification System + Parking Lot re-attempt | Timed 45 min each |

---

# 💥 Final Insight (Upgraded for Senior Level)

The biggest jump from SDE1 → SDE2 in LLD happens when you stop saying:

> *"I created a Vehicle class with these fields..."*

And start saying:

> *"I modeled Vehicle as an abstract class rather than an interface because it carries shared state (registration number, owner). The specific type — Car, Truck, Bike — is determined at runtime via a Factory, which lets us add new vehicle types without touching the spot-assignment logic. This respects OCP."*

**That** is the real signal interviewers are looking for.
