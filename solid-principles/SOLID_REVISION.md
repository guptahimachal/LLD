# SOLID Principles — Interview Revision

---

## S — Single Responsibility Principle (SRP)

> **"A class should have only one reason to change."**

### ❌ Violation
```java
// User is doing too much: holds data + validates + saves
public class User {
    private String username, email;

    public boolean validate() {
        return email.contains("@") && username.length() > 0;
    }

    public void save() {
        System.out.println("Saving user to database: " + username);
    }
}
```
**Problem:** If DB logic changes → `User` changes. If validation logic changes → `User` changes. Two reasons to change.

### ✅ With SRP
```java
class User { /* only holds data */ }

class UserValidator {
    public boolean validate(User user) {
        return user.getEmail().contains("@") && user.getUsername().length() > 0;
    }
}

class UserRepository {
    public void save(User user) {
        System.out.println("Saving user to database: " + user.getUsername());
    }
}
```

### 🎤 What to say in interview
> "SRP means one class, one job. In my example, the `User` class was handling data, validation, and persistence — three different concerns, three different actors that could demand changes. I split it into `User` (data), `UserValidator` (business rules), and `UserRepository` (persistence). Now each class has exactly one reason to change, making it easier to test and maintain."

---

## O — Open/Closed Principle (OCP)

> **"Open for extension, closed for modification."**

### ❌ Violation
```java
class Invoice {
    void save() { /* save to DB */ }
    void saveToFile() { /* save to File — added by modifying Invoice */ }
    // Every new storage type = modifying this class
}
```

### ✅ With OCP
```java
interface InvoiceDao {
    void save(Invoice invoice);
}

class SaveInvoiceToDB implements InvoiceDao {
    public void save(Invoice invoice) { /* DB logic */ }
}

class SaveInvoiceToFile implements InvoiceDao {
    public void save(Invoice invoice) { /* File logic */ }
}
// New storage? Add a new class — don't touch existing ones.
```

### 🎤 What to say in interview
> "OCP says: add new features by writing new code, not by changing existing tested code. Previously `Invoice` had to be modified every time we added a storage option — risky. With the `InvoiceDao` interface, I can add `SaveInvoiceToCloud` tomorrow without touching a single existing class. This pairs well with the Strategy pattern."

---

## L — Liskov Substitution Principle (LSP)

> **"Subtypes must be substitutable for their base types without altering correctness."**

### ❌ Violation
```java
abstract class User {
    abstract Integer getScore();   // only meaningful for Customer
    abstract String getReport();   // only meaningful for Admin
}

class Customer extends User {
    Integer getScore() { return 0; }
    String getReport() { throw new RuntimeException("No access!"); } // ❌ breaks contract
}

class Admin extends User {
    Integer getScore() { return 0; } // Admin has no score — forced to implement
    String getReport() { return ""; }
}
```

### ✅ With LSP
```java
abstract class User { /* only common data */ }

interface Rewardable { Integer getScore(); }
interface Reportable  { String getReport(); }

class Customer extends User implements Rewardable {
    public Integer getScore() { return 0; }
}

class Admin extends User implements Reportable {
    public String getReport() { return ""; }
}
```

### 🎤 What to say in interview
> "LSP means a subclass shouldn't surprise you. `Customer` was forced to implement `getReport()` and just threw a `RuntimeException` — any code treating a `User` as having `getReport()` would blow up at runtime. The fix was to push only truly common behaviour into the base class and use interfaces for role-specific contracts. This also naturally leads into ISP."

---

## I — Interface Segregation Principle (ISP)

> **"No client should be forced to implement methods it doesn't use."**

### ❌ Violation
```java
interface NotificationTransformer {
    Boolean validate(String notification);
    String  transform(String notification);
}

// InternalNotificationTransformer doesn't need validation
class InternalNotificationTransformer implements NotificationTransformer {
    public Boolean validate(String n) { return null; } // ❌ forced stub
    public String  transform(String n) { return ""; }
}
```

### ✅ With ISP
```java
interface ValidateInterface   { Boolean validate(String notification); }
interface TransformerInterface { String  transform(String notification); }

// ClientNotificationTransformer needs both
class ClientNotificationTransformer implements ValidateInterface, TransformerInterface { ... }

// InternalNotificationTransformer only transforms — no forced validate
class InternalNotificationTransformer implements TransformerInterface { ... }
```

### 🎤 What to say in interview
> "ISP is about lean interfaces. The fat `NotificationTransformer` interface forced `InternalNotificationTransformer` to return `null` for `validate()` — a dead method lying around. I split it into `ValidateInterface` and `TransformerInterface`. Now each class implements only what it genuinely uses. Fat interfaces are a smell — they often hint at SRP or LSP violations too."

---

## D — Dependency Inversion Principle (DIP)

> **"High-level modules should not depend on low-level modules. Both should depend on abstractions."**

### ❌ Violation
```java
public class UserService {
    private Database database = new Database(); // ❌ hardwired to concrete class

    public void saveUser(String userName) {
        database.save(userName);
    }
}
```
**Problem:** Can't swap `Database` for a mock in tests, or switch to a different store without editing `UserService`.

### ✅ With DIP
```java
public interface UserRepository {
    void saveData(String data);
}

public class Database implements UserRepository {
    public void saveData(String data) { System.out.println("Saving to db " + data); }
}

public class UserService {
    private final UserRepository userRepository; // depends on abstraction

    public UserService(UserRepository userRepository) { // injected
        this.userRepository = userRepository;
    }

    public void saveUser(String userName) {
        userRepository.saveData(userName);
    }
}
```

### 🎤 What to say in interview
> "DIP is the backbone of testability and flexibility. `UserService` was newing up `Database` directly — completely rigid and untestable. I introduced a `UserRepository` interface and injected it via constructor. Now I can pass a `MockRepository` in tests, or switch to Redis/MongoDB without touching `UserService`. This is what Spring's DI container does under the hood."

---

## Quick Recap Table

| Principle | One-liner | Key signal of violation |
|-----------|-----------|------------------------|
| **SRP** | One class, one job | Class has multiple `// responsible for...` comments |
| **OCP** | Extend, don't modify | `if/else` or `switch` growing per new type |
| **LSP** | Subtypes honour the contract | Subclass throws UnsupportedOperation or returns null for parent method |
| **ISP** | Lean interfaces | Classes implementing methods with empty body or throwing stubs |
| **DIP** | Depend on abstractions | `new ConcreteClass()` inside a high-level service |

---

## 🔗 How the Principles Connect — The Chain Explained

> This is what separates a senior answer from a textbook recitation.  
> SOLID principles are not independent rules — **fixing one violation naturally leads you to apply the next.**

---

### The Chain: LSP violation → fat base class → ISP → DIP

Let's trace one evolving example from broken to fully SOLID.

---

#### Step 1 — You notice an LSP violation

You have a `User` base class with two abstract methods:

```java
abstract class User {
    abstract Integer getScore();   // for loyalty points
    abstract String  getReport();  // for audit reports
}
```

You create two subtypes:

```java
class Customer extends User {
    Integer getScore()  { return 42; }
    String  getReport() { throw new RuntimeException("Customers can't generate reports!"); } // ❌
}

class Admin extends User {
    Integer getScore()  { return 0; }   // meaningless — admins have no score
    String  getReport() { return "Admin report..."; }
}
```

**LSP is broken** — you can't safely use a `User` reference and call `getReport()` because `Customer` will blow up. The base class promised something its subtypes can't honour.

---

#### Step 2 — Ask WHY LSP broke → "The base class is too fat"

The root cause: `User` has two **unrelated responsibilities** crammed in — reward tracking and reporting.  
Not every `User` subtype can meaningfully implement both.  
This is the signal to apply **ISP**.

---

#### Step 3 — ISP fixes the fat base class

Split the fat contract into lean interfaces:

```java
abstract class User { /* only common identity data */ }

interface Rewardable { Integer getScore(); }   // only for Customer
interface Reportable  { String  getReport(); } // only for Admin
```

Now subtypes implement **only what they actually do**:

```java
class Customer extends User implements Rewardable {
    public Integer getScore() { return 42; } // ✅ no forced getReport()
}

class Admin extends User implements Reportable {
    public String getReport() { return "Admin report..."; } // ✅ no forced getScore()
}
```

**LSP is now automatically satisfied** — because we never promised `Customer` can generate reports.  
There's no contract to break.

---

#### Step 4 — ISP's clean interfaces make DIP trivial

Now that you have lean interfaces (`Rewardable`, `Reportable`), high-level services can depend on **only the abstraction they need**, not a concrete class:

```java
// Without DIP (before) — service is hardwired to Customer
class RewardService {
    private Customer customer = new Customer(); // ❌ concrete dependency
    void givePoints() { customer.getScore(); }
}
```

```java
// With DIP (after) — service depends on the ISP-produced interface
class RewardService {
    private final Rewardable rewardable; // ✅ depends on abstraction

    RewardService(Rewardable rewardable) { // inject anything: Customer, MockCustomer, PremiumCustomer
        this.rewardable = rewardable;
    }

    void givePoints() {
        int score = rewardable.getScore();
        System.out.println("Points: " + score);
    }
}
```

**Why was this easy?** Because ISP gave us a focused, meaningful interface (`Rewardable`) to depend on.  
A fat interface like the original `User` would have been a terrible thing to inject — it carries irrelevant baggage.

---

### The Full Chain at a Glance

```
LSP broken
  └─► Base class is too fat (not all subtypes can honour all methods)
        └─► Apply ISP: split into lean role-based interfaces
              └─► Clean interfaces are perfect targets for DIP injection
                    └─► High-level modules become testable and swappable
```

---

### How All 5 Connect

| Trigger | Leads to |
|---|---|
| Class with too many methods / reasons to change | **SRP** — split it |
| Adding a new type requires editing existing class | **OCP** — use abstraction + extension |
| Subtype breaks base class contract | **LSP** — don't overpromise in base |
| LSP fix reveals fat interface | **ISP** — split the interface |
| ISP creates clean interfaces | **DIP** — now inject those abstractions |

> **The pattern:** SRP keeps classes small → OCP keeps them stable → LSP keeps inheritance safe → ISP keeps interfaces lean → DIP makes everything pluggable and testable.

---

### 🎤 How to say this in an interview

> *"The principles aren't a checklist — they cascade. In my `User` example, the LSP violation existed because the base class was doing too much, which is an SRP problem at the abstraction level. Fixing it with ISP — splitting into `Rewardable` and `Reportable` — gave me focused interfaces. Those focused interfaces are exactly what DIP needs: something meaningful to inject. Now my `RewardService` depends on `Rewardable`, not on a concrete `Customer`, so I can test it with a mock, swap it for a `PremiumCustomer`, or change the reward logic without touching the service. That's the whole point of SOLID — every principle reinforces the others."*

