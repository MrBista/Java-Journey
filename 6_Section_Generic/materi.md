# Belajar Generic di Java - Panduan Lengkap untuk Pemula

Baik, saya akan menjelaskan Generic di Java dari dasar sampai mahir. Anggap kamu belum pernah dengar istilah ini sama sekali.

## Apa itu Generic?

Generic adalah fitur di Java yang memungkinkan kamu menulis kode yang bisa bekerja dengan berbagai tipe data, tapi tetap **type-safe** (aman dari kesalahan tipe data).

### Analogi Sederhana

Bayangkan kamu punya kotak penyimpanan. Tanpa generic, kotak itu bisa menyimpan apa saja (buku, sepatu, makanan) dan kamu tidak tahu apa isinya sampai dibuka. Dengan generic, kamu bisa menandai kotak: "Kotak khusus buku" atau "Kotak khusus sepatu", jadi kamu tahu pasti isinya dan tidak akan salah ambil.

## Mengapa Generic Penting?

### 1. Tanpa Generic (Cara Lama)

```java
// Tanpa generic - berbahaya!
ArrayList daftar = new ArrayList();
daftar.add("Apel");
daftar.add("Jeruk");
daftar.add(123); // Bisa masuk angka juga! Bahaya!

String buah = (String) daftar.get(0); // Harus casting manual
String buah2 = (String) daftar.get(2); // Runtime Error! Karena isi angka
```

**Masalahnya:**
- Bisa memasukkan tipe data apa saja (string, angka, object)
- Harus melakukan casting manual saat mengambil data
- Error baru ketahuan saat program jalan (runtime), bukan saat compile

### 2. Dengan Generic (Cara Modern)

```java
// Dengan generic - aman!
ArrayList<String> daftar = new ArrayList<String>();
daftar.add("Apel");
daftar.add("Jeruk");
// daftar.add(123); // Compile error! Tidak bisa masuk

String buah = daftar.get(0); // Tidak perlu casting
```

**Keuntungannya:**
- Hanya bisa menyimpan satu tipe data tertentu
- Tidak perlu casting
- Error langsung ketahuan saat compile (sebelum program jalan)

## Sintaks Dasar Generic

### 1. Menggunakan Generic di Collection

```java
// Format: NamaClass<TipeData>
ArrayList<String> listNama = new ArrayList<String>();
ArrayList<Integer> listAngka = new ArrayList<Integer>();
ArrayList<Double> listHarga = new ArrayList<Double>();

// Java 7+ bisa pakai Diamond Operator
ArrayList<String> listNama2 = new ArrayList<>(); // Lebih singkat
```

### 2. Tipe-Tipe Generic yang Umum

```java
// List
List<String> list = new ArrayList<>();

// Set (tidak ada duplikat)
Set<Integer> set = new HashSet<>();

// Map (pasangan key-value)
Map<String, Integer> map = new HashMap<>();
map.put("umur", 25);
map.put("tinggi", 170);

// Queue
Queue<String> queue = new LinkedList<>();
```

## Membuat Class Generic Sendiri

### 1. Generic Class Sederhana

```java
// T adalah placeholder untuk tipe data
public class Kotak<T> {
    private T isi;
    
    public void masukkan(T item) {
        this.isi = item;
    }
    
    public T ambil() {
        return isi;
    }
}

// Cara pakai:
Kotak<String> kotakString = new Kotak<>();
kotakString.masukkan("Halo");
String hasil = kotakString.ambil(); // "Halo"

Kotak<Integer> kotakAngka = new Kotak<>();
kotakAngka.masukkan(100);
Integer angka = kotakAngka.ambil(); // 100
```

### 2. Generic dengan Multiple Type Parameters

```java
public class Pasangan<K, V> {
    private K kunci;
    private V nilai;
    
    public Pasangan(K kunci, V nilai) {
        this.kunci = kunci;
        this.nilai = nilai;
    }
    
    public K getKunci() {
        return kunci;
    }
    
    public V getNilai() {
        return nilai;
    }
}

// Cara pakai:
Pasangan<String, Integer> umur = new Pasangan<>("Ali", 25);
System.out.println(umur.getKunci()); // "Ali"
System.out.println(umur.getNilai()); // 25
```

## Generic Methods (Method Generic)

Kamu bisa membuat method yang generic tanpa harus membuat classnya generic:

```java
public class Utility {
    // Method generic
    public static <T> void cetakArray(T[] array) {
        for (T item : array) {
            System.out.println(item);
        }
    }
    
    // Method dengan return type generic
    public static <T> T ambilPertama(T[] array) {
        if (array.length > 0) {
            return array[0];
        }
        return null;
    }
}

// Cara pakai:
String[] nama = {"Ali", "Budi", "Citra"};
Integer[] angka = {1, 2, 3, 4, 5};

Utility.cetakArray(nama);   // Cetak string
Utility.cetakArray(angka);  // Cetak integer

String pertama = Utility.ambilPertama(nama); // "Ali"
```

## Bounded Type Parameters (Batasan Tipe)

Kadang kamu ingin membatasi tipe data apa saja yang boleh digunakan di generic.

### 1. Upper Bound (extends)

```java
// Hanya menerima Number dan turunannya (Integer, Double, dll)
public class KalkulatorAngka<T extends Number> {
    private T angka;
    
    public KalkulatorAngka(T angka) {
        this.angka = angka;
    }
    
    public double kaliDua() {
        return angka.doubleValue() * 2;
    }
}

// Cara pakai:
KalkulatorAngka<Integer> kalk1 = new KalkulatorAngka<>(10);
System.out.println(kalk1.kaliDua()); // 20.0

KalkulatorAngka<Double> kalk2 = new KalkulatorAngka<>(5.5);
System.out.println(kalk2.kaliDua()); // 11.0

// KalkulatorAngka<String> kalk3 = new KalkulatorAngka<>("test"); // ERROR!
```

### 2. Multiple Bounds

```java
// T harus implement Comparable DAN Serializable
public class Sorter<T extends Comparable<T> & Serializable> {
    public T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }
}
```

## Wildcards (Tanda ?)

Wildcards digunakan saat kamu tidak tahu atau tidak peduli tipe data spesifiknya.

### 1. Unbounded Wildcard (?)

```java
public static void cetakList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

// Bisa menerima list tipe apa saja
List<String> listString = Arrays.asList("A", "B", "C");
List<Integer> listInteger = Arrays.asList(1, 2, 3);

cetakList(listString);
cetakList(listInteger);
```

### 2. Upper Bounded Wildcard (? extends)

```java
// Menerima List yang isinya Number atau turunannya
public static double jumlahkan(List<? extends Number> list) {
    double total = 0;
    for (Number num : list) {
        total += num.doubleValue();
    }
    return total;
}

List<Integer> listInt = Arrays.asList(1, 2, 3);
List<Double> listDouble = Arrays.asList(1.5, 2.5, 3.5);

System.out.println(jumlahkan(listInt));    // 6.0
System.out.println(jumlahkan(listDouble)); // 7.5
```

### 3. Lower Bounded Wildcard (? super)

```java
// Menerima List yang isinya Integer atau parent classnya
public static void tambahInteger(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}

List<Number> listNumber = new ArrayList<>();
List<Object> listObject = new ArrayList<>();

tambahInteger(listNumber); // OK
tambahInteger(listObject); // OK
```

## PECS Principle (Producer Extends, Consumer Super)

Ini adalah aturan penting dalam memilih wildcard:

- **Gunakan `extends`** jika kamu hanya **membaca** (producing) dari collection
- **Gunakan `super`** jika kamu hanya **menulis** (consuming) ke collection

```java
// Producer (membaca) - gunakan extends
public static void bacaData(List<? extends Number> list) {
    for (Number num : list) {
        System.out.println(num);
    }
    // list.add(10); // ERROR! Tidak bisa menambah
}

// Consumer (menulis) - gunakan super
public static void tulisData(List<? super Integer> list) {
    list.add(10);
    list.add(20);
    // Integer x = list.get(0); // Tidak ideal, return Object
}
```

## Type Erasure (Penghapusan Tipe)

Java menghapus informasi generic saat runtime. Ini penting untuk dipahami:

```java
List<String> listString = new ArrayList<>();
List<Integer> listInteger = new ArrayList<>();

// Saat runtime, keduanya menjadi List biasa (tanpa tipe)
System.out.println(listString.getClass() == listInteger.getClass()); // true
```

**Implikasi:**
- Tidak bisa membuat array generic: `new T[10]` ❌
- Tidak bisa mengecek tipe generic di runtime: `if (obj instanceof List<String>)` ❌
- Tidak bisa membuat instance generic: `new T()` ❌

## Best Practices (Praktik Terbaik)

### 1. Gunakan Generic di Collection

```java
// ❌ Buruk - tidak type safe
List list = new ArrayList();

// ✅ Baik - type safe
List<String> list = new ArrayList<>();
```

### 2. Naming Convention untuk Type Parameters

```java
// Konvensi umum:
// E - Element (digunakan di Collection)
// K - Key
// V - Value
// N - Number
// T - Type
// S, U, V - untuk tipe kedua, ketiga, keempat

public class Box<T> { } // Single type
public class Pair<K, V> { } // Key-Value pair
public class Triple<T, S, U> { } // Multiple types
```

### 3. Lebih Spesifik Lebih Baik

```java
// ❌ Terlalu umum
public void proses(List<?> list) { }

// ✅ Lebih baik - jelas tipe datanya
public void proses(List<String> list) { }
```

### 4. Gunakan Bounded Type Saat Perlu Method Spesifik

```java
// ❌ Tidak bisa akses method dari Number
public class Calculator<T> {
    public double kali(T a, T b) {
        // return a * b; // ERROR! T tidak punya operator *
    }
}

// ✅ Dengan bound, bisa akses method Number
public class Calculator<T extends Number> {
    public double kali(T a, T b) {
        return a.doubleValue() * b.doubleValue();
    }
}
```

### 5. Preferensi List<T> daripada T[]

```java
// ❌ Array generic bermasalah
public <T> T[] buatArray(int size) {
    // return new T[size]; // ERROR!
    return null;
}

// ✅ Gunakan List
public <T> List<T> buatList() {
    return new ArrayList<>();
}
```

### 6. Gunakan Diamond Operator (Java 7+)

```java
// ❌ Verbose
Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

// ✅ Lebih singkat
Map<String, List<Integer>> map = new HashMap<>();
```

### 7. Avoid Raw Types

```java
// ❌ Raw type - bahaya!
List list = new ArrayList();

// ✅ Gunakan generic
List<Object> list = new ArrayList<>(); // Jika memang perlu Object
```

### 8. Return Type yang Paling Umum

```java
// ✅ Return interface, bukan implementation
public List<String> getNames() {
    return new ArrayList<>(); // Implementation detail
}

// ❌ Terlalu spesifik
public ArrayList<String> getNames() {
    return new ArrayList<>();
}
```

## Contoh Kasus Nyata

### 1. Repository Pattern

```java
public interface Repository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    void save(T entity);
    void delete(ID id);
}

public class UserRepository implements Repository<User, Long> {
    @Override
    public User findById(Long id) {
        // implementasi mencari user by ID
        return null;
    }
    
    @Override
    public List<User> findAll() {
        // implementasi ambil semua user
        return new ArrayList<>();
    }
    
    @Override
    public void save(User entity) {
        // implementasi simpan user
    }
    
    @Override
    public void delete(Long id) {
        // implementasi hapus user
    }
}
```

### 2. Response Wrapper

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Getters dan setters
}

// Cara pakai:
ApiResponse<User> userResponse = new ApiResponse<>(true, "Success", user);
ApiResponse<List<Product>> productResponse = new ApiResponse<>(true, "Success", products);
```

### 3. Builder Pattern dengan Generic

```java
public class QueryBuilder<T> {
    private String table;
    private List<String> conditions = new ArrayList<>();
    
    public QueryBuilder<T> from(String table) {
        this.table = table;
        return this;
    }
    
    public QueryBuilder<T> where(String condition) {
        conditions.add(condition);
        return this;
    }
    
    public List<T> execute() {
        // Eksekusi query dan return hasil
        return new ArrayList<>();
    }
}

// Cara pakai dengan method chaining:
List<User> users = new QueryBuilder<User>()
    .from("users")
    .where("age > 18")
    .where("active = true")
    .execute();
```

## Kesalahan Umum yang Harus Dihindari

### 1. Menggunakan Raw Type

```java
// ❌ Salah
List list = new ArrayList();
list.add("string");
list.add(123);

// ✅ Benar
List<Object> list = new ArrayList<>();
```

### 2. Salah Gunakan Wildcard

```java
// ❌ Tidak bisa menambah karena extends
public void tambah(List<? extends Number> list) {
    // list.add(10); // ERROR!
}

// ✅ Gunakan super untuk menambah
public void tambah(List<? super Integer> list) {
    list.add(10); // OK
}
```

### 3. Membuat Generic Array

```java
// ❌ Tidak bisa
public <T> T[] buatArray() {
    // return new T[10]; // ERROR!
}

// ✅ Gunakan List
public <T> List<T> buatList() {
    return new ArrayList<>();
}
```

## Kesimpulan

Generic di Java adalah tool yang powerful untuk:
- **Type Safety**: Mencegah error tipe data
- **Code Reusability**: Satu kode untuk banyak tipe
- **Readability**: Kode lebih jelas dan mudah dibaca
- **Performance**: Tidak perlu casting berulang

Mulai dengan menggunakan generic di collection (List, Set, Map), lalu perlahan belajar membuat class dan method generic sendiri. Dengan latihan, kamu akan terbiasa dan kode kamu jadi lebih professional!

Ada yang mau ditanyakan lebih lanjut tentang generic?