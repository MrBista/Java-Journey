main materi: https://docs.google.com/presentation/d/154ri_lNOQB8lXMWQMxw2l2NXhIgCQSaCFtBtZ0VxGAw/edit?slide=id.p#slide=id.p


ai materi:



# Belajar Lambda di Java - Panduan Lengkap untuk Pemula

Saya akan mengajarkan lambda di Java dari dasar sekali. Mari kita mulai step by step!

## 1. Apa itu Lambda?

Lambda adalah cara singkat untuk menulis **fungsi anonim** (fungsi tanpa nama). Lambda diperkenalkan di Java 8 untuk membuat kode lebih ringkas dan mudah dibaca.

**Analogi sederhana:** Bayangkan kamu ingin menyuruh seseorang melakukan sesuatu. Daripada menulis instruksi panjang di kertas (seperti class biasa), kamu cukup bilang langsung "lakukan ini!" (seperti lambda).

## 2. Sintaks Dasar Lambda

```
(parameter) -> { body }
```

- `(parameter)`: input yang diterima
- `->`: tanda panah (dibaca "arrow" atau "goes to")
- `{ body }`: kode yang akan dijalankan

## 3. Contoh Perbandingan: Sebelum vs Sesudah Lambda

### Cara Lama (Tanpa Lambda):
```java
// Membuat interface
interface Salam {
    void sayHello(String nama);
}

// Implementasi dengan anonymous class
Salam salam = new Salam() {
    @Override
    public void sayHello(String nama) {
        System.out.println("Halo " + nama);
    }
};

salam.sayHello("Budi"); // Output: Halo Budi
```

### Cara Baru (Dengan Lambda):
```java
// Sama, tapi jauh lebih singkat!
Salam salam = (nama) -> {
    System.out.println("Halo " + nama);
};

salam.sayHello("Budi"); // Output: Halo Budi
```

Lihat? Kode lebih pendek dan mudah dibaca!

## 4. Aturan Penulisan Lambda

### a) Lambda tanpa parameter:
```java
() -> System.out.println("Halo Dunia!");
```

### b) Lambda dengan 1 parameter (tanda kurung bisa dihilangkan):
```java
nama -> System.out.println("Halo " + nama);
// atau
(nama) -> System.out.println("Halo " + nama);
```

### c) Lambda dengan banyak parameter:
```java
(a, b) -> a + b
```

### d) Lambda dengan 1 baris kode (kurung kurawal bisa dihilangkan):
```java
x -> x * 2
```

### e) Lambda dengan banyak baris kode:
```java
(a, b) -> {
    int hasil = a + b;
    System.out.println("Hasilnya: " + hasil);
    return hasil;
}
```

## 5. Functional Interface - Syarat Lambda

Lambda hanya bisa digunakan dengan **Functional Interface**, yaitu interface yang hanya punya **1 method abstract**.

```java
@FunctionalInterface  // Opsional, tapi bagus untuk kejelasan
interface Kalkulator {
    int hitung(int a, int b);
}

// Penggunaan lambda
Kalkulator tambah = (a, b) -> a + b;
Kalkulator kali = (a, b) -> a * b;

System.out.println(tambah.hitung(5, 3));  // 8
System.out.println(kali.hitung(5, 3));     // 15
```

## 6. Functional Interface Bawaan Java

Java sudah menyediakan banyak functional interface di package `java.util.function`:

### a) **Predicate<T>** - untuk kondisi (return boolean)
```java
import java.util.function.Predicate;

Predicate<Integer> lebihDari10 = angka -> angka > 10;

System.out.println(lebihDari10.test(15));  // true
System.out.println(lebihDari10.test(5));   // false
```

### b) **Function<T, R>** - untuk transformasi (terima T, return R)
```java
import java.util.function.Function;

Function<String, Integer> hitungPanjang = teks -> teks.length();

System.out.println(hitungPanjang.apply("Halo"));  // 4
```

### c) **Consumer<T>** - untuk aksi (terima T, tidak return)
```java
import java.util.function.Consumer;

Consumer<String> cetak = teks -> System.out.println(teks);

cetak.accept("Halo Dunia!");  // Output: Halo Dunia!
```

### d) **Supplier<T>** - untuk supply nilai (tidak terima, return T)
```java
import java.util.function.Supplier;

Supplier<Double> randomNumber = () -> Math.random();

System.out.println(randomNumber.get());  // angka random
```

## 7. Lambda dengan Collections - Contoh Praktis

Ini adalah penggunaan lambda yang paling sering kamu temui!

```java
import java.util.*;

List<String> nama = Arrays.asList("Budi", "Ani", "Citra", "Dedi");

// 1. forEach - iterasi setiap elemen
nama.forEach(n -> System.out.println(n));

// 2. Filter - saring data
nama.stream()
    .filter(n -> n.startsWith("C"))
    .forEach(System.out::println);  // Output: Citra

// 3. Map - transformasi data
nama.stream()
    .map(n -> n.toUpperCase())
    .forEach(System.out::println);  // BUDI, ANI, CITRA, DEDI

// 4. Sort - urutkan
nama.sort((a, b) -> a.compareTo(b));
System.out.println(nama);  // [Ani, Budi, Citra, Dedi]
```

## 8. Latihan Praktis

Coba praktekkan kode ini untuk memahami lebih dalam:

```java
import java.util.*;
import java.util.function.*;

public class BelajarLambda {
    public static void main(String[] args) {
        
        // Latihan 1: Filter angka genap
        List<Integer> angka = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        System.out.println("Angka genap:");
        angka.stream()
             .filter(n -> n % 2 == 0)
             .forEach(n -> System.out.print(n + " "));
        // Output: 2 4 6 8 10
        
        System.out.println("\n\nAngka dikali 2:");
        angka.stream()
             .map(n -> n * 2)
             .forEach(n -> System.out.print(n + " "));
        // Output: 2 4 6 8 10 12 14 16 18 20
        
        // Latihan 2: Kalkulator sederhana
        BiFunction<Integer, Integer, Integer> tambah = (a, b) -> a + b;
        BiFunction<Integer, Integer, Integer> kurang = (a, b) -> a - b;
        
        System.out.println("\n\n5 + 3 = " + tambah.apply(5, 3));
        System.out.println("5 - 3 = " + kurang.apply(5, 3));
        
        // Latihan 3: Validasi
        Predicate<String> emailValid = email -> email.contains("@");
        
        System.out.println("\nApakah 'user@email.com' valid? " + 
                          emailValid.test("user@email.com"));  // true
        System.out.println("Apakah 'useremail.com' valid? " + 
                          emailValid.test("useremail.com"));   // false
    }
}
```

## 9. Tips Belajar Lambda

1. **Mulai dari yang sederhana** - Praktek lambda dengan 1 parameter dulu
2. **Gunakan di Collections** - forEach, filter, map adalah tempat terbaik belajar
3. **Jangan bingung dengan sintaks** - Ingat pola: `(input) -> output`
4. **Practice makes perfect** - Tulis ulang kode lama dengan lambda

## 10. Kesalahan Umum Pemula

❌ **Salah:**
```java
// Lupa tanda kurung untuk banyak parameter
a, b -> a + b  // ERROR!
```

✅ **Benar:**
```java
(a, b) -> a + b
```

❌ **Salah:**
```java
// Lupa return jika ada kurung kurawal
(a, b) -> {
    a + b  // ERROR! Tidak ada return
}
```

✅ **Benar:**
```java
(a, b) -> {
    return a + b;
}
// atau lebih simple:
(a, b) -> a + b
```

---

**Tantangan untuk kamu:** Coba buat program sederhana yang:
1. Membuat List nama-nama teman
2. Filter nama yang panjangnya lebih dari 4 huruf
3. Ubah semua nama menjadi huruf besar
4. Cetak hasilnya

Kalau ada yang masih bingung atau mau bahas topik tertentu lebih dalam, tanya aja! Saya siap bantu. 😊