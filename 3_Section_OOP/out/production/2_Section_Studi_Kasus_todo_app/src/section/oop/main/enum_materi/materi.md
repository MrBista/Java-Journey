# Enum di Java - Panduan Lengkap

Saya akan menjelaskan Enum di Java secara mendetail untuk Anda.

## Apa itu Enum?

Enum (enumerations) adalah tipe data khusus di Java yang merepresentasikan kumpulan konstanta yang tetap. Enum diperkenalkan di Java 5 dan jauh lebih powerful daripada sekadar kumpulan konstanta.

## Dasar-dasar Enum

```java
// Enum sederhana
public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

// Penggunaan
Day today = Day.MONDAY;
```

## Karakteristik Penting Enum

**1. Enum adalah Class**
- Setiap enum secara implisit extends `java.lang.Enum`
- Tidak bisa extend class lain (karena sudah extends Enum)
- Bisa implements interface
- Bisa memiliki constructor, method, dan field

**2. Instance Enum adalah Singleton**
- Setiap konstanta enum hanya memiliki satu instance
- Thread-safe secara otomatis
- Tidak bisa membuat instance baru dengan `new`

## Rule dan Batasan

### Rule Wajib:
1. **Konstanta enum harus dideklarasikan pertama** (sebelum field/method)
2. **Constructor enum harus private** (atau package-private)
3. **Tidak bisa instantiate dengan `new`**
4. **Tidak bisa extend enum** (final secara implisit)
5. **Nama konstanta sebaiknya UPPERCASE** (konvensi)

### Fitur Enum yang Powerful

```java
public enum Status {
    // Konstanta dengan parameter
    PENDING("Menunggu", 0),
    PROCESSING("Diproses", 1),
    COMPLETED("Selesai", 2),
    FAILED("Gagal", -1);
    
    // Field
    private final String description;
    private final int code;
    
    // Constructor (harus private)
    private Status(String description, int code) {
        this.description = description;
        this.code = code;
    }
    
    // Method
    public String getDescription() {
        return description;
    }
    
    public int getCode() {
        return code;
    }
    
    // Static method
    public static Status fromCode(int code) {
        for (Status status : Status.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
    
    // Method per konstanta
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }
}
```

## Method Bawaan Enum

Setiap enum otomatis memiliki method:

```java
// values() - mengembalikan array semua konstanta
Status[] allStatus = Status.values();

// valueOf(String) - mengkonversi string ke enum
Status status = Status.valueOf("PENDING");

// name() - nama konstanta sebagai String
String name = Status.PENDING.name(); // "PENDING"

// ordinal() - posisi/index konstanta (mulai dari 0)
int index = Status.PENDING.ordinal(); // 0

// compareTo() - membandingkan berdasarkan ordinal
int comparison = Status.PENDING.compareTo(Status.COMPLETED);
```

## Best Practices

### 1. Gunakan Enum untuk Set Konstanta yang Tetap
```java
// GOOD - nilai tetap dan terbatas
public enum PaymentMethod {
    CASH, CREDIT_CARD, DEBIT_CARD, E_WALLET
}

// BAD - jangan untuk data yang dinamis
// Misalnya: daftar user, produk yang bisa bertambah
```

### 2. Tambahkan Behavior pada Enum
```java
public enum Operation {
    PLUS {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS {
        public double apply(double x, double y) { return x - y; }
    },
    MULTIPLY {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE {
        public double apply(double x, double y) { return x / y; }
    };
    
    public abstract double apply(double x, double y);
}

// Penggunaan
double result = Operation.PLUS.apply(5, 3); // 8.0
```

### 3. Implements Interface untuk Polimorfisme
```java
public interface Describable {
    String getDescription();
}

public enum Priority implements Describable {
    LOW("Prioritas Rendah"),
    MEDIUM("Prioritas Sedang"),
    HIGH("Prioritas Tinggi");
    
    private final String description;
    
    Priority(String description) {
        this.description = description;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
}
```

### 4. EnumSet dan EnumMap
```java
import java.util.EnumSet;
import java.util.EnumMap;

// EnumSet - set khusus untuk enum (sangat efisien)
EnumSet<Day> weekend = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
EnumSet<Day> weekdays = EnumSet.range(Day.MONDAY, Day.FRIDAY);

// EnumMap - map dengan key enum (lebih efisien dari HashMap)
EnumMap<Status, String> messages = new EnumMap<>(Status.class);
messages.put(Status.PENDING, "Mohon tunggu");
messages.put(Status.COMPLETED, "Sukses");
```

### 5. Switch Statement dengan Enum
```java
public String getStatusMessage(Status status) {
    return switch (status) {
        case PENDING -> "Pesanan sedang menunggu";
        case PROCESSING -> "Pesanan sedang diproses";
        case COMPLETED -> "Pesanan selesai";
        case FAILED -> "Pesanan gagal";
    };
}
```

### 6. Singleton Pattern dengan Enum
```java
// Cara terbaik implementasi Singleton
public enum DatabaseConnection {
    INSTANCE;
    
    private Connection connection;
    
    private DatabaseConnection() {
        // Initialize connection
    }
    
    public Connection getConnection() {
        return connection;
    }
}

// Penggunaan
DatabaseConnection.INSTANCE.getConnection();
```

## Anti-Patterns yang Harus Dihindari

### ❌ Jangan gunakan ordinal() untuk logic
```java
// BAD
if (status.ordinal() == 2) { ... }

// GOOD
if (status == Status.COMPLETED) { ... }
```

### ❌ Jangan simpan state yang mutable
```java
// BAD - enum harus immutable
public enum Counter {
    INSTANCE;
    private int count = 0; // mutable state
    public void increment() { count++; }
}

// GOOD - gunakan class biasa untuk mutable state
```

### ❌ Jangan buat enum terlalu kompleks
```java
// BAD - terlalu banyak tanggung jawab
public enum UserManager {
    INSTANCE;
    // ratusan baris kode business logic
}

// GOOD - pisahkan menjadi class tersendiri
```

## Use Cases Umum

1. **Status/State Machine**: order status, payment status, dll
2. **Tipe/Kategori**: payment method, user role, product category
3. **Konfigurasi**: environment (DEV, STAGING, PROD)
4. **Strategy Pattern**: berbagai algoritma/operasi
5. **Singleton**: instance tunggal yang aman
6. **Constants dengan behavior**: lebih baik dari static final

## Perbandingan dengan Alternatif

**Enum vs Static Final Constants:**
- Enum: type-safe, bisa punya method, auto-complete IDE
- Constants: tidak type-safe, hanya nilai

**Enum vs Class:**
- Enum: untuk set tetap, singleton per konstanta
- Class: untuk objek yang bisa di-instantiate berkali-kali

Apakah ada aspek khusus dari enum yang ingin Anda pelajari lebih dalam, misalnya implementasi pattern tertentu atau use case spesifik?