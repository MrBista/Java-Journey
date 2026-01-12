Baik, saya akan menjelaskan Polymorphism di Java secara mendalam untuk persiapan interview Anda.

## Pengertian Polymorphism

Polymorphism (dari bahasa Yunani: "poly" = banyak, "morph" = bentuk) adalah kemampuan suatu objek untuk mengambil banyak bentuk. Dalam Java, ini berarti referensi dari suatu tipe dapat merujuk ke objek dari berbagai tipe yang berbeda.

## Dua Jenis Polymorphism di Java

### 1. Compile-time Polymorphism (Static Polymorphism)
Diselesaikan pada saat kompilasi melalui **Method Overloading** dan **Operator Overloading** (Java tidak support operator overloading).

**Method Overloading** terjadi ketika beberapa method memiliki nama yang sama tetapi parameter berbeda (jumlah, tipe, atau urutan).

```java
class Calculator {
    // Method dengan 2 parameter int
    int add(int a, int b) {
        return a + b;
    }
    
    // Method dengan 3 parameter int
    int add(int a, int b, int c) {
        return a + b + c;
    }
    
    // Method dengan 2 parameter double
    double add(double a, double b) {
        return a + b;
    }
}
```

### 2. Runtime Polymorphism (Dynamic Polymorphism)
Diselesaikan pada saat runtime melalui **Method Overriding**. Ini adalah bentuk polymorphism yang paling umum dibahas.

**Method Overriding** terjadi ketika subclass memberikan implementasi spesifik untuk method yang sudah didefinisikan di parent class.

```java
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Dog barks");
    }
}

class Cat extends Animal {
    @Override
    void makeSound() {
        System.out.println("Cat meows");
    }
}

// Penggunaan
Animal myAnimal = new Dog();  // Upcasting
myAnimal.makeSound();  // Output: "Dog barks"
```

## Konsep Penting dalam Polymorphism

### 1. Upcasting dan Downcasting

**Upcasting** (implisit, aman):
```java
Dog dog = new Dog();
Animal animal = dog;  // Upcasting - otomatis
```

**Downcasting** (eksplisit, perlu hati-hati):
```java
Animal animal = new Dog();
Dog dog = (Dog) animal;  // Downcasting - perlu cast eksplisit

// Gunakan instanceof untuk keamanan
if (animal instanceof Dog) {
    Dog dog = (Dog) animal;
}
```

### 2. Dynamic Method Dispatch

Mekanisme dimana panggilan method yang di-override diselesaikan pada runtime, bukan compile-time. JVM menentukan method mana yang dipanggil berdasarkan objek aktual, bukan tipe referensi.

```java
class Shape {
    void draw() {
        System.out.println("Drawing Shape");
    }
}

class Circle extends Shape {
    @Override
    void draw() {
        System.out.println("Drawing Circle");
    }
}

class Rectangle extends Shape {
    @Override
    void draw() {
        System.out.println("Drawing Rectangle");
    }
}

// Dynamic Method Dispatch
Shape shape1 = new Circle();
Shape shape2 = new Rectangle();

shape1.draw();  // Drawing Circle - ditentukan saat runtime
shape2.draw();  // Drawing Rectangle - ditentukan saat runtime
```

### 3. Rules untuk Method Overriding

1. Method signature harus sama (nama dan parameter)
2. Return type harus sama atau covariant (subtype dari return type parent)
3. Access modifier tidak boleh lebih restrictive
4. Tidak bisa override `static`, `final`, atau `private` methods
5. Constructor tidak bisa di-override
6. Method yang di-override harus memiliki exception yang sama, subclass exception, atau tidak ada exception (tidak boleh lebih luas)

```java
class Parent {
    protected Number getValue() throws IOException {
        return 10;
    }
}

class Child extends Parent {
    // Valid: covariant return type (Integer adalah subtype dari Number)
    @Override
    public Integer getValue() {  // access modifier diperluas dari protected ke public
        return 20;
    }
}
```

## Polymorphism dengan Interface

Interface adalah kontrak yang mendefinisikan behavior. Polymorphism sangat kuat ketika dikombinasikan dengan interface.

```java
interface Payable {
    double calculatePayment();
}

class Employee implements Payable {
    private double salary;
    
    public Employee(double salary) {
        this.salary = salary;
    }
    
    @Override
    public double calculatePayment() {
        return salary;
    }
}

class Contractor implements Payable {
    private double hourlyRate;
    private int hoursWorked;
    
    public Contractor(double hourlyRate, int hoursWorked) {
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }
    
    @Override
    public double calculatePayment() {
        return hourlyRate * hoursWorked;
    }
}

// Polymorphism dalam action
class PaymentProcessor {
    void processPayment(Payable payable) {
        double amount = payable.calculatePayment();
        System.out.println("Processing payment: " + amount);
    }
}

// Penggunaan
PaymentProcessor processor = new PaymentProcessor();
processor.processPayment(new Employee(5000));      // Works
processor.processPayment(new Contractor(50, 160)); // Works
```

## Pertanyaan Interview yang Sering Muncul

**Q: Apa perbedaan antara Overloading dan Overriding?**
- **Overloading**: Compile-time, method dengan nama sama tapi parameter berbeda, bisa di class yang sama
- **Overriding**: Runtime, method dengan signature sama, harus di subclass

**Q: Bisakah kita override static method?**
Tidak. Static method milik class, bukan objek. Jika mendefinisikan static method dengan nama sama di subclass, itu disebut method hiding, bukan overriding.

**Q: Apa itu covariant return type?**
Sejak Java 5, override method bisa mengembalikan subtype dari return type di parent class.

**Q: Bagaimana polymorphism membantu dalam desain aplikasi?**
- Membuat kode lebih fleksibel dan maintainable
- Mendukung prinsip Open-Closed Principle (open for extension, closed for modification)
- Memungkinkan dependency injection dan loose coupling

**Q: Apa yang terjadi dengan variable dalam polymorphism?**
Variable tidak polymorphic. Variable yang diakses ditentukan oleh tipe referensi (compile-time), bukan objek aktual.

```java
class Parent {
    int x = 10;
}

class Child extends Parent {
    int x = 20;
}

Parent obj = new Child();
System.out.println(obj.x);  // Output: 10 (bukan 20!)
```

Apakah ada aspek tertentu dari polymorphism yang ingin Anda dalami lebih lanjut, seperti use cases praktis, design patterns yang memanfaatkan polymorphism, atau contoh soal interview?