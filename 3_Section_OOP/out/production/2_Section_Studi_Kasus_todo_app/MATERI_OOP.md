# Materi Lengkap Object-Oriented Programming (OOP) Java

## 1. Konsep Dasar OOP

### 1.1 Class dan Object
**Class** adalah blueprint atau template untuk membuat object. **Object** adalah instance dari class.

```java
// Contoh Class
public class Mobil {
    // Attributes/Fields
    String merk;
    String warna;
    int tahun;
    
    // Constructor
    public Mobil(String merk, String warna, int tahun) {
        this.merk = merk;
        this.warna = warna;
        this.tahun = tahun;
    }
    
    // Method
    public void info() {
        System.out.println("Mobil: " + merk + ", Warna: " + warna + ", Tahun: " + tahun);
    }
}

// Membuat Object
Mobil mobil1 = new Mobil("Toyota", "Hitam", 2023);
mobil1.info();
```

### 1.2 Constructor
Constructor adalah method khusus yang dipanggil saat object dibuat.

```java
public class Mahasiswa {
    String nama;
    int umur;
    
    // Default Constructor
    public Mahasiswa() {
        nama = "Unknown";
        umur = 0;
    }
    
    // Parameterized Constructor
    public Mahasiswa(String nama, int umur) {
        this.nama = nama;
        this.umur = umur;
    }
    
    // Constructor Overloading
    public Mahasiswa(String nama) {
        this.nama = nama;
        this.umur = 18;
    }
}
```

## 2. Empat Pilar OOP

### 2.1 ENCAPSULATION (Enkapsulasi)
Menyembunyikan data dengan menggunakan access modifier dan menyediakan getter/setter.

```java
public class BankAccount {
    // Private attributes (tidak bisa diakses langsung dari luar)
    private String accountNumber;
    private double balance;
    
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    // Getter
    public double getBalance() {
        return balance;
    }
    
    // Setter dengan validasi
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }
}
```

**Access Modifiers:**
- `private`: Hanya bisa diakses dalam class yang sama
- `default` (no modifier): Bisa diakses dalam package yang sama
- `protected`: Bisa diakses dalam package yang sama dan subclass
- `public`: Bisa diakses dari mana saja

### 2.2 INHERITANCE (Pewarisan)
Child class mewarisi properties dan methods dari parent class.

```java
// Parent Class
public class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public void eat() {
        System.out.println(name + " sedang makan");
    }
    
    public void sleep() {
        System.out.println(name + " sedang tidur");
    }
}

// Child Class
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age); // Memanggil constructor parent
        this.breed = breed;
    }
    
    // Method tambahan khusus Dog
    public void bark() {
        System.out.println(name + " menggonggong: Woof! Woof!");
    }
    
    // Method Overriding
    @Override
    public void eat() {
        System.out.println(name + " makan dog food");
    }
}

// Penggunaan
Dog anjing = new Dog("Buddy", 3, "Golden Retriever");
anjing.eat();    // Output: Buddy makan dog food
anjing.bark();   // Output: Buddy menggonggong: Woof! Woof!
```

**Keyword `super`:**
- `super()`: Memanggil constructor parent class
- `super.method()`: Memanggil method parent class

**Keyword `this`:**
- `this.attribute`: Merujuk ke attribute dari object saat ini
- `this()`: Memanggil constructor lain dalam class yang sama

### 2.3 POLYMORPHISM (Polimorfisme)
Kemampuan object untuk mengambil banyak bentuk.

#### A. Compile-Time Polymorphism (Method Overloading)
```java
public class Calculator {
    // Method overloading - nama sama, parameter beda
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
}
```

#### B. Runtime Polymorphism (Method Overriding)
```java
public class Shape {
    public void draw() {
        System.out.println("Menggambar shape");
    }
    
    public double getArea() {
        return 0;
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println("Menggambar lingkaran");
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw() {
        System.out.println("Menggambar persegi panjang");
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
}

// Penggunaan Polymorphism
Shape shape1 = new Circle(5);
Shape shape2 = new Rectangle(4, 6);

shape1.draw();  // Output: Menggambar lingkaran
shape2.draw();  // Output: Menggambar persegi panjang

System.out.println(shape1.getArea()); // Menghitung luas lingkaran
System.out.println(shape2.getArea()); // Menghitung luas persegi panjang
```

### 2.4 ABSTRACTION (Abstraksi)
Menyembunyikan detail implementasi dan hanya menampilkan functionality.

#### A. Abstract Class
```java
public abstract class Vehicle {
    protected String brand;
    
    public Vehicle(String brand) {
        this.brand = brand;
    }
    
    // Abstract method (harus diimplementasi oleh subclass)
    public abstract void start();
    public abstract void stop();
    
    // Concrete method (sudah ada implementasi)
    public void showBrand() {
        System.out.println("Brand: " + brand);
    }
}

public class Car extends Vehicle {
    public Car(String brand) {
        super(brand);
    }
    
    @Override
    public void start() {
        System.out.println(brand + " mobil dinyalakan dengan kunci");
    }
    
    @Override
    public void stop() {
        System.out.println(brand + " mobil dimatikan");
    }
}

public class Motorcycle extends Vehicle {
    public Motorcycle(String brand) {
        super(brand);
    }
    
    @Override
    public void start() {
        System.out.println(brand + " motor dinyalakan dengan kick starter");
    }
    
    @Override
    public void stop() {
        System.out.println(brand + " motor dimatikan");
    }
}
```

**Karakteristik Abstract Class:**
- Tidak bisa diinstansiasi langsung
- Bisa memiliki abstract method dan concrete method
- Bisa memiliki constructor
- Bisa memiliki member variables
- Class yang extends abstract class harus implement semua abstract method

#### B. Interface
```java
public interface Flyable {
    // Constant (public static final by default)
    int MAX_ALTITUDE = 10000;
    
    // Abstract method (public abstract by default)
    void fly();
    void land();
}

public interface Swimmable {
    void swim();
}

// Class bisa implement multiple interfaces
public class Duck extends Animal implements Flyable, Swimmable {
    public Duck(String name, int age) {
        super(name, age);
    }
    
    @Override
    public void fly() {
        System.out.println(name + " terbang rendah");
    }
    
    @Override
    public void land() {
        System.out.println(name + " mendarat di air");
    }
    
    @Override
    public void swim() {
        System.out.println(name + " berenang");
    }
}

// Interface bisa extends interface lain
public interface AdvancedFlyable extends Flyable {
    void soar(); // Terbang tinggi
}
```

**Karakteristik Interface:**
- Semua method secara default abstract dan public
- Semua variable secara default public static final
- Tidak bisa memiliki constructor
- Tidak bisa memiliki instance variables
- Class bisa implement multiple interfaces
- Sejak Java 8, bisa memiliki default method dan static method

```java
public interface Payment {
    void processPayment(double amount);
    
    // Default method (Java 8+)
    default void printReceipt() {
        System.out.println("Payment berhasil");
    }
    
    // Static method (Java 8+)
    static void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount harus positif");
        }
    }
}
```

**Perbedaan Abstract Class vs Interface:**
| Abstract Class | Interface |
|----------------|-----------|
| Single inheritance | Multiple implementation |
| Bisa punya constructor | Tidak bisa punya constructor |
| Bisa punya instance variables | Hanya constant (final) |
| Bisa punya method dengan body | Semua method abstract (kecuali default/static di Java 8+) |
| Menggunakan `extends` | Menggunakan `implements` |

## 3. Konsep Lanjutan

### 3.1 Static Keyword
```java
public class Counter {
    // Static variable - shared oleh semua instance
    public static int count = 0;
    
    // Instance variable
    public int instanceCount = 0;
    
    public Counter() {
        count++;          // Semua instance berbagi variable ini
        instanceCount++;  // Setiap instance punya variable sendiri
    }
    
    // Static method - bisa dipanggil tanpa membuat object
    public static void showCount() {
        System.out.println("Total objects: " + count);
        // System.out.println(instanceCount); // ERROR: tidak bisa akses non-static dari static
    }
    
    // Static block - dijalankan sekali saat class dimuat
    static {
        System.out.println("Counter class dimuat");
        count = 0;
    }
}

// Penggunaan
Counter.showCount(); // Memanggil static method tanpa object
Counter c1 = new Counter();
Counter c2 = new Counter();
Counter.showCount(); // Output: Total objects: 2
```

### 3.2 Final Keyword
```java
// Final class - tidak bisa diextends
public final class ImmutableClass {
    // Final variable - nilai tidak bisa diubah (constant)
    public final int MAX_SIZE = 100;
    private final String name;
    
    public ImmutableClass(String name) {
        this.name = name; // Hanya bisa diset sekali
    }
    
    // Final method - tidak bisa dioverride
    public final void display() {
        System.out.println("Name: " + name);
    }
}

// public class ChildClass extends ImmutableClass {} // ERROR: tidak bisa extends final class
```

### 3.3 Nested Class
```java
public class OuterClass {
    private int outerData = 10;
    
    // Inner class (non-static)
    public class InnerClass {
        public void display() {
            System.out.println("Outer data: " + outerData); // Bisa akses member outer
        }
    }
    
    // Static nested class
    public static class StaticNestedClass {
        public void display() {
            // System.out.println(outerData); // ERROR: tidak bisa akses non-static member
            System.out.println("Static nested class");
        }
    }
    
    // Method dengan local inner class
    public void method() {
        // Local inner class
        class LocalClass {
            void display() {
                System.out.println("Local class");
            }
        }
        
        LocalClass local = new LocalClass();
        local.display();
    }
    
    // Anonymous inner class
    public void anonymousExample() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous inner class");
            }
        };
    }
}

// Penggunaan
OuterClass outer = new OuterClass();
OuterClass.InnerClass inner = outer.new InnerClass(); // Butuh instance outer
OuterClass.StaticNestedClass nested = new OuterClass.StaticNestedClass(); // Tidak butuh instance outer
```

### 3.4 Object Class Methods
Semua class di Java secara implisit extends dari `Object` class.

```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Override toString()
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
    
    // Override equals()
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && name.equals(person.name);
    }
    
    // Override hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
```

### 3.5 Instanceof Operator
```java
Animal animal = new Dog("Buddy", 3, "Bulldog");

if (animal instanceof Dog) {
    Dog dog = (Dog) animal; // Type casting aman
    dog.bark();
}

if (animal instanceof Animal) {
    System.out.println("Ini adalah Animal"); // true
}
```

## 4. Exception Handling dalam OOP

```java
public class BankAccount {
    private double balance;
    
    // Custom Exception
    public class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }
    
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Saldo tidak cukup. Saldo: " + balance);
        }
        balance -= amount;
    }
    
    public void transfer(BankAccount target, double amount) {
        try {
            this.withdraw(amount);
            target.deposit(amount);
            System.out.println("Transfer berhasil");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer gagal: " + e.getMessage());
        } finally {
            System.out.println("Transaksi selesai");
        }
    }
    
    public void deposit(double amount) {
        balance += amount;
    }
}
```

## 5. Design Patterns dalam OOP

### 5.1 Singleton Pattern
```java
public class Database {
    // Private static instance
    private static Database instance;
    
    // Private constructor
    private Database() {
        System.out.println("Database connection created");
    }
    
    // Public method untuk mendapatkan instance
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    public void query(String sql) {
        System.out.println("Executing: " + sql);
    }
}

// Penggunaan
Database db1 = Database.getInstance();
Database db2 = Database.getInstance();
// db1 dan db2 adalah object yang sama
```

### 5.2 Factory Pattern
```java
public interface Shape {
    void draw();
}

public class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Circle");
    }
}

public class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Rectangle");
    }
}

public class ShapeFactory {
    public Shape getShape(String shapeType) {
        if (shapeType == null) {
            return null;
        }
        if (shapeType.equalsIgnoreCase("CIRCLE")) {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("RECTANGLE")) {
            return new Rectangle();
        }
        return null;
    }
}

// Penggunaan
ShapeFactory factory = new ShapeFactory();
Shape shape1 = factory.getShape("CIRCLE");
shape1.draw();
```

---

# Pertanyaan yang Sering Ditanyakan tentang OOP Java

## Pertanyaan Konsep Dasar

**1. Apa perbedaan antara class dan object?**
- Class adalah blueprint/template, object adalah instance dari class
- Class didefinisikan sekali, object bisa dibuat berkali-kali
- Contoh: Class "Mobil" adalah desain, object adalah mobil konkret seperti "Toyota Avanza saya"

**2. Apa itu constructor? Apa bedanya dengan method biasa?**
- Constructor adalah method khusus yang dipanggil saat object dibuat
- Nama constructor harus sama dengan nama class
- Constructor tidak memiliki return type
- Constructor dipanggil otomatis saat menggunakan keyword `new`

**3. Apa perbedaan method overloading dan method overriding?**
- **Overloading**: Method dengan nama sama tapi parameter berbeda (compile-time polymorphism)
- **Overriding**: Child class menulis ulang method dari parent class (runtime polymorphism)

**4. Kapan menggunakan `this` dan `super`?**
- `this`: Merujuk ke object saat ini, membedakan instance variable dengan parameter
- `super`: Merujuk ke parent class, memanggil constructor atau method parent

**5. Apa perbedaan abstract class dan interface?**
- Abstract class: bisa punya concrete method, constructor, instance variable; single inheritance
- Interface: semua method abstract (kecuali default), tidak bisa punya constructor; multiple implementation
- Gunakan abstract class untuk "is-a" relationship, interface untuk "can-do" relationship

## Pertanyaan Encapsulation

**6. Mengapa perlu encapsulation?**
- Melindungi data dari akses langsung yang tidak valid
- Kontrol penuh terhadap data melalui getter/setter
- Fleksibilitas untuk mengubah implementasi tanpa mengubah interface

**7. Kapan menggunakan private, protected, dan public?**
- `private`: Data sensitif yang hanya untuk internal class
- `protected`: Data yang perlu diakses oleh subclass
- `public`: API yang boleh diakses dari mana saja

**8. Apa itu getter dan setter? Apakah selalu diperlukan?**
- Getter: Method untuk mengambil nilai private variable
- Setter: Method untuk mengubah nilai dengan validasi
- Tidak selalu perlu jika data bersifat immutable atau tidak perlu akses dari luar

## Pertanyaan Inheritance

**9. Apa keuntungan dan kerugian inheritance?**
- **Keuntungan**: Code reuse, hierarki yang jelas, polymorphism
- **Kerugian**: Tight coupling, bisa jadi kompleks, melanggar encapsulation jika tidak hati-hati

**10. Bisakah Java melakukan multiple inheritance?**
- Tidak untuk class (menghindari diamond problem)
- Ya untuk interface (bisa implements multiple interfaces)

**11. Apa itu method overriding? Apa syaratnya?**
- Child class menulis ulang method parent dengan implementasi berbeda
- Syarat: Nama method, parameter, dan return type harus sama
- Access modifier bisa sama atau lebih luas, tidak boleh lebih ketat

**12. Mengapa constructor tidak diwariskan?**
- Constructor khusus untuk inisialisasi class tertentu
- Setiap class perlu constructor sendiri
- Gunakan `super()` untuk memanggil constructor parent

## Pertanyaan Polymorphism

**13. Apa itu upcasting dan downcasting?**
- **Upcasting**: Child class ke Parent class (implisit, aman)
  ```java
  Animal animal = new Dog(); // Upcasting
  ```
- **Downcasting**: Parent class ke Child class (eksplisit, perlu casting)
  ```java
  Dog dog = (Dog) animal; // Downcasting
  ```

**14. Apa gunanya polymorphism?**
- Fleksibilitas: Satu interface untuk berbagai implementasi
- Extensibility: Mudah menambah tipe baru tanpa mengubah kode lama
- Code yang lebih clean dan maintainable

**15. Apa itu dynamic binding?**
- Method yang dipanggil ditentukan saat runtime berdasarkan tipe object sebenarnya
- Memungkinkan runtime polymorphism

## Pertanyaan Static dan Final

**16. Kapan menggunakan static keyword?**
- Variable: Data yang shared oleh semua instance (counter, constant)
- Method: Utility method yang tidak perlu instance (Math.sqrt())
- Block: Inisialisasi static variable
- Class: Nested class yang tidak perlu akses ke outer class

**17. Apa perbedaan static method dan instance method?**
- Static method: Dipanggil dari class, tidak bisa akses instance variable
- Instance method: Dipanggil dari object, bisa akses semua member

**18. Mengapa main method harus static?**
- JVM perlu memanggil main() tanpa membuat object terlebih dahulu
- Entry point program yang universal

**19. Kapan menggunakan final keyword?**
- Variable: Membuat constant
- Method: Mencegah overriding
- Class: Mencegah inheritance (untuk security atau design)

## Pertanyaan Interface

**20. Kapan menggunakan interface vs abstract class?**
- **Interface**: Mendefinisikan contract/capability (Flyable, Serializable)
- **Abstract class**: Base class dengan common functionality

**21. Apa itu marker interface?**
- Interface tanpa method (Serializable, Cloneable)
- Menandai class memiliki properti/capability tertentu

**22. Apa itu functional interface?**
- Interface dengan hanya satu abstract method
- Bisa digunakan dengan lambda expression (Java 8+)
- Contoh: Runnable, Callable, Comparator

**23. Apa itu default method di interface?**
- Method dengan implementasi di interface (Java 8+)
- Memungkinkan menambah method baru tanpa break existing implementation
- Mengatasi masalah multiple inheritance

## Pertanyaan Lanjutan

**24. Apa itu composition vs inheritance?**
- **Inheritance**: "is-a" relationship (Dog is an Animal)
- **Composition**: "has-a" relationship (Car has an Engine)
- Composition lebih fleksibel, prefer composition over inheritance

**25. Apa itu immutable class? Bagaimana membuatnya?**
- Class yang objectnya tidak bisa diubah setelah dibuat
- Cara: Final class, final fields, no setters, defensive copy
- Contoh: String, Integer

**26. Apa itu coupling dan cohesion?**
- **Coupling**: Ketergantungan antar class (low coupling = baik)
- **Cohesion**: Seberapa fokus class pada satu tanggung jawab (high cohesion = baik)

**27. Apa prinsip SOLID?**
- **S**ingle Responsibility: Satu class satu tanggung jawab
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subclass bisa menggantikan parent class
- **I**nterface Segregation: Interface kecil dan spesifik
- **D**ependency Inversion: Depend on abstraction, not concrete

**28. Apa itu shallow copy vs deep copy?**
- **Shallow copy**: Copy reference, object yang sama
- **Deep copy**: Copy object baru, independen
- Implementasi: Clone method atau copy constructor

**29. Mengapa equals() dan hashCode() harus di-override bersama?**
- Contract: Object yang equals harus punya hashCode sama
- Penting untuk Collection (HashMap, HashSet)
- Inkonsistensi menyebabkan bug di Collection

**30. Apa itu reflection dalam Java?**
- Kemampuan inspect dan manipulasi class/object saat runtime
- Mendapatkan informasi class, method, field secara dynamic
- Digunakan di framework (Spring, Hibernate)

---

## Tips Belajar OOP

1. **Praktik dengan project nyata** - Buat aplikasi kecil seperti sistem perpustakaan, bank, atau game
2. **Gambar diagram UML** - Visualisasi class dan relationship membantu pemahaman
3. **Refactor code lama** - Perbaiki code dengan apply OOP principles
4. **Baca code orang lain** - Pelajari dari open source project
5. **Pahami SOLID principles** - Foundation untuk clean code
6. **Jangan over-engineering** - Mulai simple, refactor saat perlu
7. **Practice design patterns** - Pelajari solusi umum untuk masalah yang sering muncul