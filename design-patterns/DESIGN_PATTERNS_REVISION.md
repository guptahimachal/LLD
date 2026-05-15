# Design Patterns — Interview Revision

---

## 🛠️ Creational Patterns
*Focus on how objects are created. They abstract the instantiation process, making systems independent of how their objects are created, composed, and represented.*

### 1. Singleton
**Definition**: Ensures a class has only one instance and provides a global point of access to it.
> **🎤 Senior Interview Angle**: Often an anti-pattern if misused (global state). Emphasize thread safety (e.g., Double-Checked Locking or using Enums in Java) and its use for shared resources like connection pools or configuration managers.

**Example (Double-Checked Locking)**:
```java
public class DBConnectionDLocking {
    // volatile ensures changes to instance are immediately visible to other threads
    private static volatile DBConnectionDLocking instance;
    
    private DBConnectionDLocking() {} // Private constructor prevents direct instantiation

    public static DBConnectionDLocking getInstance() {
        if (instance == null) { // 1st check (no locking overhead if already initialized)
            synchronized (DBConnectionDLocking.class) {
                if (instance == null) { // 2nd check (thread-safe initialization)
                    instance = new DBConnectionDLocking();
                }
            }
        }
        return instance;
    }
}

class Main {
    public static void main(String[] args) {
        // Caller code
        DBConnectionDLocking conn1 = DBConnectionDLocking.getInstance();
        DBConnectionDLocking conn2 = DBConnectionDLocking.getInstance();
        System.out.println(conn1 == conn2); // true
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Database connections are expensive to open and maintain; creating a new pool or connection manager for every client request would crash the database. The Singleton pattern ensures all parts of the application share exactly one connection pool. Furthermore, the private constructor guarantees no external instantiation, while `getInstance()` provides the sole global access point. From a senior perspective, emphasizing `volatile` and double-checked locking demonstrates a deep understanding of the Java Memory Model, ensuring thread safety without the heavy performance penalty of synchronizing the entire method.
>
> **🌍 Real-World Example**: 
> - **Java Core**: `java.lang.Runtime.getRuntime()` or `java.awt.Desktop.getDesktop()`.
> - **Spring Framework**: Spring Beans are singletons by default (`@Scope("singleton")`), managed centrally by the `ApplicationContext`.

### 2. Factory Method
**Definition**: Defines an interface for creating an object, but lets subclasses decide which class to instantiate.
> **🎤 Senior Interview Angle**: Defers instantiation to subclasses. Great for loose coupling when you don't know the exact concrete types your code will work with ahead of time. 

**Example (Polymorphic Factory Method)**:
```java
// Product Interface & Concrete Product
public interface Report { void generate(); }
public class PdfReport implements Report {
    public void generate() { System.out.println("Generating PDF..."); }
}

// Creator Interface (The true Factory Method)
public abstract class ReportCreator {
    public abstract Report createReport(); // Factory Method
    
    public void render() {
        Report report = createReport(); // Relies on abstraction
        report.generate();
    }
}

// Concrete Creator
public class PdfReportCreator extends ReportCreator {
    @Override
    public Report createReport() {
        return new PdfReport();
    }
}

class Main {
    public static void main(String[] args) {
        // Caller code
        ReportCreator creator = new PdfReportCreator();
        creator.render(); // Outputs: Generating PDF...
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** The rendering framework (`ReportCreator.render()`) knows *when* and *how* to generate a report, but shouldn't be hardcoded to know *which* specific report (PDF, Excel, HTML) to instantiate. Factory Method is needed to defer this instantiation logic to subclasses, allowing new report types to be added without modifying the core rendering logic. The `ReportCreator` delegates the actual creation of the `Report` to its subclasses (`PdfReportCreator`). For a senior interview, showing that you understand the difference between a basic "Simple Factory" (a massive `switch` statement that violates the Open/Closed Principle) and the true "Factory Method" pattern (polymorphic creators) is a massive green flag. It proves you understand dependency inversion.
>
> **🌍 Real-World Example**: 
> - **Java Core (`Iterable.iterator()`)**: This is the perfect polymorphic Factory Method! `Iterable` is the abstract Creator. `ArrayList` and `HashSet` are concrete creators. When you call `.iterator()`, `ArrayList` returns an `ArrayList.Itr` product, while `HashSet` returns a `HashMap.KeyIterator`. The caller only ever interacts with the `Iterator` interface, completely decoupled from the specific collection's traversal logic.
> - **Spring Framework (`CacheManager`)**: The `CacheManager` interface has the factory method `getCache(String name)`. Depending on the concrete creator you configure (`RedisCacheManager` vs `ConcurrentMapCacheManager`), it instantiates and returns entirely different `Cache` products (`RedisCache` vs `ConcurrentMapCache`), all without the caller changing a single line of code.

### 3. Abstract Factory
**Definition**: Provides an interface for creating families of related or dependent objects without specifying their concrete classes.
> **🎤 Senior Interview Angle**: Think of it as a "factory of factories." Essential when your system needs to support multiple families of products (like different UI themes or database drivers) and you want to ensure the products are compatible.

**Example (Cross-Platform GUI Factory)**:
```java
// Abstract Products & Concrete Products
public interface Button { void paint(); }
public interface Checkbox { void paint(); }
public class WindowsButton implements Button {
    public void paint() { System.out.println("Windows Button"); }
}
public class WindowsCheckbox implements Checkbox {
    public void paint() { System.out.println("Windows Checkbox"); }
}

// Abstract Factory
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Concrete Factory
public class WindowsFactory implements GUIFactory {
    public Button createButton() { return new WindowsButton(); }
    public Checkbox createCheckbox() { return new WindowsCheckbox(); }
}

class Application {
    private Button button;
    public Application(GUIFactory factory) {
        this.button = factory.createButton(); // Client only knows interfaces
    }
    public void render() { button.paint(); }
}

class Main {
    public static void main(String[] args) {
        // Caller code
        GUIFactory factory = new WindowsFactory();
        Application app = new Application(factory);
        app.render(); // Outputs: Windows Button
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** If we created buttons and checkboxes independently using simple factories, a developer might accidentally mix a `WindowsButton` with a `MacCheckbox` on the same screen. Abstract Factory is needed here to enforce that these UI components are created as a cohesive, compatible family. The `GUIFactory` defines a contract for creating a *family* of related objects. The senior angle highlights that the client (`Application`) only depends on interfaces, preventing the accidental mixing of incompatible products (e.g., a Windows button on a Mac UI). It strictly adheres to the Open/Closed and Dependency Inversion principles.
>
> **🌍 Real-World Example**: 
> - **Java JDBC (`java.sql.Connection`)**: This is the ultimate Abstract Factory. A `Connection` acts as a factory that creates a *family* of related products: `Statement`, `PreparedStatement`, and `CallableStatement`. If you connect to MySQL, the MySQL `Connection` creates `MySQLStatement` and `MySQLPreparedStatement`. If you use PostgreSQL, it creates Postgres-specific versions. The client just uses the interfaces, ensuring you never accidentally mix a MySQL Statement with a Postgres Connection.
> - **Jackson Library (`JsonFactory`)**: In Jackson, the `JsonFactory` creates a family of streaming products: `JsonParser` (for reading) and `JsonGenerator` (for writing). If you swap the `JsonFactory` for a `YAMLFactory` (from Jackson's dataformat library), it produces a `YAMLParser` and `YAMLGenerator` instead. The client code reading/writing the data remains exactly the same, but the entire data-format family switches seamlessly.

### 4. Builder
**Definition**: Separates the construction of a complex object from its representation, allowing the same construction process to create various representations.
> **🎤 Senior Interview Angle**: The ultimate solution for the "telescoping constructor" problem (too many constructor arguments). Often implemented with fluent interfaces (method chaining) to make object creation readable and immutable.

**Example (Immutable House Builder)**:
```java
public class House {
    private final String roof; // final ensures immutability
    private final String wall;
    
    // Private constructor forces usage of the Builder
    private House(Builder builder) {
        this.roof = builder.roof;
        this.wall = builder.wall;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String roof;
        private String wall;
        
        public Builder setRoof(String roof) { this.roof = roof; return this; }
        public Builder setWall(String wall) { this.wall = wall; return this; }
        
        public House build() { 
            // Can validate state here before creation
            return new House(this); 
        }
    }
}

class Main {
    public static void main(String[] args) {
        // Caller code using fluent interface
        House myHouse = House.builder()
                             .setRoof("Gable")
                             .setWall("Brick")
                             .build();
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** A `House` object might have dozens of optional configuration parameters. Using a constructor would lead to an unreadable "telescoping constructor" anti-pattern (e.g., `new House("Gable", "Brick", 2, 4, true, false)`). The Builder is needed to provide a readable, step-by-step initialization process while keeping the final object immutable. The construction logic is completely isolated into the `Builder` class, allowing step-by-step initialization. The true senior angle here emphasizes **immutability**. Because the fields of `House` are `final` and there are no setters, the resulting object is perfectly thread-safe. A senior engineer knows the Builder isn't just for clean syntax—it's for safely constructing immutable complex objects.
>
> **🌍 Real-World Example**: 
> - **Java Core**: `java.lang.StringBuilder` (for mutable strings) or Java 11's `HttpClient.newBuilder()`.
> - **Libraries / Spring**: Lombok's `@Builder` annotation, or Spring's `UriComponentsBuilder` for safely building complex URLs.

### 5. Prototype
**Definition**: Specifies the kinds of objects to create using a prototypical instance, and creates new objects by copying (cloning) this prototype.
> **🎤 Senior Interview Angle**: Used heavily when creating an object from scratch is computationally expensive (e.g., requires database calls or network requests). You just clone an existing cached instance.

**Example (Caching Heavy Config)**:
```java
public class DatabaseConfig implements Cloneable {
    private String host;
    private String expensiveMetadata; 

    public DatabaseConfig(String host) {
        this.host = host;
        this.expensiveMetadata = fetchFromNetwork(); // Heavy operation
    }

    @Override
    public DatabaseConfig clone() {
        try {
            return (DatabaseConfig) super.clone(); // Shallow clone
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    private String fetchFromNetwork() { return "Heavy Data..."; }
    public void setHost(String host) { this.host = host; }
}

class Main {
    public static void main(String[] args) {
        // Initial creation is expensive
        DatabaseConfig cachedConfig = new DatabaseConfig("localhost"); 
        
        // Caller code: Cloning is instant, bypassing the heavy network call
        DatabaseConfig prodConfig = cachedConfig.clone();
        prodConfig.setHost("prod-db.internal");
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Fetching the `expensiveMetadata` from a remote network or disk for every single config instantiation would cripple application performance. Prototype is needed to perform this heavy lifting exactly once, caching the result, and then instantly cloning that fully-hydrated object for all subsequent requests. The pattern is demonstrated by creating new `DatabaseConfig` instances via `clone()` rather than `new`. The senior angle highlights performance optimization. In a high-throughput backend, avoiding redundant I/O operations by cloning an already fully-hydrated object is a key optimization technique. It also opens the floor to discuss deep vs. shallow copying.
>
> **🌍 Real-World Example**: 
> - **Java Core**: The `java.lang.Object.clone()` method itself, and classes implementing `java.lang.Cloneable`.
> - **Java Collections**: Using `ArrayList.clone()` or `HashMap.clone()` to perform a shallow copy of an existing collection.

---

## 🏗️ Structural Patterns
*Focus on how classes and objects are composed to form larger structures. They simplify design by identifying a simple way to realize relationships among entities.*

### 1. Adapter
**Definition**: Converts the interface of a class into another interface the clients expect. Lets classes work together that couldn't otherwise because of incompatible interfaces.
> **🎤 Senior Interview Angle**: The classic "wrapper." Crucial for integrating legacy code or third-party libraries into your system without altering your core application logic.

**Example (Third-Party OAuth Integration)**:
```java
// Target Interface (What our internal system expects)
public interface InternalUser {
    String getEmail();
    String getFullName();
}

// Our core system method that we cannot change
public class AuthenticationService {
    public void login(InternalUser user) {
        System.out.println("Logging in user: " + user.getFullName());
    }
}

// Adaptee (Third-party object we receive, incompatible interface)
public class GoogleProfile {
    private String googleEmail;
    private String displayName;

    public GoogleProfile(String googleEmail, String displayName) {
        this.googleEmail = googleEmail;
        this.displayName = displayName;
    }
    
    public String getGoogleEmail() { return googleEmail; }
    public String getDisplayName() { return displayName; }
}

// Adapter (Wraps the third-party object to make it compatible)
public class GoogleProfileAdapter implements InternalUser {
    private GoogleProfile googleProfile; // Wraps the incompatible object

    public GoogleProfileAdapter(GoogleProfile googleProfile) {
        this.googleProfile = googleProfile;
    }

    @Override
    public String getEmail() {
        return googleProfile.getGoogleEmail(); // Translates the method call
    }

    @Override
    public String getFullName() {
        return googleProfile.getDisplayName(); // Translates the method call
    }
}

class Main {
    public static void main(String[] args) {
        AuthenticationService authService = new AuthenticationService();
        GoogleProfile googleUser = new GoogleProfile("john@gmail.com", "John Doe");
        
        // authService.login(googleUser); // COMPILER ERROR: Incompatible types!
        
        // The adapter wraps the incompatible object to make it compatible
        InternalUser adaptedUser = new GoogleProfileAdapter(googleUser);
        authService.login(adaptedUser); // Outputs: Logging in user: John Doe
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Our core `AuthenticationService` has a strict dependency on the `InternalUser` interface. When we integrate "Sign in with Google", we receive a `GoogleProfile` object which the compiler rejects. Adapter is needed here to wrap the incompatible `GoogleProfile` object and translate its methods (`getGoogleEmail` -> `getEmail`), allowing the two previously incompatible classes to collaborate without altering either of their source codes. In a senior interview, distinguishing between an *Object Adapter* (using composition, like wrapping the `GoogleProfile` instance) and a *Class Adapter* (using multiple inheritance, which Java doesn't support) shows a deep understanding of the pattern's mechanics as defined by the Gang of Four.
>
> **🌍 Real-World Example**: 
> - **Java Core**: `java.util.Arrays.asList(array)` acts as an adapter, taking an incompatible Array object and wrapping it to make it compatible with methods that expect a `List` interface.
> - **Spring**: `HandlerAdapter` in Spring MVC adapts different types of incoming controller objects to the interface expected by the `DispatcherServlet`.

### 2. Bridge
**Definition**: Decouples an abstraction from its implementation so that the two can vary independently.
> **🎤 Senior Interview Angle**: Solves the "Cartesian product" class explosion problem. When a class has two orthogonal dimensions of variation, Bridge separates them into two independent hierarchies connected by composition instead of inheritance.

**Example (Payment Types and Gateways)**:
```java
// ==========================================
// 1. The Implementation Hierarchy (The "Gateway" layer)
// ==========================================
public interface PaymentGateway {
    void processPayment(String paymentType, double amount);
}

public class StripeGateway implements PaymentGateway {
    public void processPayment(String paymentType, double amount) {
        System.out.println("Stripe: Processing " + paymentType + " of $" + amount);
    }
}

public class PayPalGateway implements PaymentGateway {
    public void processPayment(String paymentType, double amount) {
        System.out.println("PayPal: Processing " + paymentType + " of $" + amount);
    }
}

// ==========================================
// 2. The Abstraction Hierarchy (The "Payment Type" layer)
// ==========================================
public abstract class Payment {
    protected PaymentGateway gateway; // The "Bridge" connecting the two hierarchies

    public Payment(PaymentGateway gateway) { this.gateway = gateway; }
    
    public abstract void execute(double amount);
}

// We can extend the Payment Types independently of the Gateways!
public class StandardPayment extends Payment {
    public StandardPayment(PaymentGateway gateway) { super(gateway); }

    @Override
    public void execute(double amount) {
        gateway.processPayment("Standard Payment", amount);
    }
}

public class SubscriptionPayment extends Payment {
    public SubscriptionPayment(PaymentGateway gateway) { super(gateway); }

    @Override
    public void execute(double amount) {
        System.out.println("Validating recurring subscription...");
        gateway.processPayment("Subscription", amount);
    }
}

class Main {
    public static void main(String[] args) {
        PaymentGateway stripe = new StripeGateway();
        
        // Client mixes and matches independently
        Payment sub = new SubscriptionPayment(stripe);
        sub.execute(15.99); 
        // Outputs: 
        // Validating recurring subscription... 
        // Stripe: Processing Subscription of $15.99
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** As Refactoring Guru explains, the Bridge pattern solves the problem of trying to extend a class hierarchy in two independent dimensions. Here, we are extending payments by "Payment Type" (Standard vs Subscription) and by "Payment Gateway" (Stripe vs PayPal). If we used pure inheritance, adding 2 payment types and 2 gateways would require 4 distinct classes (e.g., `StripeStandardPayment`, `PayPalSubscriptionPayment`). Bridge is needed to switch from inheritance to object composition. We split the large, tangled class hierarchy into two separate, independent hierarchies (Abstraction and Implementation). This keeps the class count linear ($N+M$) rather than exponential ($N \times M$), allowing you to add a new gateway without touching the payment logic, and vice versa.
>
> **🌍 Real-World Example**: 
> - **Java JDBC**: The `java.sql.DriverManager` and `Connection` act as the abstraction, while the specific database drivers (MySQL, PostgreSQL) act as the implementations. You can change the DB without changing your SQL execution logic.
> - **SLF4J**: The logging facade (Abstraction) bridges to different underlying logging frameworks like Logback or Log4j (Implementation).

### 3. Composite
**Definition**: Lets you compose objects into tree structures to represent part-whole hierarchies, and then work with these structures as if they were individual objects.
> **🎤 Senior Interview Angle**: As Refactoring Guru notes, this pattern only makes sense when the core model of your app can be represented as a tree. The greatest benefit is that clients don't need to care about the concrete classes of the objects; they treat both simple leaves and complex branches identically via a common interface.

**Example (File System Tree)**:
```java
// Component
public interface FileSystem { void ls(); }

// Leaf
public class File implements FileSystem {
    private String fileName;
    public File(String fileName) { this.fileName = fileName; }
    
    @Override
    public void ls() { System.out.println("File name is " + fileName); }
}

// Composite
public class Folder implements FileSystem {
    private String folderName;
    private List<FileSystem> childList;

    public Folder(String folderName) {
        this.folderName = folderName;
        this.childList = new ArrayList<>();
    }

    public void addChild(FileSystem fileSystem) { childList.add(fileSystem); }

    @Override
    public void ls() {
        System.out.println("Folder name is " + folderName);
        for (FileSystem child : childList) {
            child.ls(); // Uniformly treats both Files and Folders
        }
    }
}

class Main {
    public static void main(String[] args) {
        Folder baseFolder = new Folder("Movies");
        baseFolder.addChild(new File("Gadar"));

        Folder comedyFolder = new Folder("Comedy-Folder");
        comedyFolder.addChild(new File("Hungama"));
        comedyFolder.addChild(new File("Dhol"));

        baseFolder.addChild(comedyFolder);
        baseFolder.ls(); // Recursively prints everything
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** As Refactoring Guru highlights, without the Composite pattern, the client code running `ls()` would have to explicitly check the concrete classes (`if (node instanceof Folder)`), manually unpack boxes (folders), and iterate through them. This makes client code bloated and tightly coupled to concrete classes. Composite is needed here to force both Leaves (`File`) and Composites (`Folder`) to share the common `FileSystem` interface. The greatest benefit is that the client doesn't need to care whether it's dealing with a simple file or a nested folder containing thousands of items. You simply call `ls()` on the root, and the objects themselves recursively pass the request down the tree.
>
> **🌍 Real-World Example**: 
> - **Java AWT/Swing**: A `Container` is a `Component` that can hold other `Component`s.
> - **DOM Parsing**: An XML/HTML document is a tree where an Element can contain text nodes or child Elements.

### 4. Decorator
**Definition**: Attaches additional responsibilities to an object dynamically at runtime. Provides a flexible alternative to subclassing for extending functionality.
> **🎤 Senior Interview Angle**: Prioritizes composition over inheritance. Used extensively to add behaviors like caching, logging, or authorization to an object without modifying its underlying class.

**Example (Dynamic Text Formatting)**:
```java
// Component
public interface TextComponent { String getText(); }

// Concrete Component
public class PlainTextComponent implements TextComponent {
    private String text;
    public PlainTextComponent(String text) { this.text = text; }
    @Override public String getText() { return text; }
}

// Base Decorator
public abstract class TextDecorator implements TextComponent {
    protected TextComponent textComponent;
    public TextDecorator(TextComponent textComponent) { this.textComponent = textComponent; }
    @Override public String getText() { return textComponent.getText(); }
}

// Concrete Decorator
public class BoldTextDecorator extends TextDecorator {
    public BoldTextDecorator(TextComponent textComponent) { super(textComponent); }
    @Override public String getText() { return "<b>" + super.getText() + "</b>"; }
}

// Concrete Decorator
public class ItalicTextDecorator extends TextDecorator {
    public ItalicTextDecorator(TextComponent textComponent) { super(textComponent); }
    @Override public String getText() { return "<i>" + super.getText() + "</i>"; }
}

class Main {
    public static void main(String[] args) {
        TextComponent plain = new PlainTextComponent("Hello");
        
        // Stacking decorators dynamically at runtime
        TextComponent boldItalic = new BoldTextDecorator(new ItalicTextDecorator(plain));
        
        System.out.println(boldItalic.getText()); // Outputs: <b><i>Hello</i></b>
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Text formatting combinations are dynamic and infinite (Bold, Italic, Bold+Italic, Bold+Underline, etc.). Using inheritance to cover every combination would result in an unmaintainable class explosion. Decorator is needed to compose these formatting behaviors dynamically at runtime by wrapping objects inside other objects. Instead of creating a massive class hierarchy like `BoldItalicPlainTextComponent`, functionality is layered on at runtime like Russian nesting dolls. In an interview, highlight how this prevents "class explosion."
>
> **🌍 Real-World Example**: 
> - **Java I/O**: The quintessential example! `BufferedReader(new InputStreamReader(new FileInputStream("file.txt")))`.
> - **Spring**: `@Cacheable` or `@Transactional` annotations use proxies to decorate your beans with caching/transactional behavior at runtime.

### 5. Facade
**Definition**: Provides a unified, higher-level interface to a set of interfaces in a subsystem that makes the subsystem easier to use.
> **🎤 Senior Interview Angle**: Hides complexity. In modern backend architectures, an API Gateway acts as a Facade over a complex web of internal microservices. 

**Example (E-Commerce Order Processing)**:
```java
// Complex Subsystems
class InventoryService { public boolean check(String itemId) { return true; } }
class PaymentService { public boolean charge(String card) { return true; } }
class ShippingService { public void ship(String itemId) { System.out.println("Shipped"); } }

// Facade
public class OrderFacade {
    private InventoryService inventory = new InventoryService();
    private PaymentService payment = new PaymentService();
    private ShippingService shipping = new ShippingService();

    public boolean placeOrder(String itemId, String cardInfo) {
        // Hiding the complex choreography from the client
        if (inventory.check(itemId)) {
            if (payment.charge(cardInfo)) {
                shipping.ship(itemId);
                return true;
            }
        }
        return false;
    }
}

class Main {
    public static void main(String[] args) {
        OrderFacade facade = new OrderFacade();
        facade.placeOrder("ITEM_123", "4111-1111-1111-1111"); // Client code is radically simplified
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Placing an order requires complex orchestration across Inventory, Payment, and Shipping services. If the client UI had to manage this directly, it would become tightly coupled to the backend architecture. Facade is needed to provide a single, unified entry point (`placeOrder`), hiding the intricate choreography of the microservices. The `OrderFacade` encapsulates the interactions with multiple domain services. A senior engineer will stress that Facades don't "hide" the subsystems permanently (clients can still bypass the Facade if they need low-level access), but they provide a convenient "happy path" for 90% of use cases.
>
> **🌍 Real-World Example**: 
> - **Spring Boot**: `@SpringBootApplication` is essentially a facade for `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
> - **Microservices**: BFF (Backend-For-Frontend) and API Gateways serve as architectural Facades.

### 6. Flyweight
**Definition**: Flyweight is a structural design pattern that lets you fit more objects into the available amount of RAM by sharing common parts of state between multiple objects instead of keeping all of the data in each object.
> **🎤 Senior Interview Angle**: A pure memory optimization pattern. If you have millions of objects sharing identical data, extract the *intrinsic state* to a shared object to save RAM.

**Example (Text Editor Formatting Cache)**:
```java
// Intrinsic State — Immutable & Thread-Safe Flyweight
// All fields are private final: once constructed, this object CANNOT change.
// No setters exist. Multiple threads can safely read it concurrently.
public final class TextFormatting {          // 'final' class prevents subclasses breaking immutability
    private final String font;
    private final int size;
    private final String color;

    public TextFormatting(String font, int size, String color) {
        this.font = font;
        this.size = size;
        this.color = color;
    }

    // Only getters — NO setters
    public String getFont()  { return font;  }
    public int    getSize()  { return size;  }
    public String getColor() { return color; }
}

// Flyweight Factory — Thread-Safe Cache
public class FormattingFactory {
    // ConcurrentHashMap: thread-safe, lock-free reads, fine-grained write locks.
    // 'final' ensures the reference itself is never re-assigned.
    private static final Map<String, TextFormatting> cache = new ConcurrentHashMap<>();

    public static TextFormatting getFormatting(String font, int size, String color) {
        String key = font + "_" + size + "_" + color;
        // computeIfAbsent on ConcurrentHashMap is atomic — safe under concurrent access
        return cache.computeIfAbsent(key, k -> new TextFormatting(font, size, color));
    }
}

// Extrinsic State (Unique per character — NOT shared)
public class Character {
    private final char c;
    private final TextFormatting formatting; // Holds a reference to the shared, immutable flyweight

    public Character(char c, String font, int size, String color) {
        this.c = c;
        this.formatting = FormattingFactory.getFormatting(font, size, color);
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** A word processor might have millions of character objects on screen. Storing the font name, size, and color strings inside every single character would quickly exhaust the JVM heap memory. Flyweight is needed to extract this shared, unchanging formatting data (the intrinsic state) into a single shared object, reducing memory footprint by orders of magnitude. Imagine a document with 1 million characters. Instead of storing font strings and size integers 1 million times, `Character` just holds a reference to a cached `TextFormatting` object. A senior dev will mention the importance of making the intrinsic state (the Flyweight) completely immutable and thread-safe.
>
> **🌍 Real-World Example**: 
> - **Java Core**: `String` literal pool, `Integer.valueOf()` caching (for values -128 to 127).
> - **Connection Pools**: Reusing database connections instead of establishing new ones.

### 7. Proxy
**Definition**: Proxy is a structural design pattern that lets you provide a substitute or placeholder for another object. A proxy controls access to the original object, allowing you to perform something either *before or after* the request gets through to the original object.

> **🎤 Senior Interview Angle**: As Refactoring Guru defines it, the key insight is **controlled access** — the Proxy sits between the client and the real service, intercepts calls, and can add behaviour without the client (or the real service) ever knowing. There are several flavours:
> - **Virtual Proxy** — Lazy initialization; defer creating a heavy object until it's actually needed.
> - **Protection Proxy** — Access control; let only authorized clients reach the real service.
> - **Remote Proxy** — Handles all the network plumbing for a service that lives on a different machine (e.g., gRPC stubs).
> - **Caching Proxy** — Caches expensive results and returns them on repeated calls.
> - **Logging Proxy** — Logs every request transparently before delegating.
>
> The critical structural rule (per Refactoring Guru): the Proxy **must implement the exact same interface** as the real service, so it is completely transparent to — and interchangeable with — the real subject for any client.

**Example (Protection Proxy — Role-Based Report Access)**:

*Scenario*: A `ReportService` generates financial reports. It should only be accessible to users with the `ADMIN` role. We don't want to pollute `ReportService` with auth logic, so we wrap it with a `SecuredReportProxy`.

```java
// Service Interface — Proxy and Real Subject both implement this
public interface ReportService {
    String generateReport(String reportType);
}

// Real Subject — contains the actual business logic, knows nothing about auth
public class FinancialReportService implements ReportService {
    @Override
    public String generateReport(String reportType) {
        // Expensive: queries DB, aggregates data, etc.
        return "Financial Report [" + reportType + "]: revenue=$5M, costs=$3M";
    }
}

// Protection Proxy — intercepts calls and enforces access control
public class SecuredReportProxy implements ReportService {
    private final ReportService realService;  // delegates to this after auth check
    private final String userRole;

    public SecuredReportProxy(ReportService realService, String userRole) {
        this.realService = realService;
        this.userRole = userRole;
    }

    @Override
    public String generateReport(String reportType) {
        // --- Pre-processing: check authorization BEFORE delegating ---
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            throw new SecurityException(
                "Access denied: role '" + userRole + "' cannot generate reports."
            );
        }
        System.out.println("[AUDIT LOG] Admin user requested report: " + reportType);

        // --- Delegate to the real service ---
        String result = realService.generateReport(reportType);

        // --- Post-processing: could encrypt, watermark, or log the result ---
        return result;
    }
}

class Main {
    public static void main(String[] args) {
        ReportService realService = new FinancialReportService();

        // Admin user — proxy delegates to real service
        ReportService adminProxy = new SecuredReportProxy(realService, "ADMIN");
        System.out.println(adminProxy.generateReport("Q1-2024"));
        // Output:
        // [AUDIT LOG] Admin user requested report: Q1-2024
        // Financial Report [Q1-2024]: revenue=$5M, costs=$3M

        // Regular user — proxy blocks access entirely
        ReportService userProxy = new SecuredReportProxy(realService, "USER");
        userProxy.generateReport("Q1-2024"); // throws SecurityException
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** The `FinancialReportService` should focus purely on report generation — embedding role checks inside it violates the Single Responsibility Principle. Proxy is needed here to intercept access at the boundary, adding auth and audit logging *around* the real service without touching it at all. This is exactly the Refactoring Guru problem statement: you need to run something before or after the primary logic of a class, but you can't or shouldn't change that class.
>
> The senior angle is recognizing **Proxy vs Decorator**: both wrap an object behind the same interface, but their *intent* differs. A Decorator *enhances* the object's output (adds formatting, extra data). A Proxy *controls access* to the object itself (decides whether the call reaches the real subject at all). Proxy also typically manages the lifecycle of the real service; Decorator does not. This distinction is a common senior interview question.
>
> Also note: per Refactoring Guru, the Proxy differs from Facade — Facade simplifies a complex subsystem with a *different*, higher-level interface. Proxy always keeps the *same* interface, making it interchangeable with the real service.

> **🌍 Real-World Example**: 
> - **Spring AOP (`@Transactional`, `@PreAuthorize`)**: Spring wraps your service beans in dynamic proxies at startup. When you call a `@Transactional` method, you're hitting a proxy that opens a DB transaction *before* delegating to your real method and commits/rolls back *after* — a textbook Logging + Virtual Proxy combo.
> - **gRPC Stubs**: A generated gRPC client stub is a Remote Proxy. Your code calls methods on it as if the service is local; the stub handles all serialization, network transport, and error handling transparently.

---

## ⚙️ Behavioral Patterns
*Focus on algorithms and the assignment of responsibilities between objects. They describe not just patterns of objects or classes but also the patterns of communication between them.*

### 1. Strategy
**Definition**: Defines a family of algorithms, encapsulates each one, and makes them interchangeable. The algorithm can change independently of the client that uses it.
> **🎤 Senior Interview Angle**: Use this to kill a growing `if/else` or `switch` block. Instead of hardcoding "what to do" inside the class, you pass in the behaviour from outside. The class just calls `strategy.execute()` — it doesn't care which one it gets. Classic application of Open/Closed + Dependency Inversion.

**Example (E-Commerce Discount Engine)**:

*Scenario*: An `OrderService` needs to apply different discounts based on customer type — regular users pay full price, premium subscribers get 20% off, employees get 40% off. Without Strategy, there's a `switch(customerType)` inside `checkout()`. Every new tier means editing `OrderService` directly — bad.

```java
import java.util.List;

// 1. Strategy Interface — defines what every discount algorithm must do
public interface DiscountStrategy {
    double apply(double originalPrice);
}

// 2. Concrete Strategies — each algorithm lives in its own class
public class NoDiscount implements DiscountStrategy {
    @Override
    public double apply(double originalPrice) {
        return originalPrice; // full price, no change
    }
}

public class PremiumDiscount implements DiscountStrategy {
    private static final double RATE = 0.20;

    @Override
    public double apply(double originalPrice) {
        return originalPrice * (1 - RATE); // 20% off
    }
}

public class EmployeeDiscount implements DiscountStrategy {
    private static final double RATE = 0.40;

    @Override
    public double apply(double originalPrice) {
        return originalPrice * (1 - RATE); // 40% off
    }
}

// 3. Context — OrderService has NO idea which algorithm is running
//    It just calls discountStrategy.apply() and trusts the result
public class OrderService {
    private final DiscountStrategy discountStrategy;

    public OrderService(DiscountStrategy discountStrategy) {
        this.discountStrategy = discountStrategy;
    }

    public double checkout(List<Double> itemPrices) {
        double subtotal = itemPrices.stream().mapToDouble(Double::doubleValue).sum();
        double finalPrice = discountStrategy.apply(subtotal);
        System.out.printf("Subtotal: $%.2f  →  Final: $%.2f%n", subtotal, finalPrice);
        return finalPrice;
    }
}

// 4. Caller — picks the right strategy and injects it
class Main {
    public static void main(String[] args) {
        List<Double> cart = List.of(49.99, 19.99, 9.99);

        new OrderService(new NoDiscount()).checkout(cart);
        // Subtotal: $79.97  →  Final: $79.97

        new OrderService(new PremiumDiscount()).checkout(cart);
        // Subtotal: $79.97  →  Final: $63.98

        new OrderService(new EmployeeDiscount()).checkout(cart);
        // Subtotal: $79.97  →  Final: $47.98

        // Since DiscountStrategy is a functional interface, lambdas work too
        // Great for one-off flash sales or A/B test pricing
        DiscountStrategy flashSale = price -> price * 0.50;
        new OrderService(flashSale).checkout(cart);
        // Subtotal: $79.97  →  Final: $39.99
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Without Strategy, adding a new customer tier means opening `OrderService` and editing its `switch` block — every time, for every tier. That breaks OCP and turns `OrderService` into a class that changes for reasons unrelated to its core job (checkout). Strategy fixes this by moving each algorithm into its own class. `OrderService` never changes; you just add a new strategy class.
>
> **Senior talking points:**
> 1. **Strategy vs Polymorphism**: People confuse these. Polymorphism (`extends`) defines *what an object is*. Strategy defines *what an object does* — behaviour passed in as a dependency. They solve different problems.
> 2. **Lambdas as Strategies**: `DiscountStrategy` has one method, so Java treats it as a `@FunctionalInterface`. You can pass a lambda directly — no need for a full class for simple one-liners like flash sales.
> 3. **Composability**: You can wrap strategies inside other strategies (e.g., apply a coupon on top of the tier discount) without touching `OrderService` at all.

> **🌍 Real-World Example**:
> - **Java Core (`Comparator`)**: `Comparator` *is* a Strategy. `Collections.sort(list, comparator)` lets you swap the sorting algorithm at runtime without changing the sort call.
> - **Spring Security**: `SessionAuthenticationStrategy` is a Strategy interface. You configure which one Spring uses (e.g., `ChangeSessionIdAuthenticationStrategy`) — the auth filter just calls it, doesn't know the details.
> - **Jackson**: `JsonSerializer<T>` is a Strategy. You register one per type; Jackson calls it during serialization without caring what it does internally.

### 2. Observer
**Definition**: Defines a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically.
> **🎤 Senior Interview Angle**: This is the backbone of event-driven design. The subject (publisher) doesn't know — and shouldn't know — who is listening. Observers (subscribers) register themselves and get notified on state change. This keeps the subject completely decoupled from the consumers reacting to it. Real-world manifestations: Kafka topics, Spring's `ApplicationEvent`, RxJava observables.

**Example (Product Back-in-Stock Notification System)**:

*Scenario*: When a product comes back in stock, multiple systems need to react — send an email to waitlisted users, push a mobile notification, and update the analytics dashboard. Without Observer, `InventoryService` would directly call `EmailService`, `PushService`, and `AnalyticsService` — tightly coupling all of them together. Every new subscriber means editing `InventoryService`. That's wrong.

```java
import java.util.ArrayList;
import java.util.List;

// 1. Observer Interface — every subscriber implements this
public interface StockObserver {
    void onStockAvailable(String productId, int quantity);
}

// 2. Subject Interface — the publisher contract
public interface StockSubject {
    void subscribe(StockObserver observer);
    void unsubscribe(StockObserver observer);
    void notifyObservers(String productId, int quantity);
}

// 3. Concrete Subject — InventoryService manages stock and fires events
//    It knows NOTHING about who is listening or what they do with the event
public class InventoryService implements StockSubject {
    private final List<StockObserver> observers = new ArrayList<>();

    @Override
    public void subscribe(StockObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribe(StockObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String productId, int quantity) {
        for (StockObserver observer : observers) {
            observer.onStockAvailable(productId, quantity); // fan-out
        }
    }

    // Core business logic — restocking triggers the notification
    public void restock(String productId, int quantity) {
        System.out.println("[Inventory] Restocked: " + productId + " | qty: " + quantity);
        notifyObservers(productId, quantity);
    }
}

// 4. Concrete Observers — each reacts independently
public class EmailNotificationService implements StockObserver {
    @Override
    public void onStockAvailable(String productId, int quantity) {
        System.out.println("[Email] Sending back-in-stock email for: " + productId);
    }
}

public class PushNotificationService implements StockObserver {
    @Override
    public void onStockAvailable(String productId, int quantity) {
        System.out.println("[Push] Sending mobile alert for: " + productId);
    }
}

public class AnalyticsService implements StockObserver {
    @Override
    public void onStockAvailable(String productId, int quantity) {
        System.out.println("[Analytics] Logging restock event for: " + productId + " qty=" + quantity);
    }
}

// 5. Caller — wires everything up
class Main {
    public static void main(String[] args) {
        InventoryService inventory = new InventoryService();

        // Subscribers register themselves — InventoryService never hardcodes them
        inventory.subscribe(new EmailNotificationService());
        inventory.subscribe(new PushNotificationService());
        inventory.subscribe(new AnalyticsService());

        // One restock triggers all three independently
        inventory.restock("PRODUCT_XYZ", 50);
        // [Inventory] Restocked: PRODUCT_XYZ | qty: 50
        // [Email]     Sending back-in-stock email for: PRODUCT_XYZ
        // [Push]      Sending mobile alert for: PRODUCT_XYZ
        // [Analytics] Logging restock event for: PRODUCT_XYZ qty=50

        // Removing a subscriber at runtime — no code change needed anywhere
        // inventory.unsubscribe(pushService);
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** `InventoryService` owns stock data — that's its only job. Making it also know about emails, push notifications, and analytics violates SRP and creates tight coupling. If the analytics team adds a new dashboard, you'd have to edit `InventoryService`. With Observer, `InventoryService` just fires an event. Each subscriber owns its own reaction. Adding or removing a subscriber is zero-change to the publisher.
>
> **Senior talking points:**
> 1. **Push vs Pull model**: In the *push* model (used above), the subject sends data directly in the notification. In the *pull* model, the observer receives a reference to the subject and fetches what it needs. Pull gives observers more control but adds coupling back to the subject's interface.
> 2. **Memory leaks**: Classic Observer pitfall — if observers are never `unsubscribe()`d (e.g., in a long-lived subject), they stay in memory even after they're "done". This is a common bug in Java and Android. Always unsubscribe when the listener's lifecycle ends.
> 3. **Thread safety**: The `observers` list above is not thread-safe. In production, you'd use `CopyOnWriteArrayList` — safe for concurrent reads with infrequent writes, perfect for observer lists.
> 4. **Observer vs Pub/Sub (Kafka)**: Observer is in-process — subject calls observer directly. Pub/Sub (Kafka, RabbitMQ) is out-of-process — producer sends to a broker, consumers poll independently. Observer is synchronous and tightly timed; Pub/Sub is async and decoupled in time too.

> **🌍 Real-World Example**:
> - **Java Core (`java.util.EventListener`)**: Swing UI events use Observer. A `JButton` is the subject; your `ActionListener` is the observer. `button.addActionListener(...)` is just `subscribe()`.
> - **Spring (`ApplicationEvent` / `@EventListener`)**: Spring's event system is a built-in Observer framework. You publish an `ApplicationEvent` from anywhere; any `@EventListener` bean reacts — completely decoupled.
> - **RxJava / Project Reactor**: These are Observer on steroids — `Observable` (subject) emits items; `Observer` subscribes. Adds operators like `map`, `filter`, `debounce` to transform the event stream before it reaches the subscriber.

### 3. Command
**Definition**: Encapsulates a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.
> **🎤 Senior Interview Angle**: The key idea — turn a *call* into an *object*. Once a request is an object, you can store it, queue it, log it, retry it, or reverse it. This is what powers undo/redo, job queues, and audit logs. The sender has zero knowledge of who handles the request or how.

**Example (Text Editor — Cut / Copy / Paste with Undo)**:

*Scenario* (from Refactoring Guru): A text editor has toolbar buttons, context menus, and keyboard shortcuts all triggering the same operations (Copy, Cut, Paste). Without Command, every trigger (button, shortcut, menu) would call editor methods directly — duplicating logic everywhere. Adding undo would require tracking state in each caller. That's unmaintainable.

```java
import java.util.ArrayDeque;
import java.util.Deque;

// ── 1. Command Interface ──────────────────────────────────────────────────
// Returns true if the command changed editor state (should be pushed to history)
// Returns false if it didn't (e.g. Copy — no state change, no undo needed)
public interface Command {
    boolean execute();
}

// ── 2. Receiver — Editor ─────────────────────────────────────────────────
// The actual business logic lives here. Commands delegate to Editor methods.
// Editor has no knowledge of commands or the history stack.
public class Editor {
    public String text;       // full document text
    public String selection;  // currently selected text

    public String getSelection() {
        // In a real editor this would be the highlighted region
        return selection != null ? selection : "";
    }

    public void deleteSelection() {
        if (selection != null) {
            text = text.replace(selection, "");
            selection = null;
        }
    }

    public void replaceSelection(String clipboardText) {
        if (selection != null) {
            text = text.replace(selection, clipboardText);
        } else {
            text += clipboardText;
        }
        selection = null;
    }
}

// ── 3. Abstract Base Command — shared undo-via-snapshot logic ─────────────
// Commands only need app — they get the editor from app.activeEditor.
// This is cleaner: Application is the single context; no need to pass editor separately.
public abstract class BaseCommand implements Command {
    protected final Application app;
    private String backup; // snapshot of editor.text before this command ran

    protected BaseCommand(Application app) {
        this.app = app;
    }

    protected void saveBackup() {
        backup = app.activeEditor.text; // capture state before the change
    }

    public void undo() {
        app.activeEditor.text = backup; // restore state
    }
}

// ── 4. Concrete Commands ──────────────────────────────────────────────────

// Copy — reads selection into clipboard, does NOT change editor state
// → returns false so Application does NOT push it to history (no undo needed)
public class CopyCommand extends BaseCommand {
    public CopyCommand(Application app) { super(app); }

    @Override
    public boolean execute() {
        app.clipboard = app.activeEditor.getSelection();
        return false; // no state change → don't add to history
    }
}

// Cut — copies selection to clipboard AND deletes it from the editor
// → returns true so it IS pushed to history and can be undone
public class CutCommand extends BaseCommand {
    public CutCommand(Application app) { super(app); }

    @Override
    public boolean execute() {
        saveBackup();                                    // snapshot before mutation
        app.clipboard = app.activeEditor.getSelection();
        app.activeEditor.deleteSelection();
        return true; // state changed → push to history
    }
}

// Paste — inserts clipboard content at the current selection
public class PasteCommand extends BaseCommand {
    public PasteCommand(Application app) { super(app); }

    @Override
    public boolean execute() {
        saveBackup();
        app.activeEditor.replaceSelection(app.clipboard);
        return true;
    }
}

// Undo — delegates back to Application.undo(), keeping history management in one place
public class UndoCommand extends BaseCommand {
    public UndoCommand(Application app) { super(app); }

    @Override
    public boolean execute() {
        app.undo();
        return false; // undo itself is not tracked
    }
}

// ── 5. Invoker — CommandHistory ───────────────────────────────────────────
public class CommandHistory {
    private final Deque<BaseCommand> history = new ArrayDeque<>();

    public void push(BaseCommand command) { history.push(command); }

    public BaseCommand pop() {
        return history.isEmpty() ? null : history.pop();
    }
}

// ── 6. Application (the Client + Invoker) ────────────────────────────────
// Single context object — owns the editor, clipboard, and history.
// Commands only need a reference to this one object.
public class Application {
    public String clipboard = "";
    public Editor activeEditor = new Editor();
    private final CommandHistory history = new CommandHistory();

    public void executeCommand(BaseCommand command) {
        if (command.execute()) {   // run the command
            history.push(command); // only track if state changed
        }
    }

    public void undo() {
        BaseCommand command = history.pop();
        if (command != null) {
            command.undo();
        }
    }
}

// ── 7. Caller — simulates button clicks / keyboard shortcuts ──────────────
class Main {
    public static void main(String[] args) {
        Application app = new Application();

        // Setup — only touch app, not editor directly
        app.activeEditor.text = "Hello World";
        app.activeEditor.selection = "World";

        System.out.println("Before: " + app.activeEditor.text); // Hello World

        // Ctrl+C — copy (not tracked, no state change)
        app.executeCommand(new CopyCommand(app));

        // Ctrl+X — cut "World" (tracked, can be undone)
        app.executeCommand(new CutCommand(app));
        System.out.println("After cut:    " + app.activeEditor.text); // Hello

        // Ctrl+V — paste clipboard back
        app.executeCommand(new PasteCommand(app));
        System.out.println("After paste:  " + app.activeEditor.text); // Hello World

        // Ctrl+Z — undo the paste
        app.executeCommand(new UndoCommand(app));
        System.out.println("After undo:   " + app.activeEditor.text); // Hello

        // Ctrl+Z again — undo the cut
        app.executeCommand(new UndoCommand(app));
        System.out.println("After 2nd undo: " + app.activeEditor.text); // Hello World
    }
}

```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** The toolbar button, the context menu item, and `Ctrl+X` all trigger the exact same Cut operation. Without Command, each caller would duplicate the cut logic and maintain its own undo state — a mess. Command wraps the operation once. All three triggers just call `app.executeCommand(new CutCommand(...))`. Undo is automatically handled by the shared history stack.
>
> **Senior talking points:**
> 1. **`execute()` returns boolean (push-or-not decision)**: The elegant Refactoring Guru insight — `CopyCommand` returns `false` because it doesn't change editor state, so it's never added to the history stack. No redundant undo entries. The Invoker (`Application.executeCommand`) makes this decision uniformly — no per-command if/else needed.
> 2. **Undo via shared backup (not inverse operations)**: `BaseCommand.saveBackup()` snapshots `editor.text` before any change. `undo()` just restores it. This is simpler and more reliable than implementing an inverse for each command (e.g., "undo a cut" ≠ "paste"). The tradeoff is memory — each history entry holds a full text snapshot (Memento pattern territory).
> 3. **Same command, multiple triggers**: The same `CutCommand` object can be wired to a toolbar button, a keyboard shortcut, and a context menu item. If you swap the command, all three triggers update automatically — zero duplication.
> 4. **Command vs Strategy**: Both parameterize behaviour. Strategy swaps *how* something is done (algorithm). Command wraps *what* to do (a specific action with its own state/receiver/undo). Commands are typically single-use, stateful objects; Strategies are stateless and reusable.

> **🌍 Real-World Example**:
> - **Java (`Runnable` / `Callable`)**: `Runnable` is the Command interface. `ExecutorService.submit(runnable)` is the Invoker. You hand it a command object; it decides when and on which thread to run it.
> - **Spring Batch (`Tasklet`)**: Each step is a `Tasklet` — a Command. Spring Batch (the Invoker) manages retries, transactions, and sequencing without knowing what each step actually does.
> - **Database Transaction Log**: A DB transaction is a macro-command. The WAL (Write-Ahead Log) is the command history — each entry can be replayed (redo) or reversed (undo/rollback).

### 4. Chain of Responsibility
**Definition**: Lets you pass a request along a chain of handlers. Each handler decides either to process the request or pass it to the next handler in the chain.
> **🎤 Senior Interview Angle**: Each check/step lives in its own class — not one giant if/else block. The chain is assembled at runtime, so you can add, remove, or reorder steps without touching any existing handler. Think: Spring Security filter chain, servlet middleware, or an API Gateway pipeline.

**Example (Online Order API — Request Middleware Pipeline)**:

*Scenario* (from Refactoring Guru): An ordering API must run several checks before processing an order — authenticate the user, throttle brute-force attempts, validate the request body, check the cache, and finally process the order. Without CoR, all this logic sits in one bloated method. Every new requirement (e.g., rate limiting) forces you to touch that method — a classic OCP violation.

```java
// ── 1. Handler Interface ───────────────────────────────────────────────────
// Every handler in the chain implements this.
// setNext() wires handlers together. handle() processes or passes the request.
public interface RequestHandler {
    RequestHandler setNext(RequestHandler next);
    String handle(Request request);
}

// ── 2. Base Handler — eliminates boilerplate ───────────────────────────────
// Stores the next handler reference and provides default pass-through behaviour.
// Concrete handlers call super.handle(request) to forward when they don't stop it.
public abstract class BaseRequestHandler implements RequestHandler {
    private RequestHandler next;

    @Override
    public RequestHandler setNext(RequestHandler next) {
        this.next = next;
        return next; // enables fluent chaining: a.setNext(b).setNext(c)
    }

    @Override
    public String handle(Request request) {
        if (next != null) {
            return next.handle(request); // pass down the chain by default
        }
        return null; // end of chain — nothing handled it
    }
}

// ── 3. Request object — passed through the entire chain ───────────────────
public class Request {
    public final String username;
    public final String password;
    public final String ipAddress;
    public final String orderData;

    public Request(String username, String password, String ipAddress, String orderData) {
        this.username = username;
        this.password = password;
        this.ipAddress = ipAddress;
        this.orderData = orderData;
    }
}

// ── 4. Concrete Handlers — one responsibility each ────────────────────────

// Step 1: Authentication — reject if credentials are wrong
public class AuthenticationHandler extends BaseRequestHandler {
    @Override
    public String handle(Request request) {
        if (!"validUser".equals(request.username) || !"secret".equals(request.password)) {
            return "REJECTED [Auth]: Invalid credentials for user: " + request.username;
        }
        System.out.println("[Auth] User authenticated: " + request.username);
        return super.handle(request); // pass to next handler
    }
}

// Step 2: Throttling — block if the IP has too many recent failures
public class ThrottlingHandler extends BaseRequestHandler {
    private static final int MAX_REQUESTS = 5;
    private final java.util.Map<String, Integer> requestCounts = new java.util.HashMap<>();

    @Override
    public String handle(Request request) {
        int count = requestCounts.getOrDefault(request.ipAddress, 0);
        if (count >= MAX_REQUESTS) {
            return "REJECTED [Throttle]: Too many requests from IP: " + request.ipAddress;
        }
        requestCounts.put(request.ipAddress, count + 1);
        System.out.println("[Throttle] IP " + request.ipAddress + " — request count: " + (count + 1));
        return super.handle(request);
    }
}

// Step 3: Validation — reject if order data is missing or malformed
public class ValidationHandler extends BaseRequestHandler {
    @Override
    public String handle(Request request) {
        if (request.orderData == null || request.orderData.isBlank()) {
            return "REJECTED [Validation]: Order data cannot be empty.";
        }
        System.out.println("[Validation] Order data is valid.");
        return super.handle(request);
    }
}

// Step 4: Cache — short-circuit with cached result if available
public class CacheHandler extends BaseRequestHandler {
    private final java.util.Map<String, String> cache = new java.util.HashMap<>();

    public void populateCache(String key, String result) {
        cache.put(key, result);
    }

    @Override
    public String handle(Request request) {
        if (cache.containsKey(request.orderData)) {
            System.out.println("[Cache] Cache HIT for: " + request.orderData);
            return "CACHED: " + cache.get(request.orderData); // stop here, no DB call
        }
        System.out.println("[Cache] Cache MISS — forwarding to order handler.");
        return super.handle(request);
    }
}

// Step 5: Order Processor — the actual business logic, runs only if all checks pass
public class OrderProcessingHandler extends BaseRequestHandler {
    @Override
    public String handle(Request request) {
        System.out.println("[Order] Processing order: " + request.orderData);
        return "SUCCESS: Order placed for " + request.username + " → " + request.orderData;
    }
}

// ── 5. Caller — assembles the chain at runtime ────────────────────────────
class Main {
    public static void main(String[] args) {
        // Build handlers
        AuthenticationHandler auth = new AuthenticationHandler();
        ThrottlingHandler throttle = new ThrottlingHandler();
        ValidationHandler validation = new ValidationHandler();
        CacheHandler cache = new CacheHandler();
        OrderProcessingHandler orderProcessor = new OrderProcessingHandler();

        // Wire the chain — fluent API thanks to setNext() returning the next handler
        auth.setNext(throttle)
            .setNext(validation)
            .setNext(cache)
            .setNext(orderProcessor);

        // Pre-populate cache for one item
        cache.populateCache("ITEM_001", "Order #1001 confirmed");

        Request req1 = new Request("validUser", "secret", "192.168.1.1", "ITEM_002");
        System.out.println("\n--- Request 1 (valid, no cache) ---");
        System.out.println(auth.handle(req1));
        // [Auth]       User authenticated: validUser
        // [Throttle]   IP 192.168.1.1 — request count: 1
        // [Validation] Order data is valid.
        // [Cache]      Cache MISS — forwarding to order handler.
        // [Order]      Processing order: ITEM_002
        // SUCCESS: Order placed for validUser → ITEM_002

        Request req2 = new Request("validUser", "secret", "192.168.1.1", "ITEM_001");
        System.out.println("\n--- Request 2 (valid, cache hit) ---");
        System.out.println(auth.handle(req2));
        // [Auth]    User authenticated: validUser
        // [Throttle] IP 192.168.1.1 — request count: 2
        // [Validation] Order data is valid.
        // [Cache]   Cache HIT for: ITEM_001
        // CACHED: Order #1001 confirmed   ← stops here, never hits OrderProcessingHandler

        Request req3 = new Request("hacker", "wrong", "10.0.0.1", "ITEM_003");
        System.out.println("\n--- Request 3 (bad credentials) ---");
        System.out.println(auth.handle(req3));
        // REJECTED [Auth]: Invalid credentials for user: hacker  ← chain stops at step 1
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Without CoR, the ordering endpoint would have a deeply nested if/else tree — auth check, then throttle check, then validation check, all in one method. Adding caching or a new compliance check means editing that method. CoR gives each check its own class (SRP), and you add new steps by inserting a new handler into the chain — the existing handlers don't change (OCP).
>
> **Senior talking points:**
> 1. **Two CoR flavours**: In the *pipeline* flavour (used here), every handler that passes forwards the request — all handlers run in sequence. In the *event-bubbling* flavour (like DOM events or GUI help dialogs from the Guru example), the first handler that *can* handle the request stops the chain. Know which flavour fits your use case.
> 2. **Chain can stop early**: `AuthenticationHandler` returns immediately on failure — the throttle, validation, cache, and order processor never run. This is exactly how Spring Security short-circuits on auth failure.
> 3. **Chain is assembled at runtime**: The caller wires handlers together. You can swap in a `MockOrderProcessingHandler` for tests, or conditionally skip the `CacheHandler` for admin requests — without touching any handler class.
> 4. **CoR vs Decorator**: They look structurally similar (both chain objects via composition), but intent differs. Decorator *always* passes execution and *adds behaviour* around it. CoR handlers can *stop* the chain and each is independent. A Decorator never short-circuits.

> **🌍 Real-World Example**:
> - **Spring Security (`FilterChain`)**: `UsernamePasswordAuthenticationFilter`, `BasicAuthenticationFilter`, `ExceptionTranslationFilter` etc. are all handlers in a CoR chain. Each decides to process or call `chain.doFilter()` to pass through.
> - **Servlet API (`javax.servlet.Filter`)**: The `FilterChain.doFilter()` call is literally the CoR `super.handle()` — pass to the next filter or finally the servlet.
> - **Netty (`ChannelPipeline`)**: Netty's inbound/outbound channel handlers form a CoR pipeline. Each `ChannelHandler` processes the event or calls `ctx.fireChannelRead()` to forward it.



### 5. State
**Definition**: Allows an object to alter its behavior when its internal state changes. It appears as if the object changed its class.
> **🎤 Senior Interview Angle**: The OOP way to implement a Finite State Machine without a massive `switch` block. Each state lives in its own class. State objects hold a backreference to the context so they can trigger transitions themselves — no external `if/else` needed. Adding a new state = adding a new class, not editing existing ones.

**Example (Audio Player — Locked / Ready / Playing)**:

*Scenario* (from Refactoring Guru): An audio player has toolbar buttons (lock, play, next, previous). The same button does different things depending on whether the player is locked, idle, or actively playing. Without State, `AudioPlayer` owns a monstrous `switch(currentState)` inside every method. Adding a new state (e.g., `BufferingState`) means editing every method — a maintenance nightmare.

```java
// ── 1. State Interface — every state must handle all player actions ────────
public interface State {
    void clickLock();
    void clickPlay();
    void clickNext();
    void clickPrevious();
}

// ── 2. Context — the AudioPlayer ──────────────────────────────────────────
// Holds a reference to the CURRENT state object and delegates all button
// presses to it. Never checks "what state am I in?" itself.
public class AudioPlayer {
    private State currentState;
    private boolean playing = false;
    private String currentSong = "Song A";

    public AudioPlayer() {
        // Player starts in Ready state
        this.currentState = new ReadyState(this);
    }

    // States call this to trigger a transition
    public void changeState(State newState) {
        System.out.println("  [Transition] → " + newState.getClass().getSimpleName());
        this.currentState = newState;
    }

    // UI button delegates — player never inspects its own state
    public void clickLockButton()     { currentState.clickLock(); }
    public void clickPlayButton()     { currentState.clickPlay(); }
    public void clickNextButton()     { currentState.clickNext(); }
    public void clickPreviousButton() { currentState.clickPrevious(); }

    // Service methods called by states (the actual work)
    public void startPlayback() {
        playing = true;
        System.out.println("  [Player] ▶ Playing: " + currentSong);
    }
    public void stopPlayback() {
        playing = false;
        System.out.println("  [Player] ⏹ Stopped.");
    }
    public void nextSong()     { System.out.println("  [Player] ⏭ Next song."); }
    public void previousSong() { System.out.println("  [Player] ⏮ Previous song."); }
    public void fastForward()  { System.out.println("  [Player] ⏩ Fast forward 5s."); }
    public void rewind()       { System.out.println("  [Player] ⏪ Rewind 5s."); }
    public boolean isPlaying() { return playing; }
}

// ── 3. Concrete States — each encapsulates behaviour for one state ─────────
// Key insight: states hold a backreference to the player so THEY drive transitions.
// The player itself never decides what the next state is.

public class LockedState implements State {
    private final AudioPlayer player;
    public LockedState(AudioPlayer player) { this.player = player; }

    @Override
    public void clickLock() {
        // Unlock: go back to whichever active state is appropriate
        if (player.isPlaying()) {
            player.changeState(new PlayingState(player));
        } else {
            player.changeState(new ReadyState(player));
        }
    }

    @Override public void clickPlay()     { /* locked — do nothing */ }
    @Override public void clickNext()     { /* locked — do nothing */ }
    @Override public void clickPrevious() { /* locked — do nothing */ }
}

public class ReadyState implements State {
    private final AudioPlayer player;
    public ReadyState(AudioPlayer player) { this.player = player; }

    @Override
    public void clickLock() {
        player.changeState(new LockedState(player)); // lock the player
    }

    @Override
    public void clickPlay() {
        player.startPlayback();
        player.changeState(new PlayingState(player)); // move to Playing
    }

    @Override public void clickNext()     { player.nextSong(); }
    @Override public void clickPrevious() { player.previousSong(); }
}

public class PlayingState implements State {
    private final AudioPlayer player;
    public PlayingState(AudioPlayer player) { this.player = player; }

    @Override
    public void clickLock() {
        player.changeState(new LockedState(player)); // lock while playing
    }

    @Override
    public void clickPlay() {
        player.stopPlayback();
        player.changeState(new ReadyState(player)); // stop → back to Ready
    }

    @Override public void clickNext()     { player.fastForward(); } // single click = fast-forward
    @Override public void clickPrevious() { player.rewind(); }      // single click = rewind
}

// ── 4. Caller ─────────────────────────────────────────────────────────────
class Main {
    public static void main(String[] args) {
        AudioPlayer player = new AudioPlayer(); // starts in ReadyState

        System.out.println("--- Press Play (Ready → Playing) ---");
        player.clickPlayButton();
        // [Player] ▶ Playing: Song A
        // [Transition] → PlayingState

        System.out.println("\n--- Press Next while Playing (fast-forward) ---");
        player.clickNextButton();
        // [Player] ⏩ Fast forward 5s.

        System.out.println("\n--- Press Lock while Playing (Playing → Locked) ---");
        player.clickLockButton();
        // [Transition] → LockedState

        System.out.println("\n--- Press Next while Locked (ignored) ---");
        player.clickNextButton();
        // (nothing happens)

        System.out.println("\n--- Press Lock again (Locked → Playing, still playing) ---");
        player.clickLockButton();
        // [Transition] → PlayingState

        System.out.println("\n--- Press Play (stop) → back to Ready ---");
        player.clickPlayButton();
        // [Player] ⏹ Stopped.
        // [Transition] → ReadyState
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Without State, `AudioPlayer.clickPlay()` looks like:
> ```java
> public void clickPlay() {
>     if (state == LOCKED) { /* nothing */ }
>     else if (state == READY) { startPlayback(); state = PLAYING; }
>     else if (state == PLAYING) { stopPlayback(); state = READY; }
> }
> ```
> And every other method (`clickLock`, `clickNext`, `clickPrevious`) has the same switch. Adding a `BufferingState` means editing **every method**. State pattern fixes this by putting each state's logic in its own class — `AudioPlayer` just delegates to `currentState.clickPlay()`. Adding `BufferingState` = one new class, zero changes to existing code.
>
> **Senior talking points:**
> 1. **States drive their own transitions (backreference)**: This is the key structural difference from Strategy. States know about each other and call `player.changeState(new PlayingState(...))` themselves. A Strategy never does this — it's stateless and has no reference back to the context.
> 2. **State vs Strategy**: Both delegate behaviour via composition. But Strategy is "choose the algorithm from outside." State is "the object manages its own lifecycle — the current state decides what comes next." The context in State is almost passive.
> 3. **New state = new class, nothing else changes**: This is OCP in action. Adding a `BufferingState` means implementing `State`, writing logic for 4 buttons, and setting it as the current state — `AudioPlayer` and all other state classes remain untouched.
> 4. **Who triggers transitions?**: Either the Context or the State itself can call `changeState()`. Guru recommends letting States do it (as done here) for maximum encapsulation — the Context doesn't need to know valid transition rules.

> **🌍 Real-World Example**:
> - **Order Lifecycle (`PENDING → CONFIRMED → SHIPPED → DELIVERED`)**: Each status is a State. `order.confirm()` in `PendingState` transitions to `ConfirmedState`. Calling `confirm()` on an already-`DeliveredState` can throw an `IllegalStateException` — clean, explicit FSM.
> - **Spring Statemachine**: Spring has a first-class `StateMachineFactory` for exactly this — defining states and transitions for workflow engines, saga orchestration, or approval flows.
> - **TCP Connection (`LISTEN → SYN_RECEIVED → ESTABLISHED → CLOSE_WAIT`)**: The canonical FSM example from the GoF book. Each TCP state handles packets differently; the socket delegates to the current state object.



### 6. Template Method
**Definition**: Defines the skeleton of an algorithm in the superclass but lets subclasses override specific steps without changing the algorithm's overall structure.
> **🎤 Senior Interview Angle**: The Hollywood Principle — "Don't call us, we'll call you." The base class owns and controls the workflow. Subclasses just fill in the blanks. The template method itself is `final` — subclasses can never reorder or skip steps. This is how Spring's `JdbcTemplate`, `RestTemplate`, and `AbstractBeanFactory` all work.

**Example (Data Mining — PDF / CSV / DOC Report Extraction)**:

*Scenario* (from Refactoring Guru): A data mining app parses corporate documents in different formats (PDF, CSV, DOC) and produces a standardised analysis report. The **pipeline is always the same**: open file → extract raw data → parse it → analyse it → print the report → close file. Only the first two steps differ by format. Without Template Method, each format class duplicates the identical analyse/print/close logic. With it, the base class owns the pipeline and format-specific classes only override what's different.

```java
// ── 1. Abstract Class — owns the template method ──────────────────────────
// mine() is the TEMPLATE METHOD — final so subclasses can't reorder the steps.
// Abstract steps MUST be overridden. Optional steps have default implementations.
public abstract class DataMiner {

    // ── Template Method — the fixed pipeline ─────────────────────────────
    // Subclasses never touch this; they only override individual steps.
    public final void mine(String filePath) {
        String rawData  = openFile(filePath);   // abstract — format-specific
        String data     = extractData(rawData); // abstract — format-specific
        String parsed   = parseData(data);      // abstract — format-specific
        analyseData(parsed);                    // shared — in base class
        if (shouldPrintReport()) {              // hook — optional override
            printReport();
        }
        closeFile();                            // shared — in base class
    }

    // ── Abstract steps — subclasses MUST implement these ─────────────────
    protected abstract String openFile(String filePath);
    protected abstract String extractData(String rawData);
    protected abstract String parseData(String data);

    // ── Shared steps — common to ALL formats, defined once here ──────────
    protected void analyseData(String parsedData) {
        System.out.println("  [Analyse] Running common analysis on: " + parsedData);
    }

    protected void printReport() {
        System.out.println("  [Report]  Printing standardised report.");
    }

    protected void closeFile() {
        System.out.println("  [Close]   File closed.\n");
    }

    // ── Hook — optional override, does nothing by default ────────────────
    // Subclasses can flip this to skip the report without breaking the pipeline.
    protected boolean shouldPrintReport() {
        return true; // default: always print
    }
}

// ── 2. Concrete Subclasses — only override what's format-specific ─────────

public class PdfDataMiner extends DataMiner {
    @Override
    protected String openFile(String filePath) {
        System.out.println("  [PDF] Opening PDF: " + filePath);
        return "pdf-binary-stream";
    }

    @Override
    protected String extractData(String rawData) {
        System.out.println("  [PDF] Extracting text via PDF parser.");
        return "pdf-raw-text";
    }

    @Override
    protected String parseData(String data) {
        System.out.println("  [PDF] Parsing PDF structure into rows.");
        return "pdf-parsed-rows";
    }
}

public class CsvDataMiner extends DataMiner {
    @Override
    protected String openFile(String filePath) {
        System.out.println("  [CSV] Opening CSV: " + filePath);
        return "csv-stream";
    }

    @Override
    protected String extractData(String rawData) {
        System.out.println("  [CSV] Reading comma-separated values.");
        return "csv-raw-values";
    }

    @Override
    protected String parseData(String data) {
        System.out.println("  [CSV] Mapping columns to domain fields.");
        return "csv-parsed-rows";
    }
}

// DocDataMiner — overrides the hook to skip the report (e.g., internal docs only)
public class DocDataMiner extends DataMiner {
    @Override
    protected String openFile(String filePath) {
        System.out.println("  [DOC] Opening Word document: " + filePath);
        return "doc-stream";
    }

    @Override
    protected String extractData(String rawData) {
        System.out.println("  [DOC] Extracting paragraphs from DOCX format.");
        return "doc-raw-paragraphs";
    }

    @Override
    protected String parseData(String data) {
        System.out.println("  [DOC] Tokenizing paragraphs into sentences.");
        return "doc-parsed-sentences";
    }

    // Hook override — DOC files are internal; skip report printing
    @Override
    protected boolean shouldPrintReport() {
        return false;
    }
}

// ── 3. Caller — works with the base type; doesn't know the format ─────────
class Main {
    public static void main(String[] args) {
        // Client code is polymorphic — same mine() call, different behaviour
        DataMiner[] miners = {
            new PdfDataMiner(),
            new CsvDataMiner(),
            new DocDataMiner()
        };

        String[] files = { "report.pdf", "data.csv", "notes.doc" };

        for (int i = 0; i < miners.length; i++) {
            System.out.println("=== Mining: " + files[i] + " ===");
            miners[i].mine(files[i]);
            // Pipeline always runs in the same order:
            // openFile → extractData → parseData → analyseData → [printReport] → closeFile
            // Only openFile/extractData/parseData differ per subclass.
            // DocDataMiner skips printReport via the hook.
        }
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** Without Template Method, `PdfDataMiner`, `CsvDataMiner`, and `DocDataMiner` each have their own copy of `analyseData()`, `printReport()`, and `closeFile()`. They're identical — pure duplication. When the report format changes, you edit three places. Template Method pulls all shared steps into the base class once. Each subclass is left with only what's genuinely unique to it.
>
> **Senior talking points:**
> 1. **`final` on the template method**: This is intentional and important. It prevents subclasses from reordering or skipping steps — the base class guarantees the algorithm's structure. If you leave it non-final, a subclass could override `mine()` entirely and break the contract.
> 2. **Three kinds of steps**: (a) **Abstract** — subclass *must* provide implementation (`openFile`, `extractData`, `parseData`). (b) **Default** — shared logic in the base class, subclass *may* override (`analyseData`). (c) **Hook** — empty or trivially-defaulted method that subclasses *optionally* override for extension points (`shouldPrintReport`). Knowing these three is a strong senior signal.
> 3. **Template Method vs Strategy**: Both let you vary part of an algorithm. Template Method uses *inheritance* — the variation is baked in at compile time via subclassing. Strategy uses *composition* — the variation is injected at runtime via an interface. Strategy is more flexible; Template Method is simpler when you own the hierarchy. The Guru quote: "Template Method works at the class level, so it's static. Strategy works on the object level."
> 4. **Factory Method is a special case of Template Method**: A Factory Method is literally a one-step Template Method — the base class defines a creation algorithm with one abstract step (`createProduct()`), and subclasses fill it in. Recognising this relationship shows deep pattern literacy.

> **🌍 Real-World Example**:
> - **Spring's `JdbcTemplate`**: `query()` is the template method. It handles connection acquisition, statement preparation, result set iteration, and connection cleanup. You only provide the `RowMapper` (the format-specific step) — Spring controls the pipeline.
> - **`AbstractList` / `AbstractMap` (Java Collections)**: These abstract classes implement most `List`/`Map` methods by calling a few abstract primitives (`get(int index)`, `size()`). Implement those two, and you get `iterator()`, `contains()`, `indexOf()` for free.
> - **Servlet `HttpServlet`**: `service()` is the template method. It dispatches to `doGet()`, `doPost()`, etc. based on the HTTP verb. You override `doGet()` — the dispatch logic is fixed in the base class.



### 7. Iterator
**Definition**: Lets you traverse elements of a collection without exposing its underlying representation (list, stack, tree, graph, etc.).
> **🎤 Senior Interview Angle**: The collection doesn't change — you just swap the traversal object. This lets one collection support multiple traversal strategies (DFS, BFS, reverse) without adding traversal logic into the collection class itself. In Java, this is the entire foundation of the `for-each` loop — anything implementing `Iterable<T>` works with it.

**Pattern Structure — 4 roles:**

```
«interface» Iterable<T>          «interface» Iterator<T>
──────────────────────           ──────────────────────
+iterator(): Iterator<T>         +hasNext(): boolean
        │                        +next(): T
        │ implements                       │ implements
        ▼                                  ▼
 ConcreteCollection               ConcreteIterator
 ─────────────────                ─────────────────
 -elements[ ]                    -collection ref
 +iterator() ──── creates ──────▶ -cursor / state
                                  +hasNext()
                                  +next()
```

| Role | Responsibility |
|---|---|
| `Iterable<T>` (interface) | The collection contract — must be able to produce an iterator |
| `Iterator<T>` (interface) | The traversal contract — `hasNext()` + `next()` |
| `ConcreteCollection` | Stores elements; acts as an **iterator factory** via `iterator()` |
| `ConcreteIterator` | Holds **all traversal state** (cursor, visited set, etc.) — the collection never owns this |

**Minimal Example — a simple list of numbers:**

```java
import java.util.Iterator;
import java.util.NoSuchElementException;

// ── ConcreteIterator — owns the cursor, the collection stays untouched ─────
class NumberIterator implements Iterator<Integer> {
    private final int[] data;
    private int cursor = 0; // all traversal state lives HERE, not in the list

    NumberIterator(int[] data) { this.data = data; }

    @Override public boolean hasNext() { return cursor < data.length; }
    @Override public Integer next() {
        if (!hasNext()) throw new NoSuchElementException();
        return data[cursor++];
    }
}

// ── ConcreteCollection — stores data, produces iterators ─────────────────
// Implements Iterable<Integer> so it works with Java's for-each loop.
class NumberList implements Iterable<Integer> {
    private final int[] data;
    NumberList(int... data) { this.data = data; }

    @Override
    public Iterator<Integer> iterator() {
        return new NumberIterator(data); // each call → fresh iterator, fresh cursor
    }
}

// ── Caller ─────────────────────────────────────────────────────────────────
class Main {
    public static void main(String[] args) {
        NumberList list = new NumberList(10, 20, 30, 40);

        // for-each works because NumberList implements Iterable
        for (int n : list) System.out.print(n + " "); // 10 20 30 40

        // Two independent iterators on the same list — cursors don't share state
        Iterator<Integer> it1 = list.iterator();
        Iterator<Integer> it2 = list.iterator();
        it1.next(); // 10 — it1 cursor = 1
        it2.next(); // 10 — it2 cursor = 1, completely independent
    }
}
```
> **Key takeaway from this simple example:** `NumberList` has zero idea *how* it's being traversed — it just hands out a fresh `NumberIterator` each time. The iterator owns the cursor. Call `list.iterator()` twice, get two independent cursors. This is exactly why Java's `ArrayList` is thread-safe for *reads* when you create separate iterators — each thread has its own cursor state.

---

**More complex example (Graph with DFS + BFS):**

*Scenario* (inspired by Refactoring Guru's social network example): A `UserGraph` models a social/org-chart — users connected by edges. A reporting tool needs to walk every user exactly once, but sometimes in DFS order (for hierarchical reports) and sometimes in BFS order (for level-by-level organisation charts). Without Iterator, the graph would need both traversal algorithms baked into it — polluting its responsibility. With Iterator, the graph stays clean and exposes `depthFirstIterator()` / `breadthFirstIterator()` — the caller picks the traversal at runtime.

```java
import java.util.*;

// ── 1. The element type ───────────────────────────────────────────────────
public class User {
    public final int id;
    public final String name;
    public User(int id, String name) { this.id = id; this.name = name; }
    @Override public String toString() { return name + "(id=" + id + ")"; }
}

// ── 2. Iterator Interface — Java's built-in works perfectly here ──────────
// java.util.Iterator<T> already gives us hasNext() + next().
// We implement it directly so our iterators plug into Java's for-each loop.

// ── 3. Concrete Iterator — Depth-First Search ─────────────────────────────
// Owns ALL traversal state (stack + visited set) independently of the graph.
// Multiple DFS iterators on the SAME graph can run in parallel without conflict.
public class DepthFirstIterator implements Iterator<User> {
    private final Map<Integer, List<User>> adjacencyList;
    private final Deque<User> stack = new ArrayDeque<>();
    private final Set<Integer> visited = new HashSet<>();

    public DepthFirstIterator(Map<Integer, List<User>> adjacencyList, User startNode) {
        this.adjacencyList = adjacencyList;
        stack.push(startNode);
    }

    @Override
    public boolean hasNext() {
        // Skip already-visited nodes at the top of the stack
        while (!stack.isEmpty() && visited.contains(stack.peek().id)) {
            stack.pop();
        }
        return !stack.isEmpty();
    }

    @Override
    public User next() {
        if (!hasNext()) throw new NoSuchElementException();
        User current = stack.pop();
        visited.add(current.id);
        // Push neighbours in reverse so left-most is processed first
        List<User> neighbours = adjacencyList.getOrDefault(current.id, List.of());
        for (int i = neighbours.size() - 1; i >= 0; i--) {
            if (!visited.contains(neighbours.get(i).id)) {
                stack.push(neighbours.get(i));
            }
        }
        return current;
    }
}

// ── 4. Concrete Iterator — Breadth-First Search ───────────────────────────
public class BreadthFirstIterator implements Iterator<User> {
    private final Map<Integer, List<User>> adjacencyList;
    private final Queue<User> queue = new LinkedList<>();
    private final Set<Integer> visited = new HashSet<>();

    public BreadthFirstIterator(Map<Integer, List<User>> adjacencyList, User startNode) {
        this.adjacencyList = adjacencyList;
        queue.offer(startNode);
        visited.add(startNode.id);
    }

    @Override
    public boolean hasNext() { return !queue.isEmpty(); }

    @Override
    public User next() {
        if (!hasNext()) throw new NoSuchElementException();
        User current = queue.poll();
        for (User neighbour : adjacencyList.getOrDefault(current.id, List.of())) {
            if (!visited.contains(neighbour.id)) {
                visited.add(neighbour.id);
                queue.offer(neighbour);
            }
        }
        return current;
    }
}

// ── 5. Collection — UserGraph ─────────────────────────────────────────────
// Knows how to store users and edges. Does NOT know how to traverse.
// It's a factory for iterators — returns the right one on request.
public class UserGraph implements Iterable<User> {
    private final Map<Integer, List<User>> adjacencyList = new HashMap<>();
    private User root;

    public void addUser(User user) {
        adjacencyList.putIfAbsent(user.id, new ArrayList<>());
        if (root == null) root = user; // first added = root for traversal
    }

    public void addEdge(User from, User to) {
        adjacencyList.get(from.id).add(to);
    }

    // Default iterator: DFS (used by for-each)
    @Override
    public Iterator<User> iterator() {
        return depthFirstIterator();
    }

    public Iterator<User> depthFirstIterator() {
        return new DepthFirstIterator(adjacencyList, root);
    }

    public Iterator<User> breadthFirstIterator() {
        return new BreadthFirstIterator(adjacencyList, root);
    }
}

// ── 6. Caller — client code never touches graph internals ─────────────────
class Main {
    public static void main(String[] args) {
        //      Alice
        //     /     \
        //   Bob     Carol
        //   / \
        // Dave  Eve
        User alice = new User(1, "Alice"), bob   = new User(2, "Bob");
        User carol = new User(3, "Carol"), dave  = new User(4, "Dave");
        User eve   = new User(5, "Eve");

        UserGraph graph = new UserGraph();
        for (User u : new User[]{alice, bob, carol, dave, eve}) graph.addUser(u);
        graph.addEdge(alice, bob);  graph.addEdge(alice, carol);
        graph.addEdge(bob, dave);   graph.addEdge(bob, eve);

        // DFS — via for-each (uses default iterator())
        System.out.println("DFS traversal:");
        for (User user : graph) {           // works because UserGraph implements Iterable
            System.out.print(user.name + " ");
        }
        // Alice Bob Dave Eve Carol

        // BFS — swap the iterator, same client loop logic
        System.out.println("\nBFS traversal:");
        Iterator<User> bfs = graph.breadthFirstIterator();
        while (bfs.hasNext()) {
            System.out.print(bfs.next().name + " ");
        }
        // Alice Bob Carol Dave Eve

        // Two independent DFS iterators on the same graph — don't interfere
        System.out.println("\nTwo parallel DFS iterators:");
        Iterator<User> dfs1 = graph.depthFirstIterator();
        Iterator<User> dfs2 = graph.depthFirstIterator();
        System.out.print("dfs1 first: " + dfs1.next().name); // Alice
        System.out.print(", dfs2 first: " + dfs2.next().name); // Alice — independent state
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** If traversal logic sat inside `UserGraph`, the class would need `dfsTraverse()`, `bfsTraverse()`, `reverseTraverse()` methods — its core responsibility (storing users/edges) gets buried under traversal concerns. Any new traversal strategy means editing the collection. Iterator extracts traversal into its own object: the graph just provides a factory method, each iterator owns its own state (stack/queue/visited set), and adding a new traversal = adding one new class.
>
> **Senior talking points:**
> 1. **Each iterator owns its own traversal state**: The `visited` set and `stack`/`queue` live in the iterator, not in the graph. This is why two iterators on the same graph don't interfere — they maintain completely independent state. This is the Guru's key insight: "several iterators can go through the same collection at the same time, independently of each other."
> 2. **Implements `java.util.Iterator<T>` + `Iterable<T>`**: By implementing the standard interfaces, `UserGraph` works with Java's `for-each` loop, `Stream.of()`, and anything in the Collections framework — zero extra effort. This is exactly what all Java collections do.
> 3. **Lazy evaluation via Iterator**: The iterator computes the next element only when `next()` is called. This is how Java's `Stream` API and database cursor-based iteration work — you don't load all elements upfront. This is the bridge to understanding `Stream.iterator()` and `ResultSet` in JDBC.
> 4. **Collection is a factory for iterators**: The collection's job is to return the right iterator via `iterator()`, `depthFirstIterator()`, etc. — not to perform traversal itself. This separation is what keeps the collection class lean regardless of how many traversal strategies you add.

> **🌍 Real-World Example**:
> - **Java `Collections` (`ArrayList`, `LinkedList`, `TreeSet`)**: All implement `Iterable<T>` and return concrete iterators. `ArrayList`'s iterator tracks a cursor index; `LinkedList`'s tracks a node pointer. Same client `for-each` loop, completely different internals.
> - **JDBC `ResultSet`**: A `ResultSet` is literally an Iterator over database rows. `rs.next()` = `iterator.next()`. The client doesn't know if results come from a local cache, a cursor, or a streaming TCP socket.
> - **Spring Data `Page<T>` / `Slice<T>`**: Spring's pagination API is an iterator over database result pages. `page.hasNext()` + `page.nextPageable()` — the Iterator pattern applied to paginated queries.



### 8. Mediator
**Definition**: Reduces chaotic dependencies between objects by restricting direct communications between them, forcing all collaboration to go through a central mediator object.
> **🎤 Senior Interview Angle**: Turns a tangled mesh of N×N direct dependencies into a star topology — every component talks only to the mediator, never to each other. The trade-off is that the mediator can grow into a God Object if not kept disciplined.

**The problem it solves — mesh vs. star:**
```
WITHOUT Mediator (mesh — N×N couplings):   WITH Mediator (star — N×1 couplings):
   A ←──→ B                                    A ──→ Mediator ←── B
   ↑ ↘  ↗ ↑                                              │
   |   ×   |                                    C ──────▶│◀────── D
   ↓ ↗  ↘ ↓                                   (nobody knows anyone else)
   C ←──→ D
```

**Example (Chat App — ONE `alice` object, multiple rooms, zero changes to `ChatUser`)**:

*Scenario*: Alice is a real user. She's in a `#general` group channel AND has a private DM with Bob AND receives admin announcements. Without Mediator, `ChatUser` needs to hold references to all group members, a DM target field, and a list of subscribers — it is contaminated with routing logic. With Mediator, `ChatUser` knows nothing about routing. Alice just calls `sendTo(room, message)`. The room (mediator) decides who gets it.

```java
import java.util.*;

// ── 1. Mediator Interface ─────────────────────────────────────────────────
public interface ChatRoom {
    void deliver(String message, User sender); // mediator routes the message
    void register(User user);                  // add a participant to this room
}

// ── 2. User — joins multiple rooms, sends to a specific one ───────────────
// No single mediator stored. User is independent of ANY room type.
public class User {
    public final String name;

    public User(String name) {
        this.name = name; // No mediator in constructor — User is room-agnostic
    }

    // Alice can join as many rooms as needed — just register with each mediator
    public void joinRoom(ChatRoom room) {
        room.register(this);
    }

    // Send to a specific room — User doesn't know the room's routing rules
    public void sendTo(ChatRoom room, String message) {
        System.out.println("  [" + name + "] → " + room.getClass().getSimpleName()
            + ": \"" + message + "\"");
        room.deliver(message, this);
    }

    // Called BY the mediator — user just prints; knows nothing about routing
    public void receive(String message, String from, String roomTag) {
        System.out.println("    ✉ [" + roomTag + "] " + name
            + " ← " + from + ": \"" + message + "\"");
    }
}

// ── 3a. GroupChatRoom — broadcast to all registered members except sender ─
public class GroupChatRoom implements ChatRoom {
    private final String name;
    private final List<User> members = new ArrayList<>();

    public GroupChatRoom(String name) { this.name = name; }

    @Override public void register(User user) { members.add(user); }

    @Override
    public void deliver(String message, User sender) {
        for (User u : members) {
            if (u != sender) u.receive(message, sender.name, "#" + name);
        }
    }
}

// ── 3b. DirectMessageRoom — private 1-on-1, only 2 participants ──────────
public class DirectMessageRoom implements ChatRoom {
    private final List<User> participants = new ArrayList<>();

    @Override
    public void register(User user) {
        if (participants.size() < 2) participants.add(user);
    }

    @Override
    public void deliver(String message, User sender) {
        for (User u : participants) {
            if (u != sender) u.receive(message, sender.name, "DM");
        }
    }
}

// ── 3c. AdminBroadcastRoom — only the admin can send ─────────────────────
public class AdminBroadcastRoom implements ChatRoom {
    private User admin;
    private final List<User> subscribers = new ArrayList<>();

    public void setAdmin(User admin) { this.admin = admin; }

    @Override public void register(User user) { subscribers.add(user); }

    @Override
    public void deliver(String message, User sender) {
        if (sender != admin) {
            System.out.println("    ✗ [Announcements] " + sender.name
                + " is not admin — blocked.");
            return;
        }
        for (User u : subscribers) u.receive(message, sender.name, "📢 Announcements");
    }
}

// ── 4. Main — ONE alice, ONE bob — they join multiple rooms ───────────────
class Main {
    public static void main(String[] args) {

        // Create REAL users — no mediator coupled at construction
        User alice = new User("Alice");
        User bob   = new User("Bob");
        User carol = new User("Carol");
        User adminUser = new User("Admin");

        // Create rooms (mediators)
        GroupChatRoom general  = new GroupChatRoom("general");
        DirectMessageRoom dm   = new DirectMessageRoom();
        AdminBroadcastRoom ann = new AdminBroadcastRoom();
        ann.setAdmin(adminUser);

        // Alice and Bob join #general; Carol joins too
        alice.joinRoom(general); bob.joinRoom(general); carol.joinRoom(general);

        // Alice and Bob also open a private DM — same alice/bob objects
        alice.joinRoom(dm); bob.joinRoom(dm);

        // Bob and Carol subscribe to admin announcements
        bob.joinRoom(ann); carol.joinRoom(ann);

        // ── Alice sends to group ───────────────────────────────────────────
        System.out.println("=== Alice sends to #general ===");
        alice.sendTo(general, "Hey team, standup in 5!");
        //   ✉ [#general] Bob   ← Alice: "Hey team, standup in 5!"
        //   ✉ [#general] Carol ← Alice: "Hey team, standup in 5!"

        // ── Alice sends a private DM to Bob (carol sees NOTHING) ──────────
        System.out.println("\n=== Alice sends a private DM to Bob ===");
        alice.sendTo(dm, "Bob, are you joining the call?");
        //   ✉ [DM] Bob ← Alice: "Bob, are you joining the call?"
        // Carol is NOT in this DM — she receives nothing

        // ── Bob replies in the DM ─────────────────────────────────────────
        System.out.println("\n=== Bob replies in DM ===");
        bob.sendTo(dm, "Yes, give me 2 mins!");
        //   ✉ [DM] Alice ← Bob: "Yes, give me 2 mins!"

        // ── Admin broadcasts (only Bob + Carol are subscribers) ───────────
        System.out.println("\n=== Admin sends announcement ===");
        adminUser.sendTo(ann, "Deployment at 6PM, expect 10min downtime.");
        //   ✉ [📢 Announcements] Bob   ← Admin: "Deployment at 6PM..."
        //   ✉ [📢 Announcements] Carol ← Admin: "Deployment at 6PM..."

        // ── Alice tries to send an announcement (she's not admin) ─────────
        System.out.println("\n=== Alice tries to send announcement ===");
        alice.joinRoom(ann);
        alice.sendTo(ann, "Can I post here?");
        //   ✗ [Announcements] Alice is not admin — blocked.
    }
}
```
> **💡 Why this fixes your observation**: `alice` is created ONCE with just her name. She then calls `joinRoom()` on whichever rooms she needs — one `alice` object participates in `#general`, a private DM, and the announcements channel simultaneously. `ChatUser`'s code never changes regardless of how many room types exist.
>
> The real-world parallel: in Slack, YOU (one identity) are in #general, have DMs, and are in #announcements. You don't become a different person for each channel. The channel (mediator) decides who sees your message.
>
> **Senior talking points:**
> 1. **Component reusability is the headline benefit**: `User` class has zero routing logic. `GroupChatRoom`, `DirectMessageRoom`, `AdminBroadcastRoom` each have completely different routing rules — and `User` doesn't know or care. Adding a `ThreadedRoom` = one new class, `User` untouched.
> 2. **Mesh → Star topology**: Without Mediator, N users = O(N²) couplings. With Mediator, O(N). Alice would need direct references to Bob, Carol, Admin for each channel — and every new user requires updating Alice's state.
> 3. **The God Object risk**: The mediator absorbs all routing logic — keep it focused on *orchestration only*. If it starts making domain decisions (is the user on a paid plan?), extract that to a policy/rules object.
> 4. **Mediator vs Observer**: Mediator = centralised synchronous hub. Observer = decentralised; subscribers opt-in dynamically. `AdminBroadcastRoom` resembles Observer internally (subscriber list), but the pattern is Mediator because components only know the room, not each other.



### 9. Memento
**Definition**: Lets you save and restore an object's previous state without exposing the details of its implementation.
> **🎤 Senior Interview Angle**: The key constraint is **encapsulation**. Naïve undo (making all fields public so an external history class can copy them) breaks the object's encapsulation and creates tight coupling to every field. Memento says: *the originator creates its own snapshot* — it's the only one with full access to its private state. The caretaker (history) stores the snapshots but can't read inside them.

**Pattern Structure — 3 roles:**

```
┌──────────────────────────────┐     creates     ┌──────────────────────┐
│  Originator (Editor)         │────────────────▶│  Memento (Snapshot)  │
│──────────────────────────────│                 │──────────────────────│
│ -content: String (private)   │                 │ -content (private)   │
│ -cursorPos: int (private)    │ ◀── restores ── │ -cursorPos (private) │
│ +save(): Snapshot            │                 │ -selectionWidth      │
│ +restore(Snapshot)           │                 │ (immutable — no set) │
└──────────────────────────────┘                 └──────────────────────┘
                                                           ▲
                                                           │ stores, never reads
                                               ┌──────────┴───────────────┐
                                               │  Caretaker (History)     │
                                               │──────────────────────────│
                                               │ -undoStack: Deque<Snap>  │
                                               │ -redoStack: Deque<Snap>  │
                                               │ +backup()                │
                                               │ +undo()                  │
                                               │ +redo()                  │
                                               └──────────────────────────┘
```

| Role | Who | Responsibility |
|---|---|---|
| **Originator** | `Editor` | Creates snapshots of itself; restores from them. Has full access to its own state. |
| **Memento** | `Editor.Snapshot` (private static nested) | Immutable value object. All fields `private final`. No other class can read or even name this type. |
| **Caretaker** | `Editor.History` (private static nested) | Owns `undoStack`/`redoStack`. Stores Snapshots but **cannot read inside them** — only `Editor` can. Invisible to callers. |



**Example (Text Editor — Undo / Redo, History embedded inside Editor)**:

*Scenario* (Refactoring Guru's canonical example, improved design): A text editor lets users type, move the cursor, and change selection width. Every mutating operation should be undoable. The `History` (Caretaker) is a **private implementation detail** of `Editor` — it auto-snapshots before every change. The caller never touches `History` directly.

> **Design choice**: History is embedded, not exposed. Each mutation method calls `autoBackup()` internally before changing state. The caller gets a clean API — `editor.type()`, `editor.undo()`, `editor.redo()` — with zero manual backup calls. This is exactly how `javax.swing.text.AbstractDocument` works internally.

```java
import java.util.*;

// ══ ORIGINATOR ═══════════════════════════════════════════════════════════════
public class Editor {
    private String content        = "";
    private int    cursorPos      = 0;
    private int    selectionWidth = 0;

    // ── Caretaker — private inner class, invisible to callers ────────────────
    private final History history = new History();

    // ── Editing operations — each auto-snapshots before mutating ─────────────
    public void type(String text) {
        history.backup(new Snapshot(content, cursorPos, selectionWidth));
        content    = content.substring(0, cursorPos) + text
                   + content.substring(cursorPos);
        cursorPos += text.length();
    }

    public void moveCursor(int pos) {
        history.backup(new Snapshot(content, cursorPos, selectionWidth));
        this.cursorPos = pos;
    }

    public void select(int width) {
        history.backup(new Snapshot(content, cursorPos, selectionWidth));
        this.selectionWidth = width;
    }

    // ── Undo / Redo delegated to the internal Caretaker ──────────────────────
    public boolean undo() {
        Snapshot snap = history.undo(new Snapshot(content, cursorPos, selectionWidth));
        if (snap == null) return false;
        restore(snap);
        return true;
    }

    public boolean redo() {
        Snapshot snap = history.redo(new Snapshot(content, cursorPos, selectionWidth));
        if (snap == null) return false;
        restore(snap);
        return true;
    }

    private void restore(Snapshot snap) {
        // Editor can access Snapshot's private fields directly — nested class privilege
        this.content        = snap.content;
        this.cursorPos      = snap.cursorPos;
        this.selectionWidth = snap.selectionWidth;
    }

    public String getContent()   { return content; }
    public int    getCursorPos() { return cursorPos; }
    public int    undoLevels()   { return history.undoLevels(); }
    public int    redoLevels()   { return history.redoLevels(); }

    // ══ MEMENTO ══════════════════════════════════════════════════════════════
    // Nested → Editor can read private fields; no other class can even name this type.
    // Immutable → all fields final, no setters, constructed once.
    private static final class Snapshot {
        private final String content;
        private final int    cursorPos;
        private final int    selectionWidth;

        private Snapshot(String content, int cursorPos, int selectionWidth) {
            this.content        = content;
            this.cursorPos      = cursorPos;
            this.selectionWidth = selectionWidth;
        }
    }

    // ══ CARETAKER ════════════════════════════════════════════════════════════
    // Stores and manages Snapshots. Cannot read inside them — only Editor can.
    // Lives inside Editor so it's a private implementation detail.
    private static final class History {
        private final Deque<Snapshot> undoStack = new ArrayDeque<>();
        private final Deque<Snapshot> redoStack = new ArrayDeque<>();

        void backup(Snapshot current) {
            undoStack.push(current);
            redoStack.clear();   // new edit invalidates redo branch
        }

        Snapshot undo(Snapshot current) {
            if (undoStack.isEmpty()) return null;
            redoStack.push(current);   // save where we are, for redo
            return undoStack.pop();
        }

        Snapshot redo(Snapshot current) {
            if (redoStack.isEmpty()) return null;
            undoStack.push(current);
            return redoStack.pop();
        }

        int undoLevels() { return undoStack.size(); }
        int redoLevels() { return redoStack.size(); }
    }
}


// ── Caller — talks to ONE object, zero manual backup calls ────────────────
class Main {
    public static void main(String[] args) {
        Editor editor = new Editor();

        editor.type("Hello");
        System.out.println(editor.getContent());       // Hello

        editor.type(", World");
        System.out.println(editor.getContent());       // Hello, World

        editor.moveCursor(12);
        editor.type("!");
        System.out.println(editor.getContent());       // Hello, World!

        System.out.println("Undo levels: " + editor.undoLevels()); // 3

        editor.undo();
        System.out.println(editor.getContent());       // Hello, World  (! undone)

        editor.undo();
        System.out.println(editor.getContent());       // Hello          (, World undone)

        editor.redo();
        System.out.println(editor.getContent());       // Hello, World   (redo)

        // Encapsulation proof:
        // Editor.Snapshot is private static — no outside class can even
        // reference the type, let alone read snap.content.
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why Caretaker goes inside here:** When History is external, callers must remember to `backup()` before every mutation — a leaky abstraction. Any caller who forgets creates an un-undoable edit. The editor's contract becomes `history.backup(); editor.type(x)` — two calls, one semantic action. Embedding History fixes this: `Editor` owns the contract, enforces the snapshot discipline internally. The Memento *roles* are all still present — Originator (`Editor`), Memento (`Snapshot`), Caretaker (private `undoStack`/`redoStack` inside `Editor`). Just the Caretaker is encapsulated rather than exposed.
>
> **When to keep Caretaker external (the Refactoring Guru version):**
> When integrating with the **Command pattern** — each `Command` object is its *own* caretaker: it snapshots before executing and restores on `undo()`. This decouples the snapshot lifecycle from the editor and ties it to the command instead. Use this when different operations need different rollback strategies.
>
> **Senior talking points:**
> 1. **Nested class = the Java encapsulation trick**: `Snapshot` is `private static final` nested inside `Editor`. `Editor` can access `snap.content` directly (same outer class). No other class can even *name* `Editor.Snapshot` — it's `private`. No getters needed on `Snapshot` at all.
> 2. **Snapshot must be immutable**: All fields `final`, no setters, constructed once. A mutable snapshot = corrupted undo history.
> 3. **RAM trade-off**: Every mutation deep-copies state. For large documents, use delta snapshots (store only the diff), bounded history (max N steps), or lazy copy-on-write. Mention this in interviews — it shows you think at scale.
> 4. **`redoStack.clear()` on new edit**: Critical detail. When you type after undo, the redo branch is invalidated — just like every real text editor. Miss this and you get branching history corruption.

> **🌍 Real-World Example**:
> - **`javax.swing.text.AbstractDocument`**: Swing's document model manages its own `UndoableEdit` history internally. You call `doc.insertString()` — the undo snapshot is captured inside. `UndoManager` is the external caretaker option if you need multi-document undo.
> - **Database `SAVEPOINT` / `ROLLBACK TO SAVEPOINT`**: The DB engine saves a transaction checkpoint (memento) internally. The application says `ROLLBACK TO SAVEPOINT sp1` — it doesn't touch the DB's internal state pages directly.
> - **Git commits**: `git commit` = `autoBackup()`. `git reset --hard HEAD~1` = `undo()`. The `.git` object store is the internal caretaker. You interact via metadata (SHA, message), never the raw object internals.

**⚔️ Memento vs Command — same text editor, different mechanism:**

Both patterns can implement undo on a text editor. They look similar (`CommandHistory` and `History` are both stacks). The difference is **what goes into the stack**.

| | **Command** | **Memento** |
|---|---|---|
| **Stack stores** | `Command` objects (actions with undo logic) | `Snapshot` objects (dumb frozen state, no logic) |
| **`CommandHistory` vs `History`** | Stores *smart* objects — each Command knows how to reverse itself | Stores *dumb* objects — Snapshots are just data, can't undo themselves |
| **Undo works by** | `command.undo()` — Command reverses its own action | `editor.restore(snapshot)` — Editor rebuilds itself from the photo |
| **Intelligence lives in** | The Command class (distributed) | The Originator/Editor (centralised) |
| **Memory** | Low — stores only the delta (what changed) | High — full deep-copy of state on every action |
| **Analogy** | A *diary entry*: "I deleted 'World' at position 6" → re-insert it to undo | A *photocopy*: take a full copy before every change → restore the copy to undo |

> **When to use which:**
> - **Command undo** — when every action has a clean, computable inverse (cut → paste back, delete → re-insert). Surgical and memory-efficient.
> - **Memento undo** — when the object's state is complex or private and you can't easily compute the inverse. Simpler logic, trades memory for correctness.
> - **Both together** — Command fires the action; Memento captures the full state before it. On undo, Command delegates to `editor.restore(snapshot)`. Best of both — action tracking + reliable rollback.




### 10. Visitor
**Definition**: Lets you add new operations to a class hierarchy without modifying those classes. The operation is placed in a separate "visitor" object, and each element "accepts" the visitor and tells it which method to call.
> **🎤 Senior Interview Angle**: The core problem is the **Open/Closed vs Single Responsibility tension** on a *stable hierarchy*. The hierarchy (shapes, AST nodes, document elements) is frozen — you can't or don't want to touch those classes — but you need to keep adding new operations (export to XML, export to JSON, calculate area, validate). Without Visitor, every new operation means editing every class in the hierarchy. With Visitor, new operation = new class.

**Pattern Structure — 2 roles + Double Dispatch:**

```
«interface»                        «interface»
  Shape                               Visitor
──────────────                    ──────────────────────────────
+ accept(Visitor)                 + visit(Circle c)
      ▲                           + visit(Rectangle r)
      │                           + visit(Triangle t)
 ─────┴──────────────
 Circle  Rectangle  Triangle      XMLExportVisitor   AreaVisitor
  │          │          │         implements Visitor  implements Visitor
  └──────────┴──────────┘
  each implements accept(v) {
      v.visit(this)   ← Double Dispatch key line
  }
```

**The Double Dispatch trick — why `accept()` is needed:**
```java
// WHY you can't just do this:
for (Shape shape : shapes) {
    visitor.visit(shape);   // WRONG — Java resolves overloads at compile-time.
                             // shape is typed as Shape, so it always calls
                             // visit(Shape) — not visit(Circle) or visit(Rectangle).
}

// WHY accept() fixes it:
for (Shape shape : shapes) {
    shape.accept(visitor);  // Dispatch 1: Java calls the ACTUAL runtime type's accept()
                             // Inside Circle.accept():  visitor.visit(this)
                             //   → 'this' is Circle     ← Dispatch 2: correct overload
}
// Two dispatches = Double Dispatch. The object's runtime type selects the right visitor method.
```

**Example (Geometric Shapes — XML Export and Area Calculation)**:

*Scenario* (Refactoring Guru's canonical example): You have a stable `Shape` hierarchy (`Circle`, `Rectangle`, `Triangle`). Product keeps asking for new operations — "export to XML", "export to JSON", "calculate area", "validate geometry". Adding each operation as a method in every shape class pollutes their core responsibility and requires touching production code repeatedly. Visitor extracts each operation into its own class.

```java
import java.util.List;

// ── Element Interface — shapes only need to declare accept() ──────────────
// Shapes stay clean. The ONLY addition is this one accept() method.
public interface Shape {
    void accept(ShapeVisitor visitor); // the "door" for visitors
}

// ── Concrete Elements — each tells the visitor its own type via 'this' ────
public class Circle implements Shape {
    public final double radius;
    public final double x, y;

    public Circle(double radius, double x, double y) {
        this.radius = radius; this.x = x; this.y = y;
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this); // Double Dispatch: 'this' is Circle → visit(Circle) is called
    }
}

public class Rectangle implements Shape {
    public final double width, height, x, y;

    public Rectangle(double width, double height, double x, double y) {
        this.width = width; this.height = height; this.x = x; this.y = y;
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this); // 'this' is Rectangle → visit(Rectangle) called
    }
}

public class Triangle implements Shape {
    public final double base, height;

    public Triangle(double base, double height) {
        this.base = base; this.height = height;
    }

    @Override
    public void accept(ShapeVisitor visitor) {
        visitor.visit(this);
    }
}

// ── Visitor Interface — one visit() overload per concrete element type ────
public interface ShapeVisitor {
    void visit(Circle c);
    void visit(Rectangle r);
    void visit(Triangle t);
}

// ── Concrete Visitor #1 — XML Export ─────────────────────────────────────
// New operation = new class. Circle, Rectangle, Triangle untouched.
public class XMLExportVisitor implements ShapeVisitor {
    @Override
    public void visit(Circle c) {
        System.out.printf("<circle x='%.0f' y='%.0f' radius='%.0f'/>%n",
            c.x, c.y, c.radius);
    }

    @Override
    public void visit(Rectangle r) {
        System.out.printf("<rectangle x='%.0f' y='%.0f' w='%.0f' h='%.0f'/>%n",
            r.x, r.y, r.width, r.height);
    }

    @Override
    public void visit(Triangle t) {
        System.out.printf("<triangle base='%.0f' height='%.0f'/>%n",
            t.base, t.height);
    }
}

// ── Concrete Visitor #2 — Area Calculation ────────────────────────────────
// Completely different operation, zero changes to shape classes.
public class AreaVisitor implements ShapeVisitor {
    private double totalArea = 0;

    @Override public void visit(Circle c)    { totalArea += Math.PI * c.radius * c.radius; }
    @Override public void visit(Rectangle r) { totalArea += r.width * r.height; }
    @Override public void visit(Triangle t)  { totalArea += 0.5 * t.base * t.height; }

    public double getTotalArea() { return totalArea; }
}

// ── Caller ────────────────────────────────────────────────────────────────
class Main {
    public static void main(String[] args) {
        List<Shape> shapes = List.of(
            new Circle(5, 10, 20),
            new Rectangle(4, 6, 0, 0),
            new Triangle(3, 8)
        );

        // ── Operation 1: Export all shapes to XML ─────────────────────────
        System.out.println("=== XML Export ===");
        XMLExportVisitor xmlVisitor = new XMLExportVisitor();
        for (Shape shape : shapes) {
            shape.accept(xmlVisitor);  // Double Dispatch — each shape calls the right visit()
        }
        // <circle x='10' y='20' radius='5'/>
        // <rectangle x='0' y='0' w='4' h='6'/>
        // <triangle base='3' height='8'/>

        // ── Operation 2: Calculate total area ─────────────────────────────
        System.out.println("\n=== Area Calculation ===");
        AreaVisitor areaVisitor = new AreaVisitor();
        for (Shape shape : shapes) {
            shape.accept(areaVisitor);
        }
        System.out.printf("Total area: %.2f%n", areaVisitor.getTotalArea());
        // Total area: 106.54  (π*25 + 24 + 12)

        // Adding JSONExportVisitor tomorrow = one new class, shapes stay frozen.
    }
}
```
> **💡 Why it fits & Senior Angle**: **Why it's needed here:** If you added `exportToXML()` directly to `Circle`, `Rectangle`, `Triangle`, then next week when you need `exportToJSON()` you edit all three again. Next month `calculateArea()` — edit all three again. The hierarchy becomes a dumping ground for every operation anyone ever needs. Visitor separates the stable hierarchy from the volatile operations: shapes never change, you just keep adding new Visitor classes.
>
> **Senior talking points:**
> 1. **Double Dispatch — the "why accept()" question**: Java resolves overloaded methods at *compile time* based on the declared type. If `shape` is declared as `Shape`, `visitor.visit(shape)` always calls `visit(Shape)` regardless of runtime type. `accept()` forces a *runtime dispatch* on the element's actual class first, which then makes a second dispatch to the correct visitor overload. Two dispatches = Double Dispatch. This is the single most-asked follow-up question on Visitor.
> 2. **The trade-off — OCP vs OCP**: Adding a new *operation* (new Visitor) is easy — OCP satisfied. Adding a new *element type* (new `Ellipse` shape) means updating every existing Visitor class — OCP violated from the other side. Visitor optimises for "operations change often, hierarchy is stable." If the hierarchy changes often, use polymorphism instead.
> 3. **Visitor accumulates state across elements**: The `AreaVisitor` keeps a running `totalArea` as it visits each shape. This is the pattern's hidden superpower — a visitor can aggregate results across an entire object tree, which pure polymorphism can't do without external state.
> 4. **Visitor vs Strategy**: Both extract an algorithm from the class. Strategy swaps *one algorithm* for an object via composition (`shape.setDrawStrategy(strategy)`). Visitor applies *one operation across many different types* — it handles the type dispatch problem that Strategy doesn't address.

> **🌍 Real-World Example**:
> - **Java compiler AST (`javax.lang.model`)**: The compiler's `ElementVisitor`/`TypeVisitor` interfaces are a direct Visitor implementation. The AST (Abstract Syntax Tree) is the stable hierarchy — nodes never change. Operations (type-checking, code generation, annotation processing) are Visitors.
> - **Jackson `JsonSerializer`**: Jackson's serializer visits each field/node in your object graph. Your domain objects don't know about JSON — they just `accept` Jackson's visitor which handles the serialization per type.
> - **Refactoring Guru analogy (Insurance Agent)**: The agent (Visitor) visits each building (Element). If it's a residential building → medical insurance. If it's a bank → theft insurance. If it's a coffee shop → fire insurance. The building types don't change; new insurance products = new agent with new rules.


