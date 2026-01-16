Ah, pertanyaan bagus! Saya jelaskan konsep ini dengan detail.

## Penjelasan Konstanta Enum dengan Parameter

Ketika Anda menulis:
```java
PENDING("Menunggu", 0)
```

Sebenarnya Anda sedang **memanggil constructor** dari enum tersebut. Ini mirip dengan `new Status("Menunggu", 0)`, tapi untuk enum tidak pakai keyword `new`.

## Mari Bandingkan dengan Class Biasa

### Class Biasa:
```java
public class Status {
    private final String description;
    private final int code;
    
    // Constructor
    public Status(String description, int code) {
        this.description = description;
        this.code = code;
    }
}

// Cara membuat instance
Status pending = new Status("Menunggu", 0);
Status processing = new Status("Diproses", 1);
```

### Enum (sama konsepnya, tapi sintaksnya beda):
```java
public enum Status {
    // Ini seperti: 
    // public static final Status PENDING = new Status("Menunggu", 0);
    // public static final Status PROCESSING = new Status("Diproses", 1);
    PENDING("Menunggu", 0),      // memanggil constructor
    PROCESSING("Diproses", 1),   // memanggil constructor
    COMPLETED("Selesai", 2);     // memanggil constructor
    
    // Field (sama seperti class biasa)
    private final String description;
    private final int code;
    
    // Constructor (harus private untuk enum)
    private Status(String description, int code) {
        this.description = description;
        this.code = code;
    }
    
    // Getter
    public String getDescription() {
        return description;
    }
    
    public int getCode() {
        return code;
    }
}
```

## Proses yang Terjadi

Ketika Java membaca `PENDING("Menunggu", 0)`:

1. Java memanggil `private Status(String description, int code)`
2. Parameter `"Menunggu"` masuk ke `description`
3. Parameter `0` masuk ke `code`
4. Field `description` dan `code` di-assign
5. Objek PENDING dibuat dan disimpan sebagai konstanta

## Contoh Lebih Sederhana

### Enum tanpa parameter (tidak punya constructor):
```java
public enum Day {
    MONDAY, TUESDAY, WEDNESDAY
    // Tidak ada field, tidak ada constructor
    // Hanya nama konstanta saja
}
```

### Enum dengan 1 parameter:
```java
public enum Day {
    MONDAY("Senin"),
    TUESDAY("Selasa"),
    WEDNESDAY("Rabu");
    
    private final String indonesianName;
    
    // Constructor dengan 1 parameter
    private Day(String indonesianName) {
        this.indonesianName = indonesianName;
    }
    
    public String getIndonesianName() {
        return indonesianName;
    }
}

// Penggunaan
System.out.println(Day.MONDAY.getIndonesianName()); // Output: Senin
```

### Enum dengan banyak parameter:
```java
public enum Currency {
    USD("US Dollar", "$", 1.0),
    IDR("Indonesian Rupiah", "Rp", 15000.0),
    EUR("Euro", "€", 0.85);
    
    private final String fullName;
    private final String symbol;
    private final double exchangeRateToUSD;
    
    // Constructor dengan 3 parameter
    private Currency(String fullName, String symbol, double exchangeRateToUSD) {
        this.fullName = fullName;
        this.symbol = symbol;
        this.exchangeRateToUSD = exchangeRateToUSD;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public double getExchangeRateToUSD() {
        return exchangeRateToUSD;
    }
}

// Penggunaan
Currency idr = Currency.IDR;
System.out.println(idr.getFullName());    // Indonesian Rupiah
System.out.println(idr.getSymbol());      // Rp
System.out.println(idr.getExchangeRateToUSD()); // 15000.0
```

## Visualisasi Step by Step

```java
public enum Size {
    SMALL("S", 10),
    MEDIUM("M", 20),
    LARGE("L", 30);
    
    private final String code;
    private final int value;
    
    private Size(String code, int value) {
        this.code = code;
        this.value = value;
    }
}
```

**Yang terjadi saat Java load enum ini:**

```
Step 1: Java baca SMALL("S", 10)
        └─> Panggil constructor: Size("S", 10)
        └─> this.code = "S"
        └─> this.value = 10
        └─> Buat objek SMALL

Step 2: Java baca MEDIUM("M", 20)
        └─> Panggil constructor: Size("M", 20)
        └─> this.code = "M"
        └─> this.value = 20
        └─> Buat objek MEDIUM

Step 3: Java baca LARGE("L", 30)
        └─> Panggil constructor: Size("L", 30)
        └─> this.code = "L"
        └─> this.value = 30
        └─> Buat objek LARGE
```

## Kenapa Pakai Cara Ini?

Karena dengan cara ini, setiap konstanta enum bisa punya **data tambahan** yang berbeda-beda:

```java
public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error");
    
    private final int code;
    private final String message;
    
    private HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}

// Penggunaan
HttpStatus status = HttpStatus.NOT_FOUND;
System.out.println(status.getCode());    // 404
System.out.println(status.getMessage()); // Not Found
```

## Aturan Penting

1. **Urutan penulisan harus:**
   ```java
   public enum MyEnum {
       // 1. Konstanta HARUS di atas (paling pertama)
       CONSTANT1("value"),
       CONSTANT2("value");
       
       // 2. Field
       private final String field;
       
       // 3. Constructor
       private MyEnum(String field) {
           this.field = field;
       }
       
       // 4. Method
       public String getField() {
           return field;
       }
   }
   ```

2. **Konstanta terakhir pakai semicolon (;)** jika ada field/method setelahnya

3. **Constructor harus private** (tidak boleh public)

## Contoh Kesalahan Umum

```java
// ❌ SALAH - konstanta tidak di atas
public enum Status {
    private String description; // Error!
    
    PENDING("Menunggu"); // Error!
}

// ✅ BENAR
public enum Status {
    PENDING("Menunggu"); // Konstanta di atas
    
    private String description;
}
```

```java
// ❌ SALAH - lupa semicolon
public enum Status {
    PENDING("Menunggu"),
    COMPLETED("Selesai") // Error! Harus pakai semicolon jika ada field
    
    private String description;
}

// ✅ BENAR
public enum Status {
    PENDING("Menunggu"),
    COMPLETED("Selesai"); // Pakai semicolon
    
    private String description;
}
```

Apakah sekarang sudah lebih jelas? Intinya, nilai dalam kurung itu adalah **parameter yang dikirim ke constructor** enum tersebut.