# Java Thread — Chapter 4b: ThreadPool Deep Dive

> Penjelasan mendalam sebelum menyentuh implementasi class
> Dari konsep → cara kerja internal → barulah kode

---

## Mulai dari Pertanyaan Sederhana

Kenapa kita perlu "pool" sama sekali?

Bayangkan kamu punya warung fotokopi. Ada dua cara operasional:

**Cara 1 — Tanpa Pool:**
Setiap ada pelanggan datang → rekrut karyawan baru → karyawan kerja → selesai → karyawan dipecat.
Pelanggan berikutnya datang → rekrut karyawan baru lagi → dan seterusnya.

**Cara 2 — Dengan Pool:**
Rekrut 3 karyawan tetap di awal. Kalau ada pelanggan → karyawan yang lagi nganggur langsung
handle. Kalau semua sedang sibuk → pelanggan antri sebentar. Kalau pelanggan terlalu banyak →
buka rekrutan sementara sampai batas tertentu.

Thread Pool bekerja persis seperti cara ke-2.
Karyawan = Thread. Pelanggan = Task. Antrian = Queue. Batas rekrutan = maximumPoolSize.

---

## Hirarki Interface — Baca Ini Dulu Sebelum Lihat Class

Banyak developer langsung pakai `Executors.newFixedThreadPool()` tanpa tahu apa yang ada
di baliknya. Ini menyebabkan mereka tidak mengerti kenapa sesuatu bisa atau tidak bisa dilakukan.

Mari kita lihat hirarkinya dari atas ke bawah:

```
Executor                          (interface — paling dasar)
    │
    └── ExecutorService           (interface — tambah lifecycle management)
            │
            └── ScheduledExecutorService   (interface — tambah scheduling)

Implementasi:
    AbstractExecutorService       (abstract class — implementasi sebagian ExecutorService)
        │
        └── ThreadPoolExecutor    (concrete class — implementasi penuh, ini "mesinnya")
                │
                └── ScheduledThreadPoolExecutor  (concrete class — tambah fitur scheduling)

Helper/Factory:
    Executors                     (utility class — factory method untuk buat semua di atas)
```

Jadi ketika kamu menulis:

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
```

Yang sebenarnya terjadi di balik layar adalah:

```java
// Ini yang Executors.newFixedThreadPool(5) lakukan di dalamnya:
return new ThreadPoolExecutor(
    5,                           // corePoolSize
    5,                           // maximumPoolSize (sama dengan core → "fixed")
    0L,                          // keepAliveTime (tidak relevan karena max == core)
    TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>()  // queue TIDAK TERBATAS — ini bahayanya!
);
```

Kamu tidak lihat ini karena Executors menyembunyikannya. Dan itulah kenapa
`newFixedThreadPool` bisa menyebabkan OOM di production — queue-nya tidak terbatas.

---

## Interface Executor — Sekecil Ini, Sepenting Ini

```java
public interface Executor {
    void execute(Runnable command);
}
```

Hanya satu method. Tapi maknanya dalam:

> "Saya tidak peduli **bagaimana** kamu menjalankan Runnable ini.
>  Kamu boleh jalankan sekarang, nanti, di thread lain, di thread yang sama —
>  terserah implementasinya."

Ini adalah abstraksi yang memisahkan **"apa yang dikerjakan"** dari **"bagaimana cara mengerjakannya"**.

Karena abstraksi ini, kode bisnis kamu tidak perlu tahu apakah tasknya jalan
di thread baru, thread pool, atau bahkan dijalankan synchronous. Tinggal ganti implementasinya.

---

## Interface ExecutorService — Tambah Lifecycle dan Return Value

`ExecutorService` memperluas `Executor` dengan dua hal penting:

**1. Lifecycle management** — pool bisa dimatikan:

```java
void shutdown();          // minta berhenti dengan sopan (tunggu task yang sedang jalan)
List<Runnable> shutdownNow(); // minta berhenti paksa (interrupt semua, kembalikan yang belum jalan)
boolean isShutdown();     // apakah sudah diminta berhenti?
boolean isTerminated();   // apakah benar-benar sudah berhenti?
boolean awaitTermination(long timeout, TimeUnit unit); // tunggu sampai benar-benar berhenti
```

**2. Submit task dengan return value** — via Future:

```java
<T> Future<T> submit(Callable<T> task);  // task yang mengembalikan nilai
<T> Future<T> submit(Runnable task, T result);
Future<?> submit(Runnable task);

<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks);
<T> T invokeAny(Collection<? extends Callable<T>> tasks);
```

Perbedaan `execute()` vs `submit()`:

| | execute() | submit() |
|---|---|---|
| Dari interface | Executor | ExecutorService |
| Parameter | Runnable | Runnable atau Callable |
| Return | void | Future |
| Exception | Langsung uncaught | Tersimpan di Future, bisa diambil nanti |

---

## ThreadPoolExecutor — Jantungnya ThreadPool

Ini class yang paling penting. Semua `Executors.newXxx()` pada akhirnya membuat
`ThreadPoolExecutor` dengan konfigurasi yang berbeda-beda.

### 7 Parameter Konstruktor — Pahami Satu per Satu

```java
ThreadPoolExecutor(
    int corePoolSize,
    int maximumPoolSize,
    long keepAliveTime,
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue,
    ThreadFactory threadFactory,
    RejectedExecutionHandler handler
)
```

Mari kita bedah masing-masing:

---

### Parameter 1: corePoolSize

Jumlah thread minimum yang **selalu hidup**, bahkan saat tidak ada task.

```
corePoolSize = 3

Kondisi: tidak ada task sama sekali

[ Thread-1: idle ] [ Thread-2: idle ] [ Thread-3: idle ]

Ketiga thread ini tetap hidup, siap menerima task kapanpun.
```

Analogi: Karyawan tetap yang digaji meski sedang tidak ada pelanggan.

**Yang tidak banyak developer tahu:**
Secara default, core thread dibuat secara **lazy** — dibuat saat pertama kali ada task,
bukan saat pool dibuat. Tapi kamu bisa paksa pre-create dengan `prestartAllCoreThreads()`.

```java
pool.prestartAllCoreThreads(); // buat semua core thread sekarang juga
// berguna kalau kamu mau pool langsung "hangat" tanpa warm-up
```

---

### Parameter 2: maximumPoolSize

Jumlah thread **maksimum** yang boleh ada di pool.

Thread di atas `corePoolSize` hanya dibuat kalau:
- Semua core thread sedang sibuk, DAN
- Queue sudah penuh

```
corePoolSize    = 3
maximumPoolSize = 5
queue capacity  = 10

Skenario task datang bertahap:

Task 1-3 masuk  → Buat thread 1,2,3 (core threads)
Task 4-13 masuk → Semua core sibuk, task masuk queue [4,5,6,7,8,9,10,11,12,13]
Task 14 masuk   → Queue penuh! Baru buat thread ke-4
Task 15 masuk   → Queue masih penuh! Buat thread ke-5
Task 16 masuk   → Queue penuh, thread sudah maksimum → REJECTED!
```

**Poin penting yang sering salah dipahami:**
Thread tambahan (di atas core) dibuat SETELAH queue penuh, bukan SETELAH core thread penuh.
Artinya queue diisi dulu sampai penuh, baru thread baru dibuat.

---

### Parameter 3 & 4: keepAliveTime & unit

Berapa lama thread **non-core** (thread yang dibuat di atas corePoolSize) boleh idle
sebelum dihapus dari pool.

```
maximumPoolSize = 5, corePoolSize = 3

Saat beban tinggi: pool punya 5 thread
Saat beban menurun, thread 4 dan 5 idle selama keepAliveTime → dihapus
Pool kembali ke 3 thread (core)

Thread 1,2,3 (core) → tidak pernah dihapus karena keepAliveTime (secara default)
Thread 4,5 (non-core) → dihapus setelah idle selama keepAliveTime
```

**Trik:** Kamu bisa buat core thread juga bisa dihapus saat idle:

```java
pool.allowCoreThreadTimeOut(true);
// Sekarang core thread pun bisa dihapus kalau idle melebihi keepAliveTime
// Berguna kalau kamu mau pool benar-benar "mati" saat tidak ada aktivitas
```

---

### Parameter 5: BlockingQueue — Ini yang Paling Kritis

Queue adalah buffer antara "task yang masuk" dan "thread yang tersedia".
Pemilihan queue menentukan **perilaku keseluruhan pool**.

Ada 3 strategi utama:

---

**Strategi 1: Direct Handoff — SynchronousQueue**

```
Tidak ada buffer sama sekali.
Task langsung diberikan ke thread yang tersedia.
Kalau tidak ada thread tersedia → langsung buat thread baru (sampai max).
Kalau sudah max thread → langsung REJECTED.

Cocok untuk: CachedThreadPool (thread dibuat sesuai kebutuhan)
Bahaya: jumlah thread bisa tidak terbatas kalau max = Integer.MAX_VALUE
```

```java
new ThreadPoolExecutor(
    0,
    Integer.MAX_VALUE,    // ← inilah bahaya CachedThreadPool
    60L, TimeUnit.SECONDS,
    new SynchronousQueue<>()  // ← tidak ada buffer
);
// ini persis apa yang Executors.newCachedThreadPool() lakukan di balik layar
```

---

**Strategi 2: Unbounded Queue — LinkedBlockingQueue tanpa kapasitas**

```
Buffer tidak terbatas.
Thread hanya dibuat sampai corePoolSize, tidak lebih.
maximumPoolSize diabaikan (tidak pernah tercapai karena queue tidak pernah penuh).

Cocok untuk: task yang homogen dan tidak boleh ada yang hilang
Bahaya: queue bisa tumbuh tidak terbatas → OOM
```

```java
new ThreadPoolExecutor(
    5,
    5,                          // ← ini diabaikan karena queue tidak pernah penuh
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>() // ← tidak terbatas
);
// ini persis apa yang Executors.newFixedThreadPool(5) lakukan
```

---

**Strategi 3: Bounded Queue — ArrayBlockingQueue atau LinkedBlockingQueue(n)**

```
Buffer terbatas.
Interaksi antara corePoolSize, maximumPoolSize, dan kapasitas queue
menentukan perilaku sistem secara keseluruhan.

Ini yang paling disarankan untuk production.
```

```java
new ThreadPoolExecutor(
    3,                          // core: selalu ada 3 thread
    10,                         // max: bisa sampai 10 thread saat beban tinggi
    60L, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(50) // buffer 50 task
);

// Perilaku:
// Task 1-3   → langsung ke core thread (dieksekusi sekarang)
// Task 4-53  → masuk queue (menunggu)
// Task 54-61 → queue penuh, buat thread baru (thread 4 sampai 10)
// Task 62+   → queue penuh, thread max → REJECTED
```

---

### Parameter 6: ThreadFactory

Factory yang dipakai untuk membuat thread baru di dalam pool.

Default `ThreadFactory` (dari `Executors.defaultThreadFactory()`) membuat thread dengan:
- Nama: `pool-N-thread-M` (N = nomor pool, M = nomor thread)
- Priority: normal (5)
- Daemon: false
- ThreadGroup: sama dengan thread pemanggil

Kenapa perlu custom? Karena nama thread yang baik sangat membantu saat debugging:

```
Tanpa custom ThreadFactory:
  "pool-1-thread-3" BLOCKED

Dengan custom ThreadFactory:
  "payment-processor-3" BLOCKED

Mana yang lebih mudah kamu debug saat melihat thread dump jam 2 pagi?
```

Selain nama, kamu juga bisa set:
- `UncaughtExceptionHandler` → tangkap exception yang tidak di-handle di dalam task
- `daemon` → apakah thread ini daemon atau bukan
- `priority` → prioritas thread (jarang diubah, tapi bisa)

---

### Parameter 7: RejectedExecutionHandler

Yang terjadi ketika task tidak bisa diterima — queue penuh DAN thread sudah maksimum,
atau pool sudah di-shutdown.

Java menyediakan 4 implementasi bawaan:

```
AbortPolicy (DEFAULT)
→ Lempar RejectedExecutionException
→ Kapan pakai: kamu ingin tahu kalau sistem overload (bisa di-catch dan di-alert)

DiscardPolicy
→ Task dibuang diam-diam, tidak ada exception, tidak ada notifikasi
→ Kapan pakai: task tidak kritikal, boleh dibuang (misal: log metrics non-essential)
→ BAHAYA: kamu tidak akan tahu task hilang kalau tidak ada monitoring

DiscardOldestPolicy
→ Buang task yang paling lama menunggu di queue, coba lagi task baru ini
→ Kapan pakai: task baru lebih penting dari task lama (misal: price update realtime)
→ Pastikan task yang dibuang memang tidak apa-apa untuk dibuang

CallerRunsPolicy
→ Task dijalankan oleh thread yang memanggil execute() (biasanya main thread / HTTP thread)
→ Efeknya: thread pemanggil jadi lambat → backpressure alami → sistem tidak overload
→ Kapan pakai: kamu tidak boleh kehilangan task, dan mau ada backpressure otomatis
```

---

## Alur Lengkap: Apa yang Terjadi Saat execute() Dipanggil?

Ini adalah alur keputusan yang terjadi di dalam `ThreadPoolExecutor.execute()`:

```
execute(task) dipanggil
        │
        ▼
Berapa thread yang aktif sekarang?
        │
        ├── Kurang dari corePoolSize
        │       │
        │       └── Buat thread baru dan jalankan task sekarang
        │           (meskipun ada core thread yang idle!)
        │
        └── Sudah >= corePoolSize
                │
                ▼
        Apakah queue bisa menerima task?
                │
                ├── Ya (queue belum penuh)
                │       │
                │       └── Masukkan task ke queue, tunggu giliran
                │           (task akan diambil thread yang idle)
                │
                └── Tidak (queue penuh)
                        │
                        ▼
                Apakah thread bisa ditambah (< maximumPoolSize)?
                        │
                        ├── Ya
                        │       │
                        │       └── Buat thread baru dan jalankan task sekarang
                        │
                        └── Tidak (sudah di maximumPoolSize)
                                │
                                └── Panggil RejectedExecutionHandler
```

**Poin kunci yang sering bikin bingung:**

Ketika thread masih di bawah `corePoolSize`, pool **selalu membuat thread baru**
meskipun ada thread idle. Ini disengaja — memastikan core thread selalu terbentuk
di awal saat ada traffic masuk.

---

## Perbandingan Semua Executors.newXxx() — Apa di Baliknya

| Method | coreSize | maxSize | Queue | Efek |
|---|---|---|---|---|
| `newFixedThreadPool(n)` | n | n | LinkedBlockingQueue() tak terbatas | Thread tetap n, queue bisa OOM |
| `newSingleThreadExecutor()` | 1 | 1 | LinkedBlockingQueue() tak terbatas | Sequential, queue bisa OOM |
| `newCachedThreadPool()` | 0 | Integer.MAX_VALUE | SynchronousQueue | Thread tak terbatas, bisa OOM |
| `newScheduledThreadPool(n)` | n | Integer.MAX_VALUE | DelayedWorkQueue | Untuk task terjadwal |
| `newWorkStealingPool()` | - | - | - | ForkJoinPool, beda mekanisme |

**Kesimpulan:** Tidak ada satu pun dari method di atas yang aman untuk production
tanpa memahami trade-off-nya. Itulah kenapa `ThreadPoolExecutor` manual dengan
`bounded queue` lebih disarankan untuk sistem yang serius.

---

## Mengapa Queue Bounded Lebih Aman di Production

Ini analogi yang mudah dipahami:

**Warung makan tanpa batas antrian (unbounded queue):**
Pelanggan terus masuk dan antri meskipun sudah 500 orang mengantri.
Semua orang tetap antri, memakan tempat, dan pada akhirnya pelanggan ke-501
pingsan karena kehabisan oksigen (OOM).

**Warung makan dengan batas antrian (bounded queue):**
Kalau sudah 50 orang antri, pelanggan berikutnya ditolak masuk.
Pelanggan yang ditolak bisa pergi ke tempat lain (retry, fallback, atau error response).
Warung tetap bisa berfungsi normal untuk yang sudah di dalam.

Dengan bounded queue:
- Sistem tahu lebih awal kalau sedang overload
- Bisa memberi response yang tepat (503 Service Unavailable) daripada diam-diam lambat
- Memori tetap terkontrol

---

## Template ThreadPool yang Production-Ready

Ini template yang bisa kamu pakai sebagai starting point di project nyata:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ProductionReadyThreadPoolTest {

    /**
     * Template ThreadPool yang production-ready.
     *
     * Konfigurasi ini bisa kamu jadikan starting point,
     * lalu adjust berdasarkan profiling dan observasi di production.
     */
    static ThreadPoolExecutor buatPoolProductionReady(String namaPool) {
        int core = Runtime.getRuntime().availableProcessors();

        AtomicInteger threadCounter = new AtomicInteger(1);

        return new ThreadPoolExecutor(
            core,                           // corePoolSize: selalu ada sejumlah core
            core * 2,                       // maximumPoolSize: bisa 2x core saat beban puncak
            60L,                            // keepAliveTime: thread non-core dihapus setelah 60 detik idle
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(500), // bounded queue: maksimum 500 task menunggu

            r -> {                          // custom ThreadFactory
                Thread t = new Thread(r);
                t.setName(namaPool + "-" + threadCounter.getAndIncrement());
                t.setDaemon(false);
                t.setUncaughtExceptionHandler((thread, ex) ->
                    // Di production: log ke monitoring system
                    System.err.printf("[%s] Uncaught: %s%n", thread.getName(), ex.getMessage())
                );
                return t;
            },

            (task, executor) -> {           // custom RejectedExecutionHandler
                // Di production: increment metrik "tasks_rejected_total"
                // lalu putuskan: lempar exception, atau log dan buang
                throw new RejectedExecutionException(
                    namaPool + " overloaded! Queue penuh dan thread sudah maksimum."
                );
            }
        );
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void templateProductionReady_berjalaanDenganBenar() throws InterruptedException {
        ThreadPoolExecutor pool = buatPoolProductionReady("order-processor");
        CountDownLatch latch = new CountDownLatch(20);
        AtomicInteger selesai = new AtomicInteger(0);

        // Kirim 20 task
        for (int i = 1; i <= 20; i++) {
            final int id = i;
            pool.execute(() -> {
                System.out.printf("[%s] Memproses order-%d%n",
                    Thread.currentThread().getName(), id);
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                selesai.incrementAndGet();
                latch.countDown();
            });
        }

        latch.await();

        System.out.println("\n=== Pool Statistics ===");
        System.out.printf("Core pool size    : %d%n", pool.getCorePoolSize());
        System.out.printf("Max pool size     : %d%n", pool.getMaximumPoolSize());
        System.out.printf("Largest pool seen : %d%n", pool.getLargestPoolSize());
        System.out.printf("Tasks completed   : %d%n", pool.getCompletedTaskCount());

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(20, selesai.get(), "Semua task harus selesai");
        System.out.println("✅ Production-ready pool berjalan dengan baik!");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void templateProductionReady_rejectedSaatOverload() {
        // Buat pool sangat kecil untuk simulasi overload
        ThreadPoolExecutor poolKecil = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2), // queue hanya 2
            Executors.defaultThreadFactory(),
            (task, executor) -> {
                throw new RejectedExecutionException("System overloaded!");
            }
        );

        AtomicInteger ditolak = new AtomicInteger(0);

        // Kirim 10 task ke pool yang hanya bisa handle 3 (1 thread + 2 queue)
        for (int i = 0; i < 10; i++) {
            try {
                poolKecil.execute(() -> {
                    try { Thread.sleep(500); } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (RejectedExecutionException e) {
                ditolak.incrementAndGet();
                System.out.println("Task ditolak: " + e.getMessage());
            }
        }

        poolKecil.shutdownNow();

        assertTrue(ditolak.get() > 0, "Seharusnya ada task yang ditolak");
        System.out.printf("%n✅ %d task ditolak dengan benar saat overload%n", ditolak.get());
    }
}
```

---

## Ringkasan Mental Model

Kalau kamu harus menjelaskan ThreadPool ke orang lain dalam 30 detik, gunakan ini:

```
ThreadPool = Tim karyawan tetap yang siap menerima pekerjaan

corePoolSize     = jumlah karyawan tetap minimum
maximumPoolSize  = batas maksimum karyawan (termasuk kontrak saat sibuk)
workQueue        = meja antrian pekerjaan yang menunggu
keepAliveTime    = berapa lama karyawan kontrak boleh nganggur sebelum dirumahkan
threadFactory    = prosedur rekrutmen (nama, properti karyawan baru)
rejectedHandler  = kebijakan saat antrian penuh dan karyawan sudah maksimum

execute(task)    = taruh pekerjaan baru
shutdown()       = "selesaikan yang ada, jangan terima yang baru"
shutdownNow()    = "berhenti semua sekarang juga"
```

---

*Selanjutnya → Chapter 5: Future, Callable & CompletableFuture*
