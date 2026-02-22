# Java Thread — Chapter 1: Thread Dasar yang Benar

> **Target:** Mid Java Developer yang ingin naik ke Senior
> **Pendekatan:** Masalah → Solusi Salah → Solusi Benar → Kenapa

---

## Apa yang Thread Selesaikan?

Sebelum nulis satu baris kode pun, pahami dulu **masalah apa yang coba diselesaikan Thread**.

Bayangkan kamu punya aplikasi yang harus:
1. Download file dari internet (butuh 5 detik)
2. Sambil itu, tetap melayani input dari user
3. Sambil itu, auto-save progress setiap 30 detik

Kalau kodenya **sequential** (satu per satu), user harus nunggu download selesai dulu baru bisa klik tombol apapun. Aplikasi terasa "freeze". Itulah masalah yang Thread selesaikan.

---

## Yang Terjadi di Balik Layar

Ketika aplikasi Java jalan, OS mengalokasikan sebuah **Process**. Di dalam process itu ada minimal 1 **Thread** — yang kamu kenal sebagai thread `main`.

```
OS
└── JVM Process
    ├── Heap Memory     ← SHARED oleh semua thread
    ├── Method Area     ← SHARED
    └── Threads:
        ├── main thread → Stack SENDIRI (private)
        ├── GC thread   → Stack SENDIRI (private)
        └── thread-mu   → Stack SENDIRI (private)
```

**Ini penting:**
- **Heap = SHARED** → semua thread bisa akses object yang sama → ini sumber masalah race condition nanti
- **Stack = PRIVATE** → variabel lokal di dalam method aman, tidak bisa diakses thread lain

---

## Masalah 1: Extend Thread Langsung

### ❌ Solusi yang Salah

```java
class DownloadTask extends Thread {
    @Override
    public void run() {
        System.out.println("Downloading...");
    }
}

new DownloadTask().start();
```

**Kenapa salah?**
Kamu **menggabungkan dua hal yang berbeda**: *apa yang dikerjakan* (task) dengan *siapa yang mengerjakan* (thread). Kalau besok mau jalankan task yang sama di ThreadPool, kamu tidak bisa — karena task-nya sudah terikat ke class Thread.

### ✅ Solusi yang Benar

```java
// Task terpisah dari thread
Runnable downloadTask = () -> {
    System.out.println("Downloading...");
};

// Sekarang task ini fleksibel, bisa dijalankan dengan cara apapun:
new Thread(downloadTask).start();           // thread baru
executor.execute(downloadTask);             // thread pool
CompletableFuture.runAsync(downloadTask);   // async modern
```

> **Prinsip Senior:** *Runnable/Callable adalah "resep", Thread/Executor adalah "dapur". Pisahkan keduanya.*

---

## Masalah 2: Salah Paham start() vs run()

### ❌ Solusi yang Salah

```java
Runnable tugas = () -> {
    System.out.println("Thread: " + Thread.currentThread().getName());
};

tugas.run(); // Output: Thread: main
             // Tidak ada thread baru! Tetap jalan di main thread!
```

### ✅ Solusi yang Benar

```java
new Thread(tugas).start(); // Output: Thread: Thread-0
                           // Thread baru dibuat dan berjalan!
```

**Perbedaannya fundamental:**
- `.run()` → hanya memanggil method biasa, tidak ada thread baru dibuat
- `.start()` → minta OS buat thread baru, OS jadwalkan eksekusi `run()` di thread itu

---

## Masalah 3: Mengira Thread Jalan Berurutan

### ❌ Asumsi yang Salah

```java
Thread t1 = new Thread(() -> System.out.println("A"));
Thread t2 = new Thread(() -> System.out.println("B"));
Thread t3 = new Thread(() -> System.out.println("C"));

t1.start();
t2.start();
t3.start();

// ❌ Jangan berasumsi outputnya selalu A → B → C
// Bisa jadi: B, A, C
// Bisa jadi: C, A, B
// Urutannya ditentukan OS scheduler, BUKAN kode kamu!
```

Ini bukan bug — ini memang sifat dasar concurrency. Senior developer selalu menulis kode yang **tidak berasumsi** soal urutan eksekusi thread.

---

## Thread Sleep — Handle InterruptedException dengan Benar

### ❌ Solusi yang Salah (Paling Umum)

```java
// Menelan exception — BAHAYA!
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // dibiarkan kosong
}

// Hanya print, tapi thread terus jalan
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    e.printStackTrace(); // tapi tidak berhenti!
}
```

### ✅ Solusi yang Benar

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // kembalikan sinyal interrupt
    return;                             // atau break dari loop
}
```

**Kenapa harus `Thread.currentThread().interrupt()`?**
`InterruptedException` otomatis **mengclear flag interrupt**. Kalau tidak di-restore, kode di atasmu yang mengecek `isInterrupted()` tidak akan pernah tahu thread ini sudah diinterrupt. Data bisa corrupt, resource bisa bocor.

---

## Thread Join — Jangan Infinite Wait

### ❌ Solusi yang Salah

```java
// Kalau thread hang, program hang selamanya
t1.join();
```

### ✅ Solusi yang Benar

```java
t1.join(5000); // tunggu maksimal 5 detik

if (t1.isAlive()) {
    System.out.println("Thread masih jalan setelah 5 detik, ada masalah!");
    t1.interrupt();
}
```

---

## Thread Interrupt — Pola yang Benar

### ❌ Solusi yang Salah

```java
Runnable buruk = () -> {
    while (true) {
        doWork(); // kerja terus tanpa cek interrupt
                  // sinyal interrupt tidak ada efeknya!
    }
};
```

### ✅ Solusi yang Benar

```java
Runnable baik = () -> {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            doWork();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore flag
            break;                              // keluar dengan bersih
        }
    }
    // cleanup resources di sini sebelum thread mati
    System.out.println("Thread berhenti dengan bersih");
};
```

---

## Thread State — Wajib Tahu untuk Debugging

Thread punya siklus hidup yang harus kamu pahami:

```
NEW → RUNNABLE → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED
```

| State          | Artinya                              | Contoh Penyebab                  |
|----------------|--------------------------------------|----------------------------------|
| NEW            | Dibuat, belum start()                | `new Thread(task)`               |
| RUNNABLE       | Sedang jalan atau siap jalan         | Setelah `.start()`               |
| BLOCKED        | Nunggu masuk synchronized block      | Thread lain sedang pegang lock   |
| WAITING        | Nunggu tanpa batas waktu             | `object.wait()`, `join()`        |
| TIMED_WAITING  | Nunggu dengan batas waktu            | `sleep(n)`, `join(n)`            |
| TERMINATED     | Selesai atau uncaught exception      | `run()` sudah return             |

```java
Thread t = new Thread(() -> {
    try { Thread.sleep(2000); } catch (Exception e) {}
});

System.out.println(t.getState()); // NEW

t.start();
Thread.sleep(100);
System.out.println(t.getState()); // TIMED_WAITING (karena sleep di dalam)

t.join();
System.out.println(t.getState()); // TERMINATED
```

**Kegunaan nyata di senior level:**
- Banyak thread BLOCKED → ada masalah lock contention
- Banyak thread WAITING → kemungkinan deadlock
- Thread stuck di RUNNABLE tapi CPU tinggi → infinite loop tanpa sleep

---

## Daemon Thread

```java
Thread backgroundTask = new Thread(() -> {
    while (true) {
        cleanupOldFiles();
        try { Thread.sleep(60_000); } catch (Exception e) { break; }
    }
});

backgroundTask.setDaemon(true); // ← HARUS sebelum start()!
backgroundTask.start();

// JVM tidak akan menunggu thread ini sebelum exit
```

**Aturan penting:**
- ✅ Cocok untuk: monitoring, cleanup, logging background
- ❌ Jangan untuk: menyimpan ke database, mengirim network request — karena JVM bisa kill kapan saja tanpa peringatan

---

## Ringkasan Prinsip Chapter 1

Yang membedakan mid-level dan senior bukan hafalan API-nya, tapi cara berpikirnya:

1. **Pisahkan task dari eksekutor** — Runnable/Callable adalah resep, Thread/Pool adalah dapur
2. **Jangan asumsikan urutan** — urutan eksekusi thread ditentukan OS, bukan kode
3. **Handle InterruptedException dengan benar** — restore flag, lalu keluar dengan bersih
4. **Join dengan timeout** — jangan infinite wait, selalu ada batas waktu
5. **Cek isInterrupted() di setiap loop panjang** — buat thread bisa dihentikan kapan saja
6. **Pahami Thread State** — kunci debugging masalah concurrency
7. **Daemon thread untuk task background yang tidak kritis** — JVM bisa kill kapan saja

---

*Selanjutnya → Chapter 2: Race Condition & Synchronized*
