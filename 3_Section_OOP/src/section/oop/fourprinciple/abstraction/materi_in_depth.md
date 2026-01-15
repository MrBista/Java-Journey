# Abstraction - Panduan SUPER LENGKAP untuk Pemula

Oke, sekarang aku jelasin dari **NOL** sampai **EXPERT** level. Artifact di samping itu kode lengkap yang bisa kamu pelajari sambil baca penjelasan ini.

---

## 🎯 PART 1: Konsep Paling Dasar

### Apa itu Abstraction?

Bayangkan kamu pakai **smartphone**:
- Kamu tekan tombol power → HP nyala
- Kamu buka aplikasi → langsung jalan
- Kamu ambil foto → langsung tersimpan

**Kamu TIDAK perlu tahu:**
- Bagaimana prosesor bekerja
- Bagaimana memori menyimpan data
- Bagaimana kamera sensor menangkap cahaya

Itu adalah **ABSTRACTION** = **Menyembunyikan kompleksitas, menampilkan kesederhanaan**

### Dalam Programming

```java
// Kamu hanya perlu tahu ini:
payment.processPayment();

// TIDAK perlu tahu detail di dalamnya:
// - Validasi kartu kredit
// - Koneksi ke bank
// - Enkripsi data
// - Logging transaksi
```

---

## 🎯 PART 2: Abstract Class - Dari Dasar

### Apa itu Abstract Class?

**Abstract class = Class yang tidak lengkap**

Seperti **blueprint rumah**:
- Ada denah umum (method biasa)
- Ada bagian yang belum detail (abstract method)
- **Tidak bisa langsung ditinggali** (tidak bisa di-instantiate)
- Harus **dibangun dulu** (harus di-extend)

### Karakteristik Abstract Class

```java
abstract class Animal {
    // ✅ Boleh punya field
    protected String name;
    protected int age;
    
    // ✅ Boleh punya constructor
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // ✅ Boleh punya method biasa (concrete method)
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    // ✅ Boleh punya abstract method (WAJIB diimplement anak)
    public abstract void makeSound();
    public abstract void move();
}
```

### Rules Abstract Class:

1. **Tidak bisa dibuat objek langsung**
```java
// ❌ COMPILE ERROR
Animal animal = new Animal("Generic", 5);

// ✅ BENAR - via child class
Animal dog = new Dog("Buddy", 3);
```

2. **Child class WAJIB implement SEMUA abstract method**
```java
class Dog extends Animal {
    public Dog(String name, int age) {
        super(name, age);
    }
    
    // WAJIB implement
    @Override
    public void makeSound() {
        System.out.println("Woof woof!");
    }
    
    @Override
    public void move() {
        System.out.println("Running on four legs");
    }
}
```

3. **Kecuali child class juga abstract**
```java
abstract class Pet extends Animal {
    public Pet(String name, int age) {
        super(name, age);
    }
    
    // Tidak wajib implement abstract method
    // Karena Pet juga abstract
}
```

---

## 🎯 PART 3: Interface - Dari Dasar

### Apa itu Interface?

**Interface = Kontrak/Perjanjian**

Seperti **SIM (Surat Izin Mengemudi)**:
- SIM adalah **kontrak** bahwa kamu bisa mengemudi
- Tapi SIM **tidak mengajarkan** cara mengemudi
- **Cara mengemudi** adalah implementasimu sendiri

### Interface Sebelum Java 8 (Interface Lama)

```java
interface Drivable {
    // Semua method OTOMATIS public abstract
    void startEngine();
    void accelerate();
    void brake();
    void stopEngine();
    
    // Semua variable OTOMATIS public static final (konstanta)
    int MAX_SPEED = 200;
}
```

**Semua method = abstract** (hanya signature, tanpa body)

### Interface Java 8+ (Interface Modern)

Java 8 menambahkan **3 fitur baru**:

1. **Default Method**
2. **Static Method**
3. **Private Method** (Java 9+)

---

## 🎯 PART 4: DEFAULT METHOD - Penjelasan LENGKAP

### Kenapa Butuh Default Method?

**Masalah sebelum Java 8:**

```java
// Interface lama yang sudah dipakai banyak class
interface OldInterface {
    void method1();
    void method2();
}

// Sudah ada 100 class yang implement ini
class ClassA implements OldInterface { ... }
class ClassB implements OldInterface { ... }
// ... 98 class lainnya

// ❌ MASALAH: Kita mau tambah method baru
interface OldInterface {
    void method1();
    void method2();
    void method3(); // BARU
}

// 💥 SEMUA 100 CLASS RUSAK!
// Karena mereka semua WAJIB implement method3()
```

**Solusi: DEFAULT METHOD**

```java
interface ModernInterface {
    void method1();
    void method2();
    
    // ✅ Method baru dengan default implementation
    default void method3() {
        System.out.println("Default implementation");
    }
}

// ✅ SEMUA CLASS TETAP JALAN
// Karena method3() sudah punya implementasi default
```

### Cara Kerja Default Method

```java
interface Vehicle {
    // Abstract method - WAJIB implement
    void start();
    void stop();
    
    // Default method - OPSIONAL override
    default void honk() {
        System.out.println("Beep beep!");
    }
}

class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car started");
    }
    
    @Override
    public void stop() {
        System.out.println("Car stopped");
    }
    
    // TIDAK override honk()
    // Jadi pakai yang default dari interface
}

class Motorcycle implements Vehicle {
    @Override
    public void start() {
        System.out.println("Motorcycle started");
    }
    
    @Override
    public void stop() {
        System.out.println("Motorcycle stopped");
    }
    
    // Override honk dengan custom sound
    @Override
    public void honk() {
        System.out.println("Beep beep beep! (motorcycle)");
    }
}

// Testing
Car car = new Car();
car.honk(); // Output: "Beep beep!" (default)

Motorcycle moto = new Motorcycle();
moto.honk(); // Output: "Beep beep beep! (motorcycle)" (custom)
```

### Default Method Bisa Panggil Abstract Method

```java
interface Printable {
    // Abstract
    String getContent();
    
    // Default method yang pakai abstract method
    default void print() {
        System.out.println("=== Printing Document ===");
        System.out.println(getContent()); // Panggil abstract method
        System.out.println("=== End of Document ===");
    }
}

class Report implements Printable {
    @Override
    public String getContent() {
        return "This is a report content";
    }
    
    // print() sudah disediakan default method
}

// Usage
Report report = new Report();
report.print();
// Output:
// === Printing Document ===
// This is a report content
// === End of Document ===
```

### Memanggil Default Method dari Interface (super)

```java
interface Logger {
    default void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

class CustomLogger implements Logger {
    @Override
    public void log(String message) {
        // Tambah timestamp sebelum log
        String timestamp = LocalDateTime.now().toString();
        System.out.println("[" + timestamp + "]");
        
        // Panggil default method dari interface
        Logger.super.log(message);
    }
}

// Usage
CustomLogger logger = new CustomLogger();
logger.log("Test message");
// Output:
// [2026-01-15T10:30:45]
// [LOG] Test message
```

---

## 🎯 PART 5: STATIC METHOD di Interface

### Karakteristik Static Method

1. **Dipanggil via nama interface** (bukan via objek)
2. **TIDAK bisa di-override**
3. **Tidak diwariskan** ke implementing class

```java
interface MathHelper {
    // Static method
    static int add(int a, int b) {
        return a + b;
    }
    
    static int multiply(int a, int b) {
        return a * b;
    }
    
    // Static method bisa dipanggil oleh default method
    default int square(int num) {
        return multiply(num, num); // Panggil static method
    }
}

// Usage
// ✅ Panggil via nama interface
int result = MathHelper.add(5, 3);
System.out.println(result); // 8

// ❌ TIDAK bisa via objek
class Calculator implements MathHelper {}
Calculator calc = new Calculator();
calc.add(5, 3); // COMPILE ERROR!
```

### Perbedaan Static vs Default

```java
interface Example {
    // Static: dipanggil via Interface
    static void staticMethod() {
        System.out.println("Static method");
    }
    
    // Default: dipanggil via objek
    default void defaultMethod() {
        System.out.println("Default method");
    }
}

// Usage
Example.staticMethod(); // ✅ OK

class MyClass implements Example {}
MyClass obj = new MyClass();
obj.defaultMethod(); // ✅ OK
obj.staticMethod(); // ❌ ERROR
```

---

## 🎯 PART 6: MULTIPLE INTERFACE (Diamond Problem)

### Kenapa Java Allow Multiple Interface?

```java
// ❌ Java TIDAK allow ini (multiple inheritance class)
class A {}
class B {}
class C extends A, B {} // COMPILE ERROR

// ✅ Java ALLOW ini (multiple interface)
interface Flyable {}
interface Swimmable {}
class Duck implements Flyable, Swimmable {} // OK!
```

**Alasannya:**
- Class bisa punya **state** (field) → ambiguitas kalau multiple
- Interface (sebelum Java 8) **tidak punya state** → aman

### Diamond Problem di Interface

```java
interface A {
    default void show() {
        System.out.println("From A");
    }
}

interface B {
    default void show() {
        System.out.println("From B");
    }
}

// ❌ CONFLICT: Kedua interface punya default method sama
class C implements A, B {
    // WAJIB resolve conflict
    @Override
    public void show() {
        // Cara 1: Pilih salah satu
        A.super.show();
        
        // Cara 2: Pilih yang lain
        // B.super.show();
        
        // Cara 3: Custom (recommended)
        // System.out.println("From C");
    }
}
```

### Contoh Real: Duck yang Bisa Fly & Swim

Lihat di artifact bagian `PART 6` untuk contoh lengkap Duck class.

---

## 🎯 PART 7: Abstract Class vs Interface - Kapan Pakai?

### Decision Tree

```
Punya SHARED CODE/BEHAVIOR? 
├─ YA → Abstract Class
│   ├─ Contoh: DatabaseConnection punya openConnection()
│   └─ Contoh: Payment punya generateTransactionId()
│
└─ TIDAK → Interface
    ├─ Define CAPABILITY
    ├─ Contoh: Flyable, Swimmable, Cacheable
    └─ Untuk multiple "can-do"
```

### Perbandingan Detail

| Aspek | Abstract Class | Interface |
|-------|---------------|-----------|
| **Fields** | ✅ Bisa (instance variables) | ❌ Hanya constants |
| **Constructor** | ✅ Bisa | ❌ Tidak bisa |
| **Method Types** | Abstract + Concrete | Abstract + Default + Static |
| **Access Modifier** | public, protected, private | public only (default public) |
| **Multiple** | ❌ Single inheritance | ✅ Multiple implementation |
| **State** | ✅ Bisa punya state | ❌ Stateless |
| **Relationship** | IS-A (Dog IS-A Animal) | CAN-DO (Bird CAN Fly) |

### Contoh Abstract Class (Shared Code)

```java
abstract class Employee {
    // Shared state
    protected String name;
    protected String id;
    protected double baseSalary;
    
    // Shared constructor
    public Employee(String name, String id, double baseSalary) {
        this.name = name;
        this.id = id;
        this.baseSalary = baseSalary;
    }
    
    // Shared behavior
    public void clockIn() {
        System.out.println(name + " clocked in at " + LocalTime.now());
    }
    
    // Abstract - tiap tipe employee hitung bonus berbeda
    public abstract double calculateBonus();
    
    // Template method
    public final double getTotalSalary() {
        return baseSalary + calculateBonus();
    }
}

class Manager extends Employee {
    public Manager(String name, String id, double baseSalary) {
        super(name, id, baseSalary);
    }
    
    @Override
    public double calculateBonus() {
        return baseSalary * 0.2; // 20% bonus
    }
}

class Developer extends Employee {
    public Developer(String name, String id, double baseSalary) {
        super(name, id, baseSalary);
    }
    
    @Override
    public double calculateBonus() {
        return baseSalary * 0.15; // 15% bonus
    }
}
```

### Contoh Interface (Capability)

```java
// Capability 1
interface Exportable {
    void exportToPDF();
    void exportToExcel();
    
    default void export(String format) {
        switch(format) {
            case "PDF": exportToPDF(); break;
            case "Excel": exportToExcel(); break;
            default: System.out.println("Unsupported format");
        }
    }
}

// Capability 2
interface Emailable {
    void sendEmail(String recipient);
}

// Capability 3
interface Printable {
    void print();
}

// Report bisa export, email, dan print
class Report implements Exportable, Emailable, Printable {
    public void exportToPDF() { /* ... */ }
    public void exportToExcel() { /* ... */ }
    public void sendEmail(String recipient) { /* ... */ }
    public void print() { /* ... */ }
}

// Invoice hanya bisa export dan email (tidak bisa print)
class Invoice implements Exportable, Emailable {
    public void exportToPDF() { /* ... */ }
    public void exportToExcel() { /* ... */ }
    public void sendEmail(String recipient) { /* ... */ }
}
```

---

## 🎯 PART 8: Design Patterns dengan Abstraction

### Template Method Pattern

```java
abstract class DataMiner {
    // Template method - defines skeleton
    public final void mine(String path) {
        openFile(path);
        extractData();
        parseData();
        analyzeData();
        sendReport();
        closeFile();
    }
    
    // Common steps
    private void openFile(String path) {
        System.out.println("Opening file: " + path);
    }
    
    private void closeFile() {
        System.out.println("Closing file");
    }
    
    // Abstract steps - subclass decides
    protected abstract void extractData();
    protected abstract void parseData();
    
    // Hook - subclass can override
    protected void analyzeData() {
        System.out.println("Default analysis");
    }
    
    protected abstract void sendReport();
}

class PDFDataMiner extends DataMiner {
    protected void extractData() {
        System.out.println("Extracting PDF data");
    }
    
    protected void parseData() {
        System.out.println("Parsing PDF format");
    }
    
    protected void sendReport() {
        System.out.println("Sending PDF report");
    }
}

class CSVDataMiner extends DataMiner {
    protected void extractData() {
        System.out.println("Extracting CSV data");
    }
    
    protected void parseData() {
        System.out.println("Parsing CSV format");
    }
    
    protected void sendReport() {
        System.out.println("Sending CSV report");
    }
    
    // Override hook
    protected void analyzeData() {
        System.out.println("Advanced CSV analysis");
    }
}
```

### Strategy Pattern with Interface

```java
interface PaymentStrategy {
    boolean pay(double amount);
    
    default void displayPaymentInfo() {
        System.out.println("Payment method: " + getMethodName());
    }
    
    String getMethodName();
}

class CreditCardStrategy implements PaymentStrategy {
    private String cardNumber;
    
    public CreditCardStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card");
        return true;
    }
    
    public String getMethodName() {
        return "Credit Card";
    }
}

class PayPalStrategy implements PaymentStrategy {
    private String email;
    
    public PayPalStrategy(String email) {
        this.email = email;
    }
    
    public boolean pay(double amount) {
        System.out.println("Paid " + amount + " using PayPal");
        return true;
    }
    
    public String getMethodName() {
        return "PayPal";
    }
}

// Context
class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout(double amount) {
        paymentStrategy.displayPaymentInfo();
        paymentStrategy.pay(amount);
    }
}

// Usage
ShoppingCart cart = new ShoppingCart();
cart.setPaymentStrategy(new CreditCardStrategy("1234-5678"));
cart.checkout(100.0);

cart.setPaymentStrategy(new PayPalStrategy("user@email.com"));
cart.checkout(200.0);
```

---

## 🎯 PART 9: Private Method di Interface (Java 9+)

```java
interface Calculator {
    // Public abstract
    int add(int a, int b);
    int subtract(int a, int b);
    
    // Default method
    default void calculate(int a, int b) {
        logOperation("add", a, b);
        int sum = add(a, b);
        
        logOperation("subtract", a, b);
        int diff = subtract(a, b);
        
        displayResults(sum, diff);
    }
    
    // Private helper method (Java 9+)
    private void logOperation(String op, int a, int b) {
        System.out.println("Performing " + op + " on " + a + " and " + b);
    }
    
    private void displayResults(int... results) {
        System.out.println("Results: " + Arrays.toString(results));
    }
}
```

**Keuntungan private method:**
- Code reuse di dalam interface
- Tidak expose ke implementing class
- Keep interface clean

---

## 🎯 PART 10: Best Practices & Common Mistakes

### ✅ DO's

1. **Interface untuk contract, Abstract untuk shared code**
```java
// ✅ GOOD
interface Saveable {
    void save();
}

abstract class Document {
    protected String content;
    
    public void setContent(String content) {
        this.content = content;
    }
}
```

2. **Nama interface yang jelas**
```java
// ✅ GOOD
interface Readable {}
interface Writable {}
interface Closeable {}

// ❌ BAD
interface IData {} // Jangan pakai prefix I
interface DataInterface {} // Jangan pakai suffix Interface
```

3. **Default method untuk backward compatibility**
```java
interface OldAPI {
    void oldMethod();
    
    // Tambah method baru tanpa break existing code
    default void newMethod() {
        System.out.println("Default implementation");
    }
}
```

### ❌ DON'Ts

1. **Jangan abstract class kalau tidak ada shared code**
```java
// ❌ BAD - harusnya interface
abstract class Clickable {
    public abstract void onClick();
}

// ✅ GOOD
interface Clickable {
    void onClick();
}
```

2. **Jangan terlalu banyak abstract method**
```java
// ❌ BAD - terlalu banyak, susah maintain
abstract class ComplexClass {
    public abstract void method1();
    public abstract void method2();
    public abstract void method3();
    // ... 20 abstract methods
}

// ✅ GOOD - pecah jadi lebih kecil
abstract class SimpleClass1 {
    public abstract void method1();
    public abstract void method2();
}

abstract class SimpleClass2 {
    public abstract void method3();
    public abstract void method4();
}
```

3. **Jangan fields di interface untuk mutable state**
```java
// ❌ BAD - field di interface harus constant
interface Config {
    String API_URL = "https://api.example.com"; // OK - constant
}

// ✅ GOOD - state di abstract class
abstract class Config {
    protected String apiUrl; // Mutable state
}
```

---

## 🎯 PART 11: Pertanyaan Interview LENGKAP

### Q1: Jelaskan perbedaan Abstract Class vs Interface

**Jawaban:**
- **Abstract Class**: Ketika ada shared behavior/code yang perlu diwariskan. Contoh: `Animal` punya method `sleep()` yang sama untuk semua hewan
- **Interface**: Ketika define capability/contract tanpa implementation. Contoh: `Flyable` untuk benda yang bisa terbang

**Follow-up**: Berikan contoh kapan pakai masing-masing
- Abstract: DatabaseConnection (shared: openConnection, closeConnection)
- Interface: Serializable, Comparable, Runnable (pure contract)

### Q2: Apa itu default method? Mengapa ditambahkan di Java 8?

**Jawaban:**
Default method memungkinkan menambah method baru ke interface tanpa break existing implementations. Contoh:

```java
interface List {
    void add(Object o);
    // Java 8 tambah:
    default void sort(Comparator c) {
        // implementation
    }
}
```

Tanpa default method, semua class yang implement `List` akan error karena wajib implement `sort()`.

### Q3: Bisakah interface punya constructor?

**Jawaban:** Tidak, karena:
1. Interface tidak bisa di-instantiate
2. Interface tidak punya state/fields (kecuali constants)
3. Constructor untuk initialize state, tapi interface tidak punya state

### Q4: Bisakah abstract method punya body {}?

**Jawaban:** Tidak. Abstract method hanya signature. Kalau ada body, itu concrete method.

```java
// ❌ COMPILE ERROR
public abstract void method() {
    System.out.println("test");
}

// ✅ CORRECT
public abstract void method();
```

### Q5: Diamond problem di Java?

**Jawaban:**
Java tidak allow multiple inheritance untuk class (diamond problem), tapi allow multiple interface. Kalau ada conflict di default method, wajib resolve:

```java
class C implements A, B {
    public void method() {
        A.super.method(); // Pilih A
        // atau B.super.method(); // Pilih B
        // atau custom implementation
    }
}
```

### Q6: Bisakah override static method?

**Jawaban:** Tidak. Static method bukan polymorphic. Itu method hiding, bukan overriding.

### Q7: Final vs Abstract method?

**Jawaban:**
- **final**: Tidak bisa di-override (implementation fixed)
- **abstract**: WAJIB di-override (no implementation)
- Tidak bisa `final abstract` → contradiction

### Q8: Kapan pakai abstract class tanpa abstract method?

**Jawaban:** Jarang, tapi valid kalau:
1. Prevent direct instantiation
2. Force subclassing
3. Contoh: `HttpServlet` di Java - bisa di-extend tapi tidak wajib override method tertentu

### Q9: Multiple inheritance di Java?

**Jawaban:**
- Class: Tidak (single inheritance only)
- Interface: Ya (multiple implementation)

Reason: Menghindari diamond problem dan ambiguitas state.

### Q10: Abstract class bisa implement interface?

**Jawaban:** Ya, dan tidak wajib implement semua method.

```java
interface A {
    void method1();
    void method2();
}

abstract class B implements A {
    // Boleh implement sebagian
    public void method1() { }
    // method2() masih abstract
}

class C extends B {
    // Wajib implement method2()
    public void method2() { }
}
```

---

## 🎯 PART 12: Latihan Praktis

### Challenge 1: E-Commerce Notification System

Buat system dengan:
- Abstract class `Notification` (shared: validate, log)
- Interface `Schedulable`, `Retryable`
- Implement: `EmailNotification`, `SMSNotification`, `PushNotification`

### Challenge 2: File Storage System

Buat:
- Interface `FileStorage` (upload, download, delete)
- Default method: `exists()`, `getSize()`
- Implement: `LocalStorage`, `CloudStorage`, `S3Storage`

### Challenge 3: Game Character System

Buat:
- Abstract class `Character` (name, health, attack, defend)
- Interface `Flyable`, `Swimmable`, `Teleportable`
- Implement: `Warrior`, `Mage`, `Dragon`

---

## 📚 Kesimpulan

**Abstraction adalah tentang:**
1. **Menyembunyikan kompleksitas** - User tidak perlu tahu detail
2. **Fokus pada "APA"** - bukan "BAGAIMANA"
3. **Contract/Blueprint** - Interface & Abstract Class
4. **Flexibility** - Mudah extend dan maintain

**Ingat:**
- Abstract Class = Shared code + "IS-A"
- Interface = Contract + "CAN-DO"
- Default Method = Backward compatibility
- Multiple Interface = Combine capabilities

Study artifact lengkapnya, run kodenya, dan modifikasi untuk deep understanding! 🚀

Ada yang masih bingung atau mau tanya lebih detail tentang bagian tertentu?