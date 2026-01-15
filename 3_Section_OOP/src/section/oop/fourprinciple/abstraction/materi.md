# Abstraction dalam OOP - Panduan Lengkap

Saya akan membantu kamu menguasai konsep abstraction secara mendalam untuk interview dan praktik sehari-hari.

## Apa itu Abstraction?

Abstraction adalah proses **menyembunyikan detail implementasi yang kompleks** dan **hanya menampilkan fungsionalitas penting** kepada pengguna. Seperti ketika kamu mengendarai mobil - kamu hanya perlu tahu cara menggunakan setir, pedal gas, dan rem, tanpa perlu tahu bagaimana mesin bekerja di dalamnya.

## Konsep Inti Abstraction

### 1. **Interface vs Abstract Class**

**Abstract Class:**
- Bisa punya method dengan implementasi DAN method abstract (tanpa implementasi)
- Bisa punya state/fields
- Menggunakan single inheritance
- Untuk "IS-A" relationship

**Interface:**
- Hanya kontrak/blueprint (di Java modern bisa ada default method)
- Tidak bisa punya state (kecuali constants)
- Bisa multiple implementation
- Untuk "CAN-DO" relationship

## Rules & Best Practices

### **Rule 1: Abstract Class tidak bisa di-instantiate**
```java
// ❌ SALAH
PaymentMethod payment = new PaymentMethod();

// ✅ BENAR
PaymentMethod payment = new CreditCard();
```

### **Rule 2: Minimal 1 method abstract di abstract class**
Kalau tidak ada method abstract, seharusnya itu class biasa, bukan abstract.

### **Rule 3: Child class WAJIB implement semua abstract methods**
Kecuali child class juga abstract.

### **Rule 4: Interface = kontrak yang harus dipenuhi**
Semua method di interface harus diimplementasikan.

## Best Practices dengan Contoh Real-World

### **1. Program to Interface, Not Implementation**

```java
// ❌ BAD - Terikat ke implementasi spesifik
ArrayList<String> names = new ArrayList<>();

// ✅ GOOD - Flexible, bisa ganti implementasi
List<String> names = new ArrayList<>();
// Bisa diganti ke LinkedList kapan saja tanpa ubah kode lain
```

### **2. Gunakan Abstract Class untuk Shared Behavior**

```java
// Template untuk semua payment methods
abstract class PaymentMethod {
    protected String transactionId;

    // Shared behavior - semua payment butuh ini
    public void generateTransactionId() {
        this.transactionId = UUID.randomUUID().toString();
    }

    // Setiap payment method punya cara bayar berbeda
    public abstract boolean processPayment(double amount);

    // Template method pattern
    public final void pay(double amount) {
        generateTransactionId();
        if (processPayment(amount)) {
            sendReceipt();
        }
    }

    protected abstract void sendReceipt();
}

class CreditCard extends PaymentMethod {
    private String cardNumber;

    @Override
    public boolean processPayment(double amount) {
        // Logika spesifik credit card
        return chargeCreditCard(cardNumber, amount);
    }

    @Override
    protected void sendReceipt() {
        System.out.println("Receipt sent via email");
    }
}

class EWallet extends PaymentMethod {
    private String phoneNumber;

    @Override
    public boolean processPayment(double amount) {
        // Logika spesifik e-wallet
        return deductBalance(phoneNumber, amount);
    }

    @Override
    protected void sendReceipt() {
        System.out.println("Receipt sent via app notification");
    }
}
```

### **3. Gunakan Interface untuk Multiple Capabilities**

```java
// Berbagai capability yang bisa dikombinasikan
interface Printable {
    void print();
}

interface Scannable {
    void scan();
}

interface Faxable {
    void fax();
}

// All-in-one printer
class MultiFunctionPrinter implements Printable, Scannable, Faxable {
    public void print() { /* ... */ }
    public void scan() { /* ... */ }
    public void fax() { /* ... */ }
}

// Simple printer - hanya implement yang dibutuhkan
class SimplePrinter implements Printable {
    public void print() { /* ... */ }
}
```

### **4. Interface Segregation (SOLID Principle)**

```java
// ❌ BAD - Fat interface
interface Worker {
    void work();
    void eat();
    void sleep();
}

// Robot harus implement eat() dan sleep() padahal tidak butuh

// ✅ GOOD - Segregated interfaces
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Human implements Workable, Eatable, Sleepable {
    // Implement semua
}

class Robot implements Workable {
    // Hanya implement work()
}
```

## Contoh Kompleks: Notification System

```java
// Abstract class untuk shared logic
abstract class Notification {
    protected String recipient;
    protected String message;

    public Notification(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
    }

    // Template method
    public final void send() {
        if (validate()) {
            formatMessage();
            deliver();
            log();
        }
    }

    protected boolean validate() {
        return recipient != null && message != null;
    }

    protected abstract void formatMessage();
    protected abstract void deliver();

    protected void log() {
        System.out.println("Notification sent to: " + recipient);
    }
}

// Interface untuk additional capabilities
interface Schedulable {
    void schedule(LocalDateTime time);
}

interface Retryable {
    void retry(int maxAttempts);
}

// Concrete implementations
class EmailNotification extends Notification implements Schedulable {
    public EmailNotification(String email, String message) {
        super(email, message);
    }

    @Override
    protected void formatMessage() {
        this.message = "<html>" + message + "</html>";
    }

    @Override
    protected void deliver() {
        // Send email logic
    }

    @Override
    public void schedule(LocalDateTime time) {
        // Schedule email
    }
}

class SMSNotification extends Notification implements Retryable {
    public SMSNotification(String phone, String message) {
        super(phone, message);
    }

    @Override
    protected void formatMessage() {
        // Truncate to 160 characters
        if (message.length() > 160) {
            this.message = message.substring(0, 160);
        }
    }

    @Override
    protected void deliver() {
        // Send SMS logic
    }

    @Override
    public void retry(int maxAttempts) {
        // Retry logic
    }
}
```

## Pertanyaan Interview yang Sering Muncul

### **Q1: Kapan pakai Abstract Class vs Interface?**

**Abstract Class:**
- Ada shared code/behavior
- Hubungan "IS-A" yang kuat
- Butuh fields/state
- Contoh: Animal → Dog, Cat

**Interface:**
- Pure contract/capability
- Multiple inheritance needed
- "CAN-DO" relationship
- Contoh: Flyable, Swimmable

### **Q2: Bisakah abstract class tanpa abstract method?**
Secara teknis bisa, tapi **tidak recommended**. Kalau tidak ada abstract method, seharusnya itu class biasa. Tujuan abstract class adalah memaksa child class implement method tertentu.

### **Q3: Mengapa tidak bisa instantiate abstract class?**
Karena abstract class **tidak lengkap** - ada method yang belum diimplementasikan. Seperti blueprint rumah yang belum jadi rumah sungguhan.

### **Q4: Multiple inheritance di Java?**
- Class: **TIDAK BISA** multiple inheritance (Diamond Problem)
- Interface: **BISA** multiple implementation

### **Q5: Perbedaan Abstraction vs Encapsulation?**
- **Abstraction**: Menyembunyikan kompleksitas, fokus ke "APA yang dilakukan"
- **Encapsulation**: Menyembunyikan data, fokus ke "BAGAIMANA data dilindungi"

## Tips Interview

1. **Selalu beri contoh real-world** - seperti payment system, notification system
2. **Hubungkan dengan SOLID principles** - terutama Interface Segregation
3. **Tahu trade-offs** - kapan pakai abstract class vs interface
4. **Praktik coding** - buat mini project dengan abstraction
5. **Pahami design patterns** - Strategy, Template Method, Factory menggunakan abstraction

## Latihan Coding Challenge

Coba buat sendiri:
1. **Vehicle Management System** - abstract Vehicle class dengan Car, Motorcycle, Truck
2. **Database Connection Pool** - abstract Connection dengan MySQL, PostgreSQL implementations
3. **File Storage System** - interface FileStorage dengan LocalStorage, CloudStorage, S3Storage

Kunci abstraction yang baik: **Hide complexity, show simplicity**. Kamu berhasil kalau orang lain bisa pakai class kamu tanpa perlu tahu detail implementasinya.

Ada bagian yang mau kamu explore lebih dalam lagi?