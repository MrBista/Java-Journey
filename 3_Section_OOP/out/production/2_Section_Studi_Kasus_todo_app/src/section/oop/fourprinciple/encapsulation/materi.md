pemahaman tentang **Encapsulation** tidak boleh berhenti hanya pada "membuat variabel `private` dan membuat `getter/setter`".

---

### 1. Definisi Fundamental & Filosofi

Secara teknis, Encapsulation adalah mekanisme membungkus data (variables) dan kode yang memanipulasi data (methods) menjadi satu unit tunggal (class), serta menyembunyikan detail implementasi dari dunia luar.

**Kata Kunci untuk Interview:**

* **Information Hiding:** Menyembunyikan kompleksitas internal.
* **Implementation Hiding:** Mengizinkan kita mengubah kode internal tanpa mematahkan kode klien yang menggunakannya.
* **Maintainability & Loose Coupling:** Mengurangi ketergantungan antar komponen.

---

### 2. Level 1: Access Modifiers (The Basics)

Pastikan kamu hafal tabel visibilitas ini di luar kepala. Interviewer sering menanyakan perbedaan `protected` dan `default` (package-private).

| Modifier | Class | Package | Subclass (diff pkg) | World |
| --- | --- | --- | --- | --- |
| **public** | Yes | Yes | Yes | Yes |
| **protected** | Yes | Yes | **Yes** (via inheritance) | No |
| **no modifier** | Yes | Yes | No | No |
| **private** | Yes | No | No | No |

> **Note:** Di Java, "no modifier" disebut **Package-Private**. Ini sering digunakan di JUnit testing atau class internal library.

---

### 3. Level 2: The "POJO" Trap (Getter/Setter Otomatis)

Banyak developer berpikir encapsulation = generate getter/setter untuk semua field. **Ini salah.**

Jika kamu memiliki field `private` tapi memberikan `public getter` dan `public setter` tanpa validasi, itu sama saja dengan membuat field tersebut `public`. Kamu tidak melindungi *invariant* (aturan bisnis).

**Contoh Interview yang Baik:**
Tunjukkan bahwa setter harus menjaga validitas data.

```java
public class BankAccount {
    private double balance;

    // BAD: Setter tanpa validasi (Anemic Domain Model)
    // public void setBalance(double balance) {
    //     this.balance = balance;
    // }

    // GOOD: Encapsulation dengan Business Logic
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit harus positif");
        }
        this.balance += amount;
    }
}

```

---

### 4. Level 3: Reference Leaking & Defensive Copying (Crucial!)

Ini adalah topik favorit interviewer untuk mengecoh kandidat. Memiliki field `private` tidak menjamin keamanan jika field tersebut adalah **Mutable Object** (seperti `Date`, `List`, atau `Array`).

**Skenario Jebakan:**

```java
import java.util.Date;

public class Employee {
    private String name;
    private Date joinDate; // Date adalah Mutable Object!

    public Employee(String name, Date joinDate) {
        this.name = name;
        // MASALAH 1: Menyimpan referensi langsung dari luar
        this.joinDate = joinDate;
    }

    public Date getJoinDate() {
        // MASALAH 2: Mengembalikan referensi langsung ke internal state
        return this.joinDate;
    }
}

```

**Cara Menghancurkan Encapsulation di atas:**

```java
Date d = new Date();
Employee e = new Employee("Budi", d);

// Hacker mengubah tanggal di luar class Employee
d.setTime(0L);
// ATAU
e.getJoinDate().setTime(0L);

// Sekarang state di dalam Employee berubah tanpa sepengetahuan objek Employee!

```

**Solusi: Defensive Copying**
Selalu buat *copy* baru saat menerima atau mengembalikan objek *mutable*.

```java
public class SecureEmployee {
    private final String name;
    private final Date joinDate;

    public SecureEmployee(String name, Date joinDate) {
        this.name = name;
        // Defensive Copy di Constructor
        this.joinDate = new Date(joinDate.getTime());
    }

    public Date getJoinDate() {
        // Defensive Copy di Getter
        return new Date(this.joinDate.getTime());
    }
}

```

---

### 5. Level 4: Immutability

Immutability adalah bentuk tertinggi dari encapsulation. Jika objek tidak bisa berubah setelah dibuat, maka dia *thread-safe* secara otomatis.

**Cara membuat Class Immutable (Checklist Interview):**

1. Jadikan class `final` (agar tidak bisa di-*extend* dan di-*override* method-nya).
2. Jadikan semua field `private` dan `final`.
3. Jangan sediakan method "setter".
4. Lakukan **Defensive Copying** untuk field yang *mutable* (seperti contoh `Date` di atas).
5. Gunakan **Constructor** untuk inisialisasi semua state.

**Modern Java (Java 14+): Records**
Java memperkenalkan `record` yang secara otomatis mengimplementasikan encapsulation untuk data carrier yang immutable.

```java
// Secara otomatis private final fields, public accessors, constructor, equals, hashCode, toString
public record User(String username, String email) {}

```

---

### 6. Level 5: Breaking Encapsulation (Reflection)

Interviewer mungkin bertanya: *"Apakah `private` benar-benar aman dan tidak bisa diakses sama sekali?"*

**Jawabannya:** Tidak mutlak. Kita bisa menjebolnya menggunakan **Java Reflection**.

```java
import java.lang.reflect.Field;

public class Hacker {
    public static void main(String[] args) throws Exception {
        SecretClass secret = new SecretClass();

        Field field = SecretClass.class.getDeclaredField("password");
        field.setAccessible(true); // Mematikan check access control!

        String value = (String) field.get(secret);
        System.out.println("Hacked: " + value);
    }
}

```

*Note: Ini bisa dicegah dengan `SecurityManager` (deprecated) atau konfigurasi Java Modules.*

---

### 7. Level 6: Java Modules (Java 9+)

Encapsulation tidak hanya di level Class, tapi juga di level **Module** (Strong Encapsulation).

Di `module-info.java`, kamu bisa menentukan package mana yang boleh dilihat oleh module luar.

```java
module com.mycompany.payment {
    // Hanya package 'api' yang bisa diakses dunia luar
    // Package 'internal' atau 'util' benar-benar tersembunyi
    exports com.mycompany.payment.api;
}

```

Ini mencegah penggunaan class internal library secara tidak sengaja.

---

### Ringkasan Poin untuk Interview

Saat ditanya tentang Encapsulation, strukturkan jawabanmu seperti ini:

1. **Definisi:** Membungkus data dan method, menyembunyikan detail implementasi.
2. **Manfaat:** Kontrol penuh terhadap data (validasi di setter), fleksibilitas perubahan internal, dan keamanan.
3. **Advanced (Nilai Plus):** Jelaskan bahaya me-return *mutable reference* (Reference Leaking) dan cara menanganinya dengan *Defensive Copying* atau *Immutability*.

---

**Next Step:**
Apakah kamu ingin saya berikan **contoh soal coding interview** (Live Coding case) yang spesifik meminta kamu memperbaiki *broken encapsulation* pada sebuah class?