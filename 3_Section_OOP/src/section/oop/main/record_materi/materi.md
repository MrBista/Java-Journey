# Belajar Record di Java dari Nol

Baik, saya akan mengajarkan kamu tentang **Record** di Java secara lengkap dan detail. Mari kita mulai dari dasar!

## Apa itu Record?

Record adalah fitur yang diperkenalkan di **Java 14** (preview) dan menjadi fitur resmi di **Java 16**. Record adalah cara khusus untuk membuat class yang tujuan utamanya hanya untuk **menyimpan data** (data carrier class).

### Masalah Sebelum Ada Record

Sebelum ada record, kalau kita ingin membuat class sederhana untuk menyimpan data, kita harus menulis banyak kode boilerplate:

```java
public class Mahasiswa {
    private final String nama;
    private final String nim;
    private final int umur;
    
    // Constructor
    public Mahasiswa(String nama, String nim, int umur) {
        this.nama = nama;
        this.nim = nim;
        this.umur = umur;
    }
    
    // Getter untuk semua field
    public String getNama() { return nama; }
    public String getNim() { return nim; }
    public int getUmur() { return umur; }
    
    // equals()
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Mahasiswa that = (Mahasiswa) obj;
        return umur == that.umur && 
               nama.equals(that.nama) && 
               nim.equals(that.nim);
    }
    
    // hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(nama, nim, umur);
    }
    
    // toString()
    @Override
    public String toString() {
        return "Mahasiswa{nama='" + nama + "', nim='" + nim + "', umur=" + umur + "}";
    }
}
```

Lihat betapa panjangnya kode di atas hanya untuk menyimpan 3 data! 😅

### Solusi dengan Record

Dengan record, semua kode di atas bisa ditulis dalam **1 baris**:

```java
public record Mahasiswa(String nama, String nim, int umur) {}
```

Ya, hanya itu! Record secara otomatis akan generate:
- Constructor
- Getter methods
- `equals()`
- `hashCode()`
- `toString()`

## Cara Menggunakan Record

### 1. Membuat Record

```java
public record Mahasiswa(String nama, String nim, int umur) {}
```

Bagian dalam kurung `(String nama, String nim, int umur)` disebut **component list** atau **header**.

### 2. Membuat Objek dari Record

```java
Mahasiswa mhs1 = new Mahasiswa("Budi", "12345", 20);
```

### 3. Mengakses Data

Record otomatis membuat **accessor methods** (bukan getter biasa). Namanya sama dengan nama field, bukan `getNama()`:

```java
System.out.println(mhs1.nama());   // "Budi"
System.out.println(mhs1.nim());    // "12345"
System.out.println(mhs1.umur());   // 20
```

Perhatikan: menggunakan `nama()` bukan `getNama()`!

### 4. toString() Otomatis

```java
System.out.println(mhs1);
// Output: Mahasiswa[nama=Budi, nim=12345, umur=20]
```

### 5. equals() dan hashCode() Otomatis

```java
Mahasiswa mhs2 = new Mahasiswa("Budi", "12345", 20);
System.out.println(mhs1.equals(mhs2));  // true
System.out.println(mhs1 == mhs2);        // false (beda objek)
```

## Karakteristik Penting Record

### 1. Immutable (Tidak Bisa Diubah)

Semua field di record adalah **final**. Artinya setelah dibuat, nilainya tidak bisa diubah:

```java
public record Mahasiswa(String nama, String nim, int umur) {}

// TIDAK ADA setter methods!
// mhs1.setNama("Andi"); // ERROR! Method tidak ada
```

### 2. Tidak Bisa Di-extend

Record secara otomatis extends class `java.lang.Record`, dan tidak bisa meng-extend class lain:

```java
// ERROR! Record tidak bisa extends class lain
public record Mahasiswa(String nama) extends SomeClass {}
```

### 3. Tidak Bisa Di-inherit

Class lain tidak bisa meng-extend record karena record implicitly final:

```java
// ERROR! Tidak bisa extends record
public class MahasiswaBerprestasi extends Mahasiswa {}
```

### 4. Bisa Mengimplementasikan Interface

```java
public interface Identifiable {
    String getId();
}

public record Mahasiswa(String nama, String nim, int umur) 
    implements Identifiable {
    
    @Override
    public String getId() {
        return nim;
    }
}
```

## Fitur Lanjutan Record

### 1. Compact Constructor

Untuk validasi atau modifikasi parameter sebelum assignment:

```java
public record Mahasiswa(String nama, String nim, int umur) {
    // Compact constructor - tidak perlu parameter dan assignment
    public Mahasiswa {
        // Validasi
        if (umur < 0) {
            throw new IllegalArgumentException("Umur tidak boleh negatif");
        }
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong");
        }
        
        // Normalisasi data
        nama = nama.trim();
        nim = nim.toUpperCase();
    }
}
```

### 2. Canonical Constructor (Eksplisit)

Jika butuh lebih banyak kontrol:

```java
public record Mahasiswa(String nama, String nim, int umur) {
    // Canonical constructor dengan semua parameter
    public Mahasiswa(String nama, String nim, int umur) {
        this.nama = nama != null ? nama.trim() : "";
        this.nim = nim != null ? nim.toUpperCase() : "";
        this.umur = Math.max(0, umur); // Pastikan tidak negatif
    }
}
```

### 3. Custom Constructor (Overloading)

Bisa menambahkan constructor tambahan:

```java
public record Mahasiswa(String nama, String nim, int umur) {
    // Constructor tambahan
    public Mahasiswa(String nama, String nim) {
        this(nama, nim, 18); // Default umur 18
    }
}

// Penggunaan:
Mahasiswa mhs1 = new Mahasiswa("Budi", "12345");
System.out.println(mhs1.umur()); // 18
```

### 4. Method Tambahan

Bisa menambahkan method custom:

```java
public record Mahasiswa(String nama, String nim, int umur) {
    public boolean isUnderage() {
        return umur < 18;
    }
    
    public String getFullInfo() {
        return String.format("%s (%s) - %d tahun", nama, nim, umur);
    }
}

// Penggunaan:
Mahasiswa mhs = new Mahasiswa("Budi", "12345", 17);
System.out.println(mhs.isUnderage());  // true
System.out.println(mhs.getFullInfo()); // "Budi (12345) - 17 tahun"
```

### 5. Static Members

Bisa menambahkan field dan method static:

```java
public record Mahasiswa(String nama, String nim, int umur) {
    private static int counter = 0;
    
    public static final int MIN_UMUR = 17;
    public static final int MAX_UMUR = 100;
    
    public static int getCounter() {
        return counter;
    }
    
    public Mahasiswa {
        counter++;
        if (umur < MIN_UMUR || umur > MAX_UMUR) {
            throw new IllegalArgumentException("Umur tidak valid");
        }
    }
}
```

### 6. Nested Records

Record bisa berisi record lain:

```java
public record Universitas(String nama, Alamat alamat) {
    public record Alamat(String jalan, String kota, String kodePos) {}
}

// Penggunaan:
var alamat = new Universitas.Alamat("Jl. Sudirman", "Jakarta", "12345");
var univ = new Universitas("UI", alamat);
System.out.println(univ.alamat().kota()); // "Jakarta"
```

## Kapan Menggunakan Record?

### ✅ Gunakan Record Untuk:

1. **Data Transfer Objects (DTO)**
```java
public record UserDTO(Long id, String username, String email) {}
```

2. **Value Objects**
```java
public record Money(BigDecimal amount, String currency) {}
```

3. **Configuration Objects**
```java
public record DatabaseConfig(String url, String username, String password) {}
```

4. **API Responses**
```java
public record ApiResponse(int status, String message, Object data) {}
```

5. **Hasil Query/Projection**
```java
public record MahasiswaInfo(String nama, double ipk) {}
```

### ❌ Jangan Gunakan Record Untuk:

1. **Entity yang perlu mutability** (data yang sering berubah)
```java
// JANGAN - gunakan class biasa
// public record ShoppingCart(List<Item> items) {} 

// LEBIH BAIK
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();
    
    public void addItem(Item item) {
        items.add(item);
    }
}
```

2. **Class dengan business logic kompleks**
3. **Class yang perlu inheritance hierarchy**
4. **JavaBeans yang memerlukan setter**

## Best Practices

### 1. Validasi di Constructor

```java
public record Email(String address) {
    public Email {
        if (address == null || !address.contains("@")) {
            throw new IllegalArgumentException("Email tidak valid");
        }
    }
}
```

### 2. Gunakan untuk Immutable Data

```java
public record Point(int x, int y) {
    // Sempurna! Point tidak akan berubah setelah dibuat
}
```

### 3. Hindari Mutable Fields

```java
// ❌ JANGAN INI
public record BadExample(List<String> items) {}
// Meskipun reference final, isi List bisa diubah!

// ✅ LEBIH BAIK
public record GoodExample(List<String> items) {
    public GoodExample {
        items = List.copyOf(items); // Buat defensive copy
    }
}
```

### 4. Dokumentasi yang Jelas

```java
/**
 * Merepresentasikan informasi mahasiswa
 * @param nama Nama lengkap mahasiswa
 * @param nim Nomor Induk Mahasiswa (harus unique)
 * @param umur Umur mahasiswa dalam tahun
 */
public record Mahasiswa(String nama, String nim, int umur) {}
```

### 5. Kombinasi dengan Pattern Matching (Java 16+)

```java
public sealed interface Shape permits Circle, Rectangle {}
public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}

// Pattern matching
public double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
    };
}
```

## Contoh Praktis LengkapSaya sudah membuatkan contoh kode lengkap di artifact yang bisa kamu pelajari!

## Ringkasan

**Record** adalah fitur Java modern untuk membuat class data dengan mudah:

- **Sintaks ringkas**: 1 baris vs puluhan baris kode
- **Immutable by default**: Data tidak bisa diubah setelah dibuat
- **Auto-generated methods**: Constructor, accessor, equals(), hashCode(), toString()
- **Best for**: DTO, Value Objects, Configuration, API responses
- **Hindari untuk**: Entity yang mutable, business logic kompleks

Record membuat kode Java lebih bersih, lebih aman, dan lebih mudah dipelihara. Ini adalah salah satu fitur modern Java yang sangat berguna untuk programming sehari-hari!

Apakah ada bagian yang ingin kamu tanyakan lebih lanjut? 😊