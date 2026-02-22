# Java Thread — Chapter 2: Race Condition & Synchronized

> **Target:** Mid Java Developer yang ingin naik ke Senior
> **Pendekatan:** Masalah → Solusi Salah → Solusi Benar → Kenapa

---

## Apa itu Race Condition?

Race condition terjadi ketika **dua atau lebih thread mengakses data yang sama secara bersamaan**, dan hasil akhirnya bergantung pada siapa yang "menang" duluan — bukan pada logika program kamu.

Ini adalah bug paling berbahaya di dunia concurrency karena:
- Tidak selalu reproducible — kadang jalan normal, kadang hasilnya salah
- Tidak ada exception yang dilempar — program jalan terus dengan data yang korup
- Susah dideteksi di development, sering baru muncul di production saat load tinggi

---

## Kenapa value++ Tidak Aman?

Ini pertanyaan fundamental yang harus kamu jawab tanpa ragu kalau mau naik ke senior.

`value++` terlihat seperti 1 operasi, tapi di level CPU sebenarnya **3 langkah terpisah:**

```
1. READ  → ambil nilai value dari memori ke register CPU
2. ADD   → tambahkan 1 di register CPU
3. WRITE → simpan kembali ke memori
```

Masalahnya, OS bisa meng-interrupt thread **di antara langkah-langkah ini** dan pindah ke thread lain. Inilah yang terjadi:

```
Nilai awal: value = 5

Thread A: READ  → dapat 5
Thread A: ADD   → hitung 6
                            Thread B: READ  → dapat 5  ← masih 5! A belum WRITE
                            Thread B: ADD   → hitung 6
                            Thread B: WRITE → simpan 6
Thread A: WRITE → simpan 6

Hasil akhir: value = 6
Harusnya:    value = 7  ← satu operasi HILANG!
```

---

## Masalah 1: Tidak Ada Proteksi Sama Sekali

### ❌ Solusi yang Salah

```java
class Counter {
    private int value = 0;

    public void increment() {
        value++; // TIDAK AMAN
    }

    public int getValue() {
        return value;
    }
}

Counter counter = new Counter();
ExecutorService executor = Executors.newFixedThreadPool(10);

for (int i = 0; i < 10_000; i++) {
    executor.execute(counter::increment);
}

executor.shutdown();
executor.awaitTermination(5, TimeUnit.SECONDS);

System.out.println(counter.getValue());
// Harusnya 10.000
// Aktualnya: 8.743 atau 9.211 atau angka lain — berubah tiap run!
```

### ✅ Solusi yang Benar: Synchronized Method

```java
class Counter {
    private int value = 0;

    // Hanya 1 thread yang bisa masuk method ini pada satu waktu
    public synchronized void increment() {
        value++;
    }

    public synchronized int getValue() {
        return value;
    }
}
```

Tapi ini baru permulaan. Ada banyak jebakan di synchronized yang perlu kamu tahu.

---

## Masalah 2: Synchronized Hanya di Write, Tidak di Read

Ini kesalahan yang sangat umum di mid-level developer.

### ❌ Solusi yang Salah

```java
class Counter {
    private int value = 0;

    public synchronized void increment() {
        value++;
    }

    // ❌ Lupa synchronized di getter!
    public int getValue() {
        return value; // Thread lain bisa baca nilai yang stale (lama)
    }
}
```

**Kenapa ini salah?**

Kalau thread A sedang `increment()` dan thread B memanggil `getValue()` tanpa synchronized, thread B mungkin membaca nilai lama dari cache CPU-nya sendiri — bukan nilai terbaru yang sudah ditulis thread A ke memori utama. Ini disebut **visibility problem**.

### ✅ Solusi yang Benar

```java
class Counter {
    private int value = 0;

    public synchronized void increment() {
        value++;
    }

    // ✅ Getter juga harus synchronized
    public synchronized int getValue() {
        return value;
    }
}
```

> **Aturan:** Kalau write-nya di-synchronized, read-nya juga harus di-synchronized.

---

## Masalah 3: Synchronized di Object yang Salah

Ini jebakan paling halus. Kodenya terlihat benar tapi tidak melindungi apapun.

### ❌ Solusi yang Salah

```java
class Counter {
    private Integer value = 0; // ← Integer, bukan int

    public void increment() {
        synchronized (value) { // ❌ SALAH BESAR!
            value++;
        }
    }
}
```

**Kenapa ini salah?**

`Integer` di Java adalah **immutable**. Ketika kamu melakukan `value++`, Java sebenarnya membuat **object Integer baru**. Jadi object yang dijadikan lock terus berganti! Thread A lock `Integer(5)`, lalu value berubah jadi `Integer(6)` — object lock-nya sudah berbeda. Thread B tidak terblokir karena dia lock object yang berbeda juga.

### ✅ Solusi yang Benar

```java
class Counter {
    private int value = 0;
    private final Object lock = new Object(); // ← dedicated lock object, final!

    public void increment() {
        synchronized (lock) { // ✅ Lock object yang sama dan tidak berubah
            value++;
        }
    }
}
```

> **Aturan:** Lock object harus `final` — jangan pernah synchronized di object yang bisa berubah referensinya.

---

## Masalah 4: Synchronized Method vs Synchronized Statement

### Kapan Pakai Synchronized Method?

```java
class BankAccount {
    private double balance;

    // ✅ Oke kalau SELURUH method memang perlu di-protect
    public synchronized void deposit(double amount) {
        balance += amount;
    }

    public synchronized double getBalance() {
        return balance;
    }
}
```

### Kapan Pakai Synchronized Statement?

Kalau method punya bagian yang tidak perlu di-protect — lock seluruh method adalah pemborosan dan memperlambat throughput.

### ❌ Solusi yang Kurang Tepat

```java
class UserService {
    private Map<String, User> cache = new HashMap<>();

    public synchronized User getUser(String id) {
        // Bagian 1: cek cache — perlu lock
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        // Bagian 2: query ke database — ini LAMBAT (100-200ms)
        // Tidak perlu di-lock! Thread lain bisa jalan sambil ini berjalan
        User user = database.findById(id); // ← tapi tetap ter-lock!

        // Bagian 3: simpan ke cache — perlu lock
        cache.put(id, user);
        return user;
    }
}
```

### ✅ Solusi yang Benar

```java
class UserService {
    private Map<String, User> cache = new HashMap<>();
    private final Object cacheLock = new Object();

    public User getUser(String id) {
        // Cek cache — lock sesempit mungkin
        synchronized (cacheLock) {
            if (cache.containsKey(id)) {
                return cache.get(id);
            }
        }

        // Query database — TIDAK di-lock, thread lain bisa jalan paralel
        User user = database.findById(id);

        // Simpan ke cache — lock lagi sesaat
        synchronized (cacheLock) {
            cache.put(id, user);
        }

        return user;
    }
}
```

> **Prinsip Senior:** *Lock sesempit mungkin, sesingkat mungkin.* Lock yang terlalu besar membunuh performa — sama seperti kalau kamu kasih satu kasir untuk seluruh supermarket.

---

## Masalah 5: Check-Then-Act yang Tidak Atomic

Ini race condition yang sangat halus dan sering lolos code review.

### ❌ Solusi yang Salah

```java
class Singleton {
    private static Singleton instance;

    // ❌ Double-check locking yang salah (pre-Java 5)
    public static Singleton getInstance() {
        if (instance == null) {              // ← Thread A & B bisa sama-sama lewat sini
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton(); // ← bisa dibaca setengah jadi!
                }
            }
        }
        return instance;
    }
}
```

**Kenapa masih salah?**

`instance = new Singleton()` sebenarnya 3 langkah:
1. Alokasi memori
2. Inisialisasi object
3. Assign referensi ke `instance`

Compiler/JVM bisa **mereorder** langkah ini menjadi 1→3→2. Thread lain melihat `instance != null` tapi object belum selesai diinisialisasi!

### ✅ Solusi yang Benar

```java
class Singleton {
    // volatile memastikan urutan write/read tidak di-reorder oleh JVM
    private static volatile Singleton instance;

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// Atau cara yang lebih elegan — Initialization-on-demand holder:
class SingletonBetter {
    private SingletonBetter() {}

    private static class Holder {
        static final SingletonBetter INSTANCE = new SingletonBetter();
    }

    public static SingletonBetter getInstance() {
        return Holder.INSTANCE; // Thread-safe by JVM class loading guarantee
    }
}
```

---

## Volatile — Saudara Dekat Synchronized

`volatile` sering disalahpahami. Ini bukan pengganti `synchronized`, tapi punya peran spesifik.

**Apa yang dilakukan `volatile`:**
- Memastikan write ke variable langsung ke memori utama (bukan CPU cache)
- Memastikan read selalu dari memori utama
- Mencegah instruction reordering oleh JVM/compiler

**Apa yang TIDAK dilakukan `volatile`:**
- Tidak membuat operasi compound (seperti `value++`) menjadi atomic

```java
// ✅ volatile tepat untuk flag sederhana
class Worker {
    private volatile boolean running = true; // bukan AtomicBoolean pun oke di sini

    public void run() {
        while (running) {    // ← selalu baca dari memori utama
            doWork();
        }
    }

    public void stop() {
        running = false;     // ← langsung tulis ke memori utama
    }
}

// ❌ volatile TIDAK cukup untuk operasi compound
class Counter {
    private volatile int value = 0;

    public void increment() {
        value++; // ← MASIH TIDAK AMAN! volatile tidak bikin ++ jadi atomic
    }
}
```

---

## Intrinsic Lock — Yang Terjadi di Balik synchronized

Setiap object di Java punya sebuah **intrinsic lock** (disebut juga monitor lock). Ketika kamu menulis `synchronized`, kamu sebenarnya sedang:

```
1. Thread mencoba ACQUIRE intrinsic lock dari object
2. Kalau lock bebas → thread dapat lock, masuk ke blok synchronized
3. Kalau lock sedang dipegang thread lain → thread masuk state BLOCKED, menunggu
4. Setelah blok selesai (sukses atau exception) → thread RELEASE lock
5. JVM memilih salah satu thread yang menunggu untuk dapat lock berikutnya
```

```java
class Counter {
    private int value = 0;

    // "this" adalah object yang lock-nya dipakai
    public synchronized void increment() {
        // ↑ sama dengan:
        // synchronized (this) { value++; }
        value++;
    }
}

// Untuk static method, lock-nya adalah Class object:
class Helper {
    public static synchronized void doSomething() {
        // sama dengan: synchronized (Helper.class) { ... }
    }
}
```

**Implikasi penting:**

```java
class Counter {
    private int value = 0;

    public synchronized void increment() { value++; }
    public synchronized void decrement() { value--; }
    public synchronized int getValue() { return value; }
}

Counter c = new Counter();

// Kalau thread A sedang di increment(),
// thread B yang mau panggil decrement() atau getValue() juga harus MENUNGGU.
// Karena ketiganya pakai lock yang SAMA (object 'c').
```

---

## Studi Kasus: Race Condition yang Tidak Obvious

Ini contoh yang sering muncul di code review senior level.

### ❌ Kode yang Terlihat Aman tapi Tidak

```java
class OnlineStore {
    private Map<String, Integer> stock = new HashMap<>();

    public boolean purchase(String productId, int quantity) {
        // Langkah 1: cek stok
        int currentStock = stock.getOrDefault(productId, 0);

        if (currentStock < quantity) {
            return false; // stok tidak cukup
        }

        // ← DI SINI, thread lain bisa masuk dan juga lolos pengecekan!

        // Langkah 2: kurangi stok
        stock.put(productId, currentStock - quantity);
        return true;
    }
}
```

Kalau stok = 1 dan 2 user beli bersamaan, keduanya bisa lolos pengecekan dan stok jadi -1.

### ✅ Solusi yang Benar

```java
class OnlineStore {
    private Map<String, Integer> stock = new HashMap<>();
    private final Object stockLock = new Object();

    public boolean purchase(String productId, int quantity) {
        synchronized (stockLock) {
            // Cek dan kurangi harus dalam satu blok synchronized yang sama
            int currentStock = stock.getOrDefault(productId, 0);

            if (currentStock < quantity) {
                return false;
            }

            stock.put(productId, currentStock - quantity);
            return true;
        }
        // Operasi "check-then-act" harus ATOMIC — tidak boleh ada jeda
    }
}
```

> **Prinsip Senior:** Selalu tanya — *"apakah ada state yang bisa berubah di antara operasi-operasi ini?"* Kalau iya, mereka harus dibungkus dalam satu synchronized block.

---

## Ringkasan Prinsip Chapter 2

1. **`value++` bukan operasi atomic** — di baliknya ada READ, ADD, WRITE yang bisa diinterrupt
2. **Synchronized read dan write** — kalau write-nya di-protected, read-nya juga harus
3. **Lock object harus `final`** — jangan synchronized di object yang bisa berubah
4. **Lock sesempit mungkin** — jangan lock bagian yang tidak perlu, throughput menurun
5. **Check-then-act harus atomic** — cek stok dan kurangi stok harus dalam satu blok
6. **`volatile` bukan pengganti synchronized** — hanya untuk visibility, bukan atomicity
7. **Semua method yang akses shared state harus pakai lock yang sama** — konsistensi kunci

---

*Selanjutnya → Chapter 3: Deadlock & Cara Menghindarinya*
