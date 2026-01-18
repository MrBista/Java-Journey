# Panduan Lengkap Class di Java

Saya akan menjelaskan secara detail tentang Class di Java, mulai dari konsep dasar hingga best practices.

## 1. Konsep Dasar Class

Class adalah blueprint atau template untuk membuat objek. Class mendefinisikan properties (fields/attributes) dan behaviors (methods) yang dimiliki objek.

```java
public class Mobil {
    // Fields (properties)
    String merk;
    String warna;
    int tahun;
    
    // Constructor
    public Mobil(String merk, String warna, int tahun) {
        this.merk = merk;
        this.warna = warna;
        this.tahun = tahun;
    }
    
    // Method (behavior)
    public void jalankan() {
        System.out.println("Mobil " + merk + " sedang berjalan");
    }
}
```

## 2. Komponen-Komponen Class

### Fields (Variabel Instance)
- Menyimpan state/data dari objek
- Setiap objek memiliki salinan field-nya sendiri

### Constructor
- Method khusus untuk inisialisasi objek
- Nama harus sama dengan nama class
- Tidak memiliki return type
- Bisa overloaded (beberapa constructor dengan parameter berbeda)

### Methods
- Mendefinisikan perilaku objek
- Bisa mengakses dan memodifikasi fields

### Nested Class
- Class di dalam class lain
- Bisa static atau non-static (inner class)

## 3. Access Modifiers

Java memiliki 4 level akses:

```java
public class ContohAksesModifier {
    public String publik;        // Bisa diakses dari mana saja
    protected String dilindungi;  // Bisa diakses dari package yang sama dan subclass
    String defaultAccess;         // Bisa diakses hanya dari package yang sama
    private String privat;        // Hanya bisa diakses dalam class ini
}
```

## 4. Static vs Non-Static

```java
public class Counter {
    // Static - milik class, bukan objek
    static int totalObjek = 0;
    
    // Non-static - milik setiap objek
    int nomorObjek;
    
    public Counter() {
        totalObjek++;
        nomorObjek = totalObjek;
    }
    
    // Static method - bisa dipanggil tanpa membuat objek
    public static int getTotalObjek() {
        return totalObjek;
    }
}
```

**Kapan menggunakan static:**
- Untuk utility methods yang tidak bergantung pada state objek
- Untuk konstanta (dengan `static final`)
- Untuk shared data antar semua instance

## 5. Best Practices

### a) Encapsulation (Enkapsulasi)
Sembunyikan internal details dan berikan akses melalui methods:

```java
public class Akun {
    private double saldo;  // Private field
    
    // Public getter
    public double getSaldo() {
        return saldo;
    }
    
    // Public setter dengan validasi
    public void setSaldo(double saldo) {
        if (saldo >= 0) {
            this.saldo = saldo;
        } else {
            throw new IllegalArgumentException("Saldo tidak boleh negatif");
        }
    }
}
```

### b) Naming Conventions
- Class name: PascalCase (contoh: `MobilSport`, `UserAccount`)
- Fields & methods: camelCase (contoh: `nomorPlat`, `hitungKecepatan()`)
- Constants: UPPER_SNAKE_CASE (contoh: `MAX_SPEED`, `DEFAULT_COLOR`)

### c) Single Responsibility Principle
Setiap class hanya memiliki satu tanggung jawab:

```java
// BAGUS - fokus pada satu tanggung jawab
public class EmailService {
    public void kirimEmail(String tujuan, String pesan) {
        // logika kirim email
    }
}

// KURANG BAGUS - terlalu banyak tanggung jawab
public class User {
    private String nama;
    
    public void simpanKeDatabase() { }  // Seharusnya di UserRepository
    public void kirimEmail() { }        // Seharusnya di EmailService
}
```

### d) Immutability untuk Data Objects
Gunakan `final` fields dan tidak ada setter:

```java
public final class Koordinat {
    private final double x;
    private final double y;
    
    public Koordinat(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    
    // Tidak ada setter - objek immutable
}
```

## 6. Rules dan Aturan Penting

### Rule 1: Satu Public Class per File
```java
// File: Mobil.java
public class Mobil {
    // ...
}

// Tidak boleh ada public class lain di file yang sama
class Motor {  // Boleh, tapi non-public
    // ...
}
```

### Rule 2: Nama File Harus Sama dengan Public Class
File `Mobil.java` harus mengandung `public class Mobil`

### Rule 3: Constructor Chaining
```java
public class Produk {
    private String nama;
    private double harga;
    private String kategori;
    
    public Produk(String nama) {
        this(nama, 0.0, "Umum");  // Panggil constructor lain
    }
    
    public Produk(String nama, double harga) {
        this(nama, harga, "Umum");
    }
    
    public Produk(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }
}
```

### Rule 4: this vs super
```java
public class Kendaraan {
    protected String merk;
    
    public Kendaraan(String merk) {
        this.merk = merk;
    }
}

public class Mobil extends Kendaraan {
    private int jumlahPintu;
    
    public Mobil(String merk, int jumlahPintu) {
        super(merk);  // Panggil constructor parent
        this.jumlahPintu = jumlahPintu;
    }
}
```

## 7. Object Methods yang Harus Di-Override

```java
public class Mahasiswa {
    private String nim;
    private String nama;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Mahasiswa other = (Mahasiswa) obj;
        return nim.equals(other.nim);
    }
    
    @Override
    public int hashCode() {
        return nim.hashCode();
    }
    
    @Override
    public String toString() {
        return "Mahasiswa{nim='" + nim + "', nama='" + nama + "'}";
    }
}
```

## 8. Design Patterns yang Umum

### Singleton Pattern
```java
public class Database {
    private static Database instance;
    
    private Database() {
        // Constructor private
    }
    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}
```

### Builder Pattern
```java
public class Pizza {
    private String ukuran;
    private boolean keju;
    private boolean pepperoni;
    
    private Pizza(Builder builder) {
        this.ukuran = builder.ukuran;
        this.keju = builder.keju;
        this.pepperoni = builder.pepperoni;
    }
    
    public static class Builder {
        private String ukuran;
        private boolean keju = false;
        private boolean pepperoni = false;
        
        public Builder(String ukuran) {
            this.ukuran = ukuran;
        }
        
        public Builder keju(boolean value) {
            keju = value;
            return this;
        }
        
        public Builder pepperoni(boolean value) {
            pepperoni = value;
            return this;
        }
        
        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// Penggunaan:
Pizza pizza = new Pizza.Builder("Large")
    .keju(true)
    .pepperoni(true)
    .build();
```

## 9. Hal-Hal yang Harus Dihindari

❌ **God Class** - class yang terlalu besar dan melakukan terlalu banyak hal

❌ **Tight Coupling** - ketergantungan yang terlalu kuat antar class

❌ **Public Fields** - selalu gunakan private fields dengan getter/setter

❌ **Mutable Static Fields** - berbahaya dalam multi-threading

❌ **Leaking References** - mengembalikan referensi ke mutable objects

```java
// BURUK
public class Buruk {
    public List<String> data = new ArrayList<>();  // Public field
    
    public List<String> getData() {
        return data;  // Leaking reference
    }
}

// BAGUS
public class Bagus {
    private List<String> data = new ArrayList<>();
    
    public List<String> getData() {
        return new ArrayList<>(data);  // Return copy
    }
}
```

Apakah ada topik spesifik yang ingin kamu pelajari lebih dalam? Misalnya inheritance, polymorphism, abstract classes, atau interfaces?