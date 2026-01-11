Halo! Wah, mantap sudah 2 tahun berkecimpung dengan Java Spring Boot. Di level *intermediate* menuju *senior*, pemahaman tentang **Inheritance (Pewarisan)** tidak boleh hanya sebatas "Child extends Parent". Kamu harus paham apa yang terjadi di balik layar (memory), aturan *overriding* yang ketat, dan kapan **tidak** menggunakan inheritance.

Karena kamu ingin pendalaman, mari kita bedah sisi "gelap" dan teknis dari Inheritance yang sering menjebak developer berpengalaman.

---

## Deep Dive: Inheritance untuk Intermediate Dev

### 1. Urutan Eksekusi Konstruktor (Constructor Chaining)

Ini konsep fundamental tapi vital. Ketika kamu membuat objek Child, Java harus memastikan state milik Parent sudah siap *sebelum* Child melakukan inisialisasi sendiri.

* **Rule:** Baris pertama di konstruktor Child **harus** panggilan ke konstruktor Parent (`super()`).
* **Implisit:** Jika kamu tidak menulisnya, compiler akan otomatis menaruh `super()` (tanpa argumen).
* **Jebakan:** Jika Parent punya konstruktor berparameter dan *tidak* punya *no-arg constructor*, maka Child **wajib** memanggil `super(param)` secara eksplisit. Kalau tidak, *compile error*.

### 2. Method Overriding vs Variable Hiding

Ini yang sering keluar di interview sulit.

* **Method (Instance):** Bersifat **Polimorfik**. Yang dipanggil adalah method milik tipe objek aslinya (Runtime type).
* **Variable (Field) & Static Method:** Bersifat **Static Binding**. Yang dipanggil berdasarkan tipe referensinya (Reference type), bukan objek aslinya.

**Contoh Kasus:**

```java
class Parent {
    String name = "Parent Name";
    void print() { System.out.println("Parent Print"); }
}

class Child extends Parent {
    String name = "Child Name"; // Variable Hiding (Bukan Overriding!)
    @Override
    void print() { System.out.println("Child Print"); }
}

public class Main {
    public static void main(String[] args) {
        Parent p = new Child(); // Upcasting
        
        // Output: "Child Print" (Karena method di-override secara dinamis)
        p.print(); 
        
        // Output: "Parent Name" (Karena variable di-binding secara statis ke tipe Parent)
        System.out.println(p.name); 
    }
}

```

*Pelajaran:* Jangan pernah menimpa nama variabel parent di child class. Itu membingungkan (*shadowing*).

### 3. Aturan Ketat Overriding

Saat kamu melakukan `@Override`, kamu tidak bisa sembarangan mengubah tanda tangan method:

1. **Access Modifier:** Tidak boleh lebih ketat. Kalau Parent `protected`, Child harus `protected` atau `public`. Tidak boleh `private`.
2. **Return Type:** Harus sama atau merupakan *sub-class* dari return type Parent (**Covariant Return Type**).
3. **Exception:** Hanya boleh melempar exception yang sama, turunannya, atau tidak melempar sama sekali. Tidak boleh melempar *checked exception* baru yang lebih luas.

### 4. Inheritance vs Composition

Sebagai dev berpengalaman, kamu harus tahu kapan **berhenti** menggunakan inheritance.

* **Masalah:** Inheritance menciptakan *tight coupling*. Perubahan di Parent bisa merusak Child (*Fragile Base Class problem*).
* **Solusi:** Gunakan **Composition** (Has-A) jika hubungan antar class bukan benar-benar "Is-A".
* **Prinsip:** *Favor Composition over Inheritance.*

---

## File Markdown untuk Interview (Siap Copy-Paste)

Sesuai request, berikut adalah kumpulan soal interview dalam Bahasa Inggris yang dirancang untuk level *Intermediate/Senior*. Formatnya sudah saya buat dalam Raw Markdown.

Silakan salin kode di dalam blok di bawah ini dan simpan sebagai `java-inheritance-interview.md`:

```markdown
# Java Inheritance Interview Questions

A collection of intermediate to advanced interview questions focusing on Inheritance, Polymorphism, and OOP design in Java.

## Conceptual Questions

### 1. What is the difference between "Is-A" and "Has-A" relationships?
* **Is-A (Inheritance):** Represents a hierarchy where a child class is a specialized version of the parent class (e.g., `Dog` is an `Animal`). It is implemented using the `extends` keyword.
* **Has-A (Composition/Aggregation):** Represents a relationship where one class contains an instance of another class (e.g., `Car` has an `Engine`). This is generally preferred over inheritance for code reuse to reduce coupling.

### 2. Can we override static methods in Java?
No. Static methods are bound to the class, not the instance (object). If you define a static method with the same signature in the child class, it is called **Method Hiding**, not overriding. The method invoked depends on the type of the reference variable, not the object being referred to.

### 3. Explain Covariant Return Types in the context of overriding.
Since Java 5, it is possible to override a method by changing its return type, provided that the new return type is a **subclass** of the original return type.
* *Example:* If `Parent.produce()` returns `Object`, `Child.produce()` can return `String`.

### 4. Why does Java not support Multiple Inheritance with classes?
To avoid the **Diamond Problem**. If Class B and Class C both extend Class A and override a method `doSomething()`, and Class D extends both B and C, the compiler would not know which version of `doSomething()` to inherit (B's or C's). Java solves this by allowing multiple inheritance only through Interfaces (default methods handle ambiguity via strict rules).

### 5. What is the order of execution for constructors in an inheritance chain?
1.  The parent class constructor is always executed before the child class constructor.
2.  If `super()` is not called explicitly, the compiler inserts a no-arg `super()` call at the beginning of the child constructor.
3.  Instance initializer blocks of the parent run before the parent constructor.
4.  Instance initializer blocks of the child run before the child constructor.

---

## Code Analysis (Output Prediction)

### Case 1: Variable Hiding vs Method Overriding
**Question:** What is the output of the following code?

```java
class A {
    String s = "Class A";
    void show() { System.out.println("Method A"); }
}

class B extends A {
    String s = "Class B";
    void show() { System.out.println("Method B"); }
}

public class Test {
    public static void main(String[] args) {
        A obj = new B();
        System.out.println(obj.s);
        obj.show();
    }
}

```

<details>
<summary><strong>See Answer</strong></summary>

**Output:**

```
Class A
Method B

```

**Explanation:**

* **Fields are not polymorphic:** Variables are resolved at compile-time based on the reference type (`A`). Hence, `obj.s` prints "Class A".
* **Methods are polymorphic:** Instance methods are resolved at runtime based on the actual object (`B`). Hence, `obj.show()` prints "Method B".

</details>

---

### Case 2: Exception Handling in Overriding

**Question:** Will the following code compile? Why or why not?

```java
import java.io.IOException;

class Parent {
    void readFile() throws IOException {
        // logic
    }
}

class Child extends Parent {
    @Override
    void readFile() throws Exception {
        // logic
    }
}

```

<details>
<summary><strong>See Answer</strong></summary>

**Answer:** No, it will **Compilation Error**.
**Explanation:** When overriding a method, the child class cannot throw a checked exception that is higher in the hierarchy (broader) than the exception thrown by the parent class. `Exception` is a parent of `IOException`, so this is not allowed. The child can throw `IOException`, `FileNotFoundException` (subclass), or no exception at all.

</details>

---

### Case 3: Constructor Chaining

**Question:** What is the output?

```java
class Alpha {
    Alpha() { System.out.print("Alpha "); }
}

class Beta extends Alpha {
    Beta() { System.out.print("Beta "); }
}

class Gamma extends Beta {
    Gamma() { System.out.print("Gamma "); }
}

public class Main {
    public static void main(String[] args) {
        new Gamma();
    }
}

```

<details>
<summary><strong>See Answer</strong></summary>

**Output:**

```
Alpha Beta Gamma 

```

**Explanation:**
When `new Gamma()` is called:

1. `Gamma` constructor calls `super()` (Beta).
2. `Beta` constructor calls `super()` (Alpha).
3. `Alpha` constructor runs -> prints "Alpha ".
4. Control returns to `Beta` -> prints "Beta ".
5. Control returns to `Gamma` -> prints "Gamma ".

</details>

---

### Case 4: The `final` Keyword

**Question:** Can you override a `final` method? Can a `final` class be extended?

<details>
<summary><strong>See Answer</strong></summary>

* **Final Method:** No, a method marked as `final` cannot be overridden by subclasses. This is used to prevent changing the behavior of critical methods (e.g., `Object.getClass()`).
* **Final Class:** No, a class marked as `final` cannot be extended (inherited). Examples include `java.lang.String` and `java.lang.System`.

</details>

```

-----

### Langkah Selanjutnya untuk Kamu (Intermediate to Senior)

Mengingat kamu sudah punya pengalaman 2 tahun dengan Spring Boot:

1.  **Pelajari "Liskov Substitution Principle" (Huruf L dalam SOLID):** Ini adalah teori *mengapa* inheritance bisa berbahaya jika salah digunakan.
2.  **Coba pahami `default method` di Interface:** Sejak Java 8, Interface bisa punya method body. Pahami bedanya mewarisi class vs mengimplementasikan interface dengan default method (ini sering disebut *multiple inheritance of behavior*).

Apakah kamu mau aku buatkan contoh kasus nyata di Spring Boot di mana penggunaan *Inheritance* justru membuat kode jadi berantakan (bad practice), dan bagaimana cara memperbaikinya dengan *Composition*?

```