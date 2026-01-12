Halo! Pilihan yang tepat. **Polymorphism** (Polimorfisme) adalah jantung dari OOP yang membuat kode Java menjadi *flexible*, *scalable*, dan mudah di-*maintain*.

Sebagai software developer yang sedang *prepare* interview, jawaban standar "banyak bentuk" tidak akan cukup. Pewawancara ingin melihat pemahaman kamu tentang bagaimana JVM menangani method call, perbedaan *binding*, dan *edge cases*.

Mari kita bedah Polymorphism secara mendalam, dari konsep dasar hingga level "Interview Ready".

---

### 🏛️ Apa itu Polymorphism?

Secara harfiah berarti "banyak bentuk". Dalam konteks Java, ini adalah kemampuan sebuah objek untuk mengambil banyak bentuk. Penggunaan paling umum adalah ketika **referensi dari Parent Class (atau Interface) digunakan untuk menunjuk ke Object dari Child Class.**

Di Java, Polymorphism dibagi menjadi dua jenis utama:

#### 1. Static Polymorphism (Compile-time)

Ini terjadi melalui **Method Overloading**.

* **Mekanisme:** Compiler menentukan method mana yang akan dipanggil berdasarkan *method signature* (nama method + daftar parameter) saat proses kompilasi.
* **Contoh:** Constructor overloading, atau method `print(String s)` vs `print(int i)`.

#### 2. Dynamic Polymorphism (Runtime)

Ini terjadi melalui **Method Overriding**.

* **Mekanisme:** JVM menentukan method mana yang akan dijalankan pada saat *runtime*, bukan saat compile time. Ini adalah inti dari pembahasan kita.

---

### ⚙️ Deep Dive: Dynamic Polymorphism & Upcasting

Konsep kuncinya adalah **Upcasting**: Mengubah referensi subclass menjadi superclass.

```java
// Parent
class Hewan {
    void bersuara() { System.out.println("...hening..."); }
}

// Child
class Kucing extends Hewan {
    @Override
    void bersuara() { System.out.println("Meong!"); }
    
    void cakar() { System.out.println("Mencakar!"); }
}

public class Main {
    public static void main(String[] args) {
        // UPCASTING: Referensi Hewan, Objek Kucing
        Hewan h = new Kucing();
        
        // Apa yang terjadi di sini?
        h.bersuara(); 
    }
}

```

**Analisis untuk Interview:**

1. **Compile Time:** Compiler melihat referensi `h` bertipe `Hewan`. Ia mengecek class `Hewan`, apakah ada method `bersuara()`? Ada. *Compile lolos.*
2. **Runtime:** JVM melihat objek aslinya di memori (Heap), yaitu `new Kucing()`. JVM mengecek apakah `Kucing` meng-*override* method `bersuara()`? Ya. Maka yang dijalankan adalah versi `Kucing`.
* **Output:** "Meong!"



**⚠️ Batasan Upcasting:**
Jika kamu memanggil `h.cakar()`, akan terjadi **Compile Error**. Kenapa? Karena referensi `h` bertipe `Hewan`, dan class `Hewan` tidak tahu menahu tentang method `cakar()`, meskipun objek aslinya adalah Kucing.

---

### 🧠 Under The Hood: Bagaimana JVM Menanganinya? (Interview Expert Level)

Jika pewawancara bertanya: *"Bagaimana JVM tahu method mana yang harus dipanggil saat runtime?"*

Jawabannya adalah: **Virtual Method Table (vtable)**.

1. Setiap class yang dimuat ke dalam memori memiliki sebuah tabel (vtable) yang berisi alamat dari method-methodnya.
2. Jika sebuah class meng-*override* method parent, alamat method di vtable child class akan menunjuk ke implementasi baru tersebut.
3. Saat `h.bersuara()` dipanggil, JVM melakukan **Dynamic Binding** (Late Binding). Ia melihat ke vtable milik *objek aktual* (Kucing), bukan tipe referensinya (Hewan).

---

### 🔥 Topik Jebakan (Gotchas) dalam Interview

Ini adalah bagian di mana banyak kandidat senior sekalipun sering tergelincir.

#### 1. Polymorphism Tidak Berlaku untuk Variable (Data Member)

Ini aturan emas: **Method itu Polymorphic, tapi Variable itu tidak.** Variable selalu di-resolve berdasarkan **Reference Type**, bukan Object Type.

```java
class Parent {
    String name = "Parent";
}

class Child extends Parent {
    String name = "Child";
}

public class Test {
    public static void main(String[] args) {
        Parent p = new Child();
        System.out.println(p.name); 
    }
}

```

* **Output:** `Parent`
* **Alasan:** Variable binding terjadi saat *compile time* (Static Binding). Karena referensi `p` adalah `Parent`, maka `p.name` mengambil milik `Parent`. Konsep ini disebut **Variable Hiding** (bukan overriding).

#### 2. Static Methods Tidak Bisa Di-Override

Jika kamu mendefinisikan method `static` dengan nama yang sama di Parent dan Child, itu disebut **Method Hiding**, bukan Overriding. Static method terikat pada Class, bukan Object.

#### 3. Private Methods

Method `private` tidak bisa di-override karena tidak terlihat oleh child class. Mereka terikat secara statis (compile time).

#### 4. Covariant Return Types

Sejak Java 5, kamu bisa mengubah *return type* dari method yang di-override, ASALKAN return type baru adalah **subclass** dari return type asli.

```java
class Factory {
    Hewan create() { return new Hewan(); }
}

class CatFactory extends Factory {
    @Override
    // Valid! Kucing adalah subclass Hewan (Covariant)
    Kucing create() { return new Kucing(); } 
}

```

---

### 🛡️ Contoh Skenario Nyata (Design Pattern Strategy)

Untuk level developer berpengalaman, hindari contoh "Hewan/Kucing". Gunakan contoh kasus bisnis, misalnya sistem pembayaran. Ini menunjukkan kamu paham penerapan arsitekturnya.

```java
// Interface (Kontrak)
interface PaymentStrategy {
    void pay(double amount);
}

// Implementasi 1
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    public CreditCardPayment(String cc) { this.cardNumber = cc; }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using CC: " + cardNumber);
    }
}

// Implementasi 2
class OVO_Payment implements PaymentStrategy {
    private String phoneNumber;
    public OVO_Payment(String phone) { this.phoneNumber = phone; }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using OVO: " + phoneNumber);
    }
}

// Client Code (Decoupled)
class ShoppingCart {
    // Polymorphism in action: 
    // ShoppingCart tidak perlu tahu detail implementasi pembayaran
    public void checkout(double amount, PaymentStrategy strategy) {
        // Dynamic Binding terjadi di sini
        strategy.pay(amount);
    }
}

```

**Kenapa ini bagus untuk interview?**

* Menunjukkan prinsip **Open/Closed Principle** (SOLID): Jika ingin menambah metode pembayaran baru (misal: GoPay), kamu cukup buat class baru tanpa mengubah kode `ShoppingCart`.

---

### 📝 Cheat Sheet Ringkas untuk Interview

| Fitur | Method Overloading | Method Overriding |
| --- | --- | --- |
| **Tipe Polymorphism** | Static (Compile-time) | Dynamic (Runtime) |
| **Signature** | Harus beda (parameter) | Harus sama persis |
| **Return Type** | Bisa beda | Sama atau Covariant |
| **Binding** | Early Binding | Late Binding |
| **Handling Exception** | Bebas | Tidak boleh melempar Checked Exception baru/lebih luas |
| **Keyword** | Tidak ada khusus | `@Override` (Best practice) |

---

**Satu langkah selanjutnya untukmu:**

Apakah kamu ingin saya berikan **kuis studi kasus (snippet code)** yang sering muncul di interview Java backend (misalnya tentang urutan eksekusi Constructor vs Static Block dalam hierarki Polymorphism) untuk menguji pemahamanmu?