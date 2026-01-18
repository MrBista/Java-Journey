# Static Keyword di Java - Panduan Lengkap

Saya akan menjelaskan secara detail tentang static keyword di Java, termasuk konsep, aturan, dan best practices.

## Konsep Dasar Static

Static adalah keyword yang membuat suatu member (variable, method, block, nested class) menjadi milik **class** bukan milik **object/instance**. Artinya, static member akan dibuat hanya sekali di memory dan dibagikan ke semua instance dari class tersebut.

## 1. Static Variable (Class Variable)

```java
public class Counter {
    static int count = 0;  // Static variable
    int instanceCount = 0; // Instance variable
    
    public Counter() {
        count++;           // Shared oleh semua instance
        instanceCount++;   // Unik untuk setiap instance
    }
}

// Penggunaan:
Counter c1 = new Counter();
Counter c2 = new Counter();
Counter c3 = new Counter();

System.out.println(Counter.count);        // Output: 3 (shared)
System.out.println(c1.instanceCount);     // Output: 1 (per instance)
```

**Karakteristik:**
- Dibuat saat class pertama kali di-load ke memory
- Hanya ada satu copy untuk semua instance
- Bisa diakses langsung melalui nama class
- Diinisialisasi sebelum instance variable

## 2. Static Method

```java
public class MathUtil {
    // Static method - tidak butuh instance
    public static int add(int a, int b) {
        return a + b;
    }
    
    // Instance method - butuh instance
    public int multiply(int a, int b) {
        return a * b;
    }
}

// Penggunaan:
int result = MathUtil.add(5, 3);  // Langsung via class name

MathUtil util = new MathUtil();
int result2 = util.multiply(5, 3); // Butuh instance
```

**Aturan Penting Static Method:**
- **TIDAK** bisa mengakses instance variable/method secara langsung
- **BISA** mengakses static variable/method lainnya
- **TIDAK** bisa menggunakan keyword `this` atau `super`
- **TIDAK** bisa di-override (tapi bisa di-hiding)

```java
public class Example {
    static int staticVar = 10;
    int instanceVar = 20;
    
    public static void staticMethod() {
        System.out.println(staticVar);      // ✓ OK
        // System.out.println(instanceVar); // ✗ ERROR
        // this.instanceVar;                // ✗ ERROR
        
        // Untuk akses instance variable, butuh object:
        Example obj = new Example();
        System.out.println(obj.instanceVar); // ✓ OK
    }
}
```

## 3. Static Block

Digunakan untuk inisialisasi static variable yang kompleks:

```java
public class DatabaseConfig {
    static String connectionString;
    static int maxConnections;
    
    // Static initialization block
    static {
        System.out.println("Static block dijalankan");
        connectionString = loadFromConfig();
        maxConnections = 100;
        
        // Bisa ada exception handling
        try {
            // Complex initialization
        } catch (Exception e) {
            // Handle error
        }
    }
    
    // Bisa ada multiple static blocks
    static {
        System.out.println("Static block kedua");
    }
    
    private static String loadFromConfig() {
        return "jdbc:mysql://localhost:3306/db";
    }
}
```

**Urutan eksekusi:**
1. Static variable diinisialisasi dengan default value
2. Static initialization block dijalankan (dari atas ke bawah)
3. Constructor dijalankan (saat instance dibuat)

## 4. Static Nested Class

```java
public class Outer {
    private static String staticOuter = "Static Outer";
    private String instanceOuter = "Instance Outer";
    
    // Static nested class
    static class StaticNested {
        void display() {
            System.out.println(staticOuter);        // ✓ OK
            // System.out.println(instanceOuter);   // ✗ ERROR
            
            // Butuh instance dari Outer:
            Outer outer = new Outer();
            System.out.println(outer.instanceOuter); // ✓ OK
        }
    }
}

// Penggunaan:
Outer.StaticNested nested = new Outer.StaticNested();
nested.display();
```

## Best Practices

### ✓ Gunakan Static Untuk:

**1. Utility Methods**
```java
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
```

**2. Constants**
```java
public class Constants {
    public static final String APP_NAME = "MyApp";
    public static final int MAX_RETRY = 3;
    public static final double PI = 3.14159;
}
```

**3. Factory Methods**
```java
public class User {
    private String name;
    private String email;
    
    private User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    // Static factory method
    public static User createUser(String name, String email) {
        // Bisa tambah validasi
        if (name == null || email == null) {
            throw new IllegalArgumentException("Name and email required");
        }
        return new User(name, email);
    }
    
    public static User createGuestUser() {
        return new User("Guest", "guest@example.com");
    }
}
```

**4. Singleton Pattern**
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        // Private constructor
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

**5. Counter atau Shared State**
```java
public class RequestCounter {
    private static int totalRequests = 0;
    
    public static synchronized void incrementRequest() {
        totalRequests++;
    }
    
    public static int getTotalRequests() {
        return totalRequests;
    }
}
```

### ✗ Hindari Static Untuk:

**1. Mutable Shared State (tanpa synchronization)**
```java
// ✗ BAD - race condition di multi-threading
public class BadCounter {
    public static int count = 0;
    
    public void increment() {
        count++; // Tidak thread-safe
    }
}

// ✓ GOOD
public class GoodCounter {
    private static int count = 0;
    
    public static synchronized void increment() {
        count++;
    }
}
```

**2. Over-using Static Methods**
```java
// ✗ BAD - sulit di-test, tidak bisa di-mock
public class UserService {
    public static void saveUser(User user) {
        // Database operation
    }
}

// ✓ GOOD - bisa di-inject dan di-mock
public class UserService {
    private UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    
    public void saveUser(User user) {
        repository.save(user);
    }
}
```

## Hal-hal Penting yang Perlu Diketahui

### 1. Static Import
```java
import static java.lang.Math.*;

public class Calculator {
    public double calculate() {
        return sqrt(pow(3, 2) + pow(4, 2)); // Tanpa Math.sqrt()
    }
}
```

### 2. Static vs Instance - Memory
```java
public class MemoryExample {
    static int staticVar = 100;  // 1 copy di memory (class area)
    int instanceVar = 200;       // N copies (heap, per instance)
    
    public static void main(String[] args) {
        MemoryExample obj1 = new MemoryExample();
        MemoryExample obj2 = new MemoryExample();
        
        obj1.staticVar = 999;
        System.out.println(obj2.staticVar); // 999 (shared!)
        
        obj1.instanceVar = 888;
        System.out.println(obj2.instanceVar); // 200 (independent)
    }
}
```

### 3. Static Method Hiding (bukan Override)
```java
class Parent {
    public static void display() {
        System.out.println("Parent static");
    }
}

class Child extends Parent {
    public static void display() {  // Method Hiding, bukan Override
        System.out.println("Child static");
    }
}

// Penggunaan:
Parent p = new Child();
p.display();  // Output: "Parent static" (tergantung reference type!)

Child c = new Child();
c.display();  // Output: "Child static"
```

### 4. Thread Safety
```java
public class ThreadSafeCounter {
    private static volatile int count = 0;
    
    // Option 1: synchronized method
    public static synchronized void increment() {
        count++;
    }
    
    // Option 2: synchronized block
    public static void increment2() {
        synchronized(ThreadSafeCounter.class) {
            count++;
        }
    }
    
    // Option 3: AtomicInteger (lebih efisien)
    private static AtomicInteger atomicCount = new AtomicInteger(0);
    
    public static void incrementAtomic() {
        atomicCount.incrementAndGet();
    }
}
```

### 5. Lazy Initialization dengan Thread-Safety
```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {}
    
    // Double-checked locking
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

## Common Mistakes

```java
// ✗ MISTAKE 1: Accessing static via instance (misleading)
Counter c = new Counter();
c.count++;  // BAD - terlihat seperti instance variable
Counter.count++;  // GOOD - jelas bahwa ini static

// ✗ MISTAKE 2: Non-static reference in static context
public class Example {
    int x = 10;
    
    public static void main(String[] args) {
        // System.out.println(x);  // ERROR!
        System.out.println(new Example().x);  // OK
    }
}

// ✗ MISTAKE 3: Overusing static for stateful logic
public class ShoppingCart {  // BAD DESIGN
    static List<Item> items = new ArrayList<>();
}
```
Sebenarnya ada **kesalahpahaman** di sini! 😊

## Klarifikasi Penting:

**Static method BISA dipanggil dari non-static method!** ✓

Yang **TIDAK BISA** adalah sebaliknya:
- ❌ Static method **tidak bisa** langsung call non-static method/variable
- ✅ Non-static method **bisa** call static method

## Contoh yang Benar:

```java
public class Example {
    static int staticVar = 100;
    int instanceVar = 200;
    
    // Static method
    public static void staticMethod() {
        System.out.println("Static method");
    }
    
    // Non-static method
    public void instanceMethod() {
        // ✓ BISA call static method
        staticMethod();
        
        // ✓ BISA akses static variable
        System.out.println(staticVar);
        
        // ✓ BISA akses instance variable
        System.out.println(instanceVar);
    }
    
    // Static method
    public static void anotherStaticMethod() {
        // ✓ BISA call static method lain
        staticMethod();
        
        // ✓ BISA akses static variable
        System.out.println(staticVar);
        
        // ❌ TIDAK BISA langsung akses instance variable
        // System.out.println(instanceVar); // ERROR!
        
        // ❌ TIDAK BISA langsung call instance method
        // instanceMethod(); // ERROR!
        
        // ✓ TAPI bisa kalau buat object dulu
        Example obj = new Example();
        obj.instanceMethod(); // OK
        System.out.println(obj.instanceVar); // OK
    }
}
```

## Visualisasi Aturannya:

```
┌─────────────────────────────────────────────┐
│         NON-STATIC METHOD                   │
│  ┌────────────────────────────────────┐    │
│  │ ✓ Bisa akses instance variable     │    │
│  │ ✓ Bisa akses static variable       │    │
│  │ ✓ Bisa call instance method        │    │
│  │ ✓ Bisa call static method          │    │
│  │ ✓ Bisa pakai 'this' dan 'super'    │    │
│  └────────────────────────────────────┘    │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│         STATIC METHOD                       │
│  ┌────────────────────────────────────┐    │
│  │ ❌ TIDAK bisa akses instance var   │    │
│  │ ✓ Bisa akses static variable       │    │
│  │ ❌ TIDAK bisa call instance method │    │
│  │ ✓ Bisa call static method          │    │
│  │ ❌ TIDAK bisa pakai 'this'/'super' │    │
│  └────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

## Contoh Praktis dalam Kode:

```java
public class Calculator {
    private String calculatorName;
    private static final double PI = 3.14159;
    
    public Calculator(String name) {
        this.calculatorName = name;
    }
    
    // Static utility method
    public static double calculateCircleArea(double radius) {
        return PI * radius * radius;
    }
    
    // Static utility method
    public static double calculateSquareArea(double side) {
        return side * side;
    }
    
    // Non-static method - BISA call static method
    public void printAreaComparison(double radius, double side) {
        System.out.println("Calculator: " + calculatorName);
        
        // ✓ Call static method dari non-static method - LEGAL!
        double circleArea = calculateCircleArea(radius);
        double squareArea = calculateSquareArea(side);
        
        System.out.println("Circle area: " + circleArea);
        System.out.println("Square area: " + squareArea);
    }
    
    // Non-static method lain
    public void demonstrateCalculations() {
        // ✓ Semua ini LEGAL
        double area1 = calculateCircleArea(5.0);
        double area2 = Calculator.calculateCircleArea(5.0); // Juga OK
        
        System.out.println("Area: " + area1);
    }
}

// Penggunaan:
public class Main {
    public static void main(String[] args) {
        Calculator calc = new Calculator("My Calculator");
        
        // Non-static method memanggil static method
        calc.printAreaComparison(5.0, 4.0);
        // Output:
        // Calculator: My Calculator
        // Circle area: 78.53975
        // Square area: 16.0
    }
}
```

## Contoh Real-World:

```java
public class UserService {
    private DatabaseConnection dbConnection;
    private String serviceName;
    
    public UserService(String serviceName) {
        this.serviceName = serviceName;
    }
    
    // Static utility method untuk validasi
    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
    
    // Static utility method untuk sanitasi
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim().toLowerCase();
    }
    
    // Non-static method - memanggil static utility methods
    public boolean registerUser(String name, String email) {
        System.out.println("Service: " + serviceName);
        
        // ✓ Call static methods dari non-static method
        String cleanName = sanitizeInput(name);
        String cleanEmail = sanitizeInput(email);
        
        // ✓ Call static method untuk validasi
        if (!isValidEmail(cleanEmail)) {
            System.out.println("Invalid email");
            return false;
        }
        
        // Proses registrasi...
        System.out.println("Registering: " + cleanName + " - " + cleanEmail);
        return true;
    }
    
    // Non-static method lain
    public void updateUser(String userId, String newEmail) {
        // ✓ Bebas call static method
        if (isValidEmail(newEmail)) {
            // Update logic...
        }
    }
}

// Penggunaan:
public class Main {
    public static void main(String[] args) {
        UserService service = new UserService("Main User Service");
        
        // Non-static method yang di dalamnya call static methods
        service.registerUser("  JOHN DOE  ", "john@example.com");
        // Output:
        // Service: Main User Service
        // Registering: john doe - john@example.com
    }
}
```

## Pattern yang Umum (Best Practice):

```java
public class OrderProcessor {
    private String processorId;
    private List<Order> orders;
    
    // Static utility methods
    public static double calculateTax(double amount) {
        return amount * 0.1;
    }
    
    public static double calculateDiscount(double amount, String couponCode) {
        if ("SAVE20".equals(couponCode)) {
            return amount * 0.2;
        }
        return 0;
    }
    
    public static boolean isWeekend() {
        int day = LocalDate.now().getDayOfWeek().getValue();
        return day == 6 || day == 7;
    }
    
    // Non-static method menggunakan static utilities
    public double processOrder(Order order, String coupon) {
        double subtotal = order.getAmount();
        
        // ✓ Call static methods
        double discount = calculateDiscount(subtotal, coupon);
        double afterDiscount = subtotal - discount;
        double tax = calculateTax(afterDiscount);
        double total = afterDiscount + tax;
        
        // Weekend surcharge
        if (isWeekend()) {
            total += 5.0;
        }
        
        System.out.println("Processor: " + processorId);
        System.out.println("Total: $" + total);
        
        return total;
    }
}
```

## Kenapa Pattern Ini Bagus?

1. **Separation of Concerns**: Static methods untuk logic yang tidak butuh state, non-static untuk yang butuh state
2. **Reusability**: Static utility methods bisa dipanggil dari mana saja
3. **Testing**: Lebih mudah test karena utility methods tidak tergantung state
4. **Clean Code**: Instance methods fokus ke business logic, utilities terpisah

## Kesimpulan:

**Kamu BEBAS call static method dari non-static method!** Ini bahkan sangat umum dan merupakan best practice. Yang perlu diingat:

- ✅ Non-static → Static: **SELALU BISA**
- ❌ Static → Non-static: **TIDAK BISA** (kecuali buat object dulu)

Jadi kalau kamu punya utility static methods, kamu bisa dengan bebas memanggilnya dari instance methods! 👍