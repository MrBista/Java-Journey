# Java Thread — Chapter 4: ThreadPool & ExecutorService

> **Target:** Mid Java Developer yang ingin naik ke Senior
> **Pendekatan:** Masalah → Solusi Salah → Solusi Benar → Kenapa
> **Catatan:** Semua contoh kode bisa langsung dijalankan via JUnit 5

---

## Kenapa Tidak Boleh Buat Thread Manual Terus-terusan?

Sebelum masuk ke ThreadPool, kita harus pahami dulu **masalah apa yang dipecahkan**.

Bayangkan sebuah REST API yang menerima 1000 request per detik. Kalau setiap request dibuatkan 1 thread baru:

```
1000 request/detik
× 512KB per thread stack
= 512MB memori hanya untuk stack thread!
```

Belum lagi biaya **OS-level context switching** — setiap kali CPU berpindah dari satu thread ke thread lain, ada overhead yang tidak kecil. Di atas 100-200 thread aktif, performa aplikasi justru turun karena CPU lebih banyak switching daripada bekerja.

**ThreadPool hadir untuk menyelesaikan dua masalah ini:**
1. Thread di-reuse — tidak perlu buat dan destroy berulang kali
2. Jumlah thread dibatasi — tidak ada lagi OOM karena thread tidak terkontrol

---

## Anatomi ThreadPool

Sebelum koding, pahami dulu komponen-komponen ThreadPool:

```
                    ┌─────────────────────────────────────┐
submit(task) ──────►│           Task Queue                │
                    │  [task1] [task2] [task3] [task4]... │
                    └──────────────┬──────────────────────┘
                                   │ diambil saat thread idle
                    ┌──────────────▼──────────────────────┐
                    │           Thread Pool               │
                    │  [Thread-1] [Thread-2] [Thread-3]  │
                    │   (bekerja)  (idle)    (bekerja)   │
                    └─────────────────────────────────────┘
                                   │
                    Kalau queue penuh + semua thread sibuk
                                   │
                    ┌──────────────▼──────────────────────┐
                    │       RejectedExecutionHandler      │
                    │   (default: lempar Exception)       │
                    └─────────────────────────────────────┘
```

Parameter kunci di `ThreadPoolExecutor`:

| Parameter | Artinya |
|---|---|
| `corePoolSize` | Jumlah thread minimum yang selalu hidup |
| `maximumPoolSize` | Jumlah thread maksimum yang boleh dibuat |
| `keepAliveTime` | Berapa lama thread idle (di atas core) sebelum dihapus |
| `workQueue` | Antrian task yang menunggu giliran |
| `rejectedHandler` | Apa yang dilakukan kalau queue penuh dan thread max |

---

## Masalah 1: Buat Thread Manual untuk Setiap Task

### ❌ Solusi yang Salah

```java
@Test
void contohMasalahBuatThreadManual() throws InterruptedException {
    int jumlahRequest = 1000;
    CountDownLatch latch = new CountDownLatch(jumlahRequest);

    long mulai = System.currentTimeMillis();

    for (int i = 0; i < jumlahRequest; i++) {
        // ❌ Buat 1000 thread sekaligus — sangat boros!
        new Thread(() -> {
            try {
                Thread.sleep(10); // simulasi proses
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }).start();
    }

    latch.await();
    long selesai = System.currentTimeMillis();

    System.out.printf("1000 thread manual selesai dalam %d ms%n", selesai - mulai);
    System.out.println("❌ Masalah: 1000 thread dibuat dan dihancurkan — sangat boros memori & CPU!");
}
```

### ✅ Solusi yang Benar: Gunakan ThreadPool

```java
@Test
void contohSolusiDenganThreadPool() throws InterruptedException {
    int jumlahRequest = 1000;
    CountDownLatch latch = new CountDownLatch(jumlahRequest);

    // ✅ Hanya 10 thread, tapi bisa handle 1000 task
    ExecutorService executor = Executors.newFixedThreadPool(10);

    long mulai = System.currentTimeMillis();

    for (int i = 0; i < jumlahRequest; i++) {
        executor.execute(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });
    }

    latch.await();
    long selesai = System.currentTimeMillis();

    executor.shutdown();
    System.out.printf("1000 task dengan 10 thread pool selesai dalam %d ms%n", selesai - mulai);
    System.out.println("✅ Hanya 10 thread yang dibuat, di-reuse untuk semua 1000 task");
}
```

---

## Masalah 2: Salah Pilih Jenis Executor

Ini kesalahan yang sering terjadi — asal pakai tanpa tahu karakteristiknya.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class JenisExecutorTest {

    /**
     * newFixedThreadPool — jumlah thread tetap, queue tidak terbatas
     *
     * ✅ Cocok untuk: task yang jumlahnya terkontrol, workload stabil
     * ❌ Bahaya: queue tidak terbatas → bisa OOM kalau task menumpuk lebih cepat dari diproses
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void fixedThreadPool_karakteristik() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger taskSelesai = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(9);

        long mulai = System.currentTimeMillis();

        for (int i = 1; i <= 9; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.printf("[%s] Mengerjakan task-%d%n",
                        Thread.currentThread().getName(), taskId);
                try { Thread.sleep(500); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                taskSelesai.incrementAndGet();
                latch.countDown();
            });
        }

        latch.await();
        long durasi = System.currentTimeMillis() - mulai;
        executor.shutdown();

        // 9 task dengan 3 thread → butuh 3 "gelombang" × 500ms = ~1500ms
        System.out.printf("9 task selesai dalam %d ms dengan 3 thread%n", durasi);
        System.out.println("Perhatikan: thread di-reuse (nama thread sama dipakai berulang)");

        assertEquals(9, taskSelesai.get());
        // 3 thread → 3 batch → minimal ~1500ms (beri toleransi)
        assertTrue(durasi >= 1400, "Harusnya butuh minimal ~1500ms (3 batch × 500ms)");
    }

    /**
     * newCachedThreadPool — thread dibuat sesuai kebutuhan, idle 60 detik lalu dihapus
     *
     * ✅ Cocok untuk: task yang sangat banyak dan sangat cepat selesai (short-lived)
     * ❌ BAHAYA BESAR: tidak ada batas thread → bisa buat ribuan thread → OOM!
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void cachedThreadPool_bahayaTidakAdaBatasThread() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        AtomicInteger jumlahThreadDibuat = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                jumlahThreadDibuat.incrementAndGet();
                // Task sangat cepat — cached pool ideal untuk ini
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        System.out.printf("CachedPool membuat hingga %d thread untuk 100 task cepat%n",
                jumlahThreadDibuat.get());
        System.out.println("⚠️  Di production, jangan pakai ini untuk task lambat/banyak!");
    }

    /**
     * newSingleThreadExecutor — hanya 1 thread, semua task dijalankan berurutan
     *
     * ✅ Cocok untuk: task yang HARUS dijalankan berurutan (sequential logging, event processing)
     * ✅ Cocok untuk: akses resource yang tidak thread-safe tapi harus dari background thread
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void singleThreadExecutor_menjaminUrutan() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ConcurrentLinkedQueue<Integer> urutan = new ConcurrentLinkedQueue<>();
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                urutan.add(taskId);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        System.out.println("Urutan eksekusi: " + urutan);

        // Single thread menjamin urutan FIFO
        Integer[] hasil = urutan.toArray(new Integer[0]);
        for (int i = 0; i < hasil.length; i++) {
            assertEquals(i + 1, hasil[i],
                    "Single thread executor harus menjaga urutan task");
        }
        System.out.println("✅ Urutan terjaga — task 1,2,3,4,5 dieksekusi berurutan");
    }
}
```

---

## Masalah 3: Tidak Tahu Cara Shutdown yang Benar

Ini kesalahan yang sangat umum dan menyebabkan resource leak di production.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ShutdownExecutorTest {

    /**
     * ❌ Cara yang salah — tidak shutdown sama sekali
     * Thread pool tidak pernah mati → resource leak!
     */
    @Test
    void contohTidakShutdown_resourceLeak() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.execute(() -> System.out.println("Task jalan"));

        // ❌ Tidak ada shutdown() → 5 thread tetap hidup selamanya
        // Di aplikasi nyata ini menyebabkan memory leak dan thread leak
        System.out.println("❌ Executor tidak di-shutdown — thread pool masih hidup!");

        // Untuk test ini kita paksa shutdown agar JVM bisa exit
        executor.shutdownNow();
    }

    /**
     * ✅ Pola shutdown yang benar dengan try-finally
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void shutdownDenganGracefulPattern() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        AtomicInteger taskSelesai = new AtomicInteger(0);

        // Submit beberapa task
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                taskSelesai.incrementAndGet();
            });
        }

        // ✅ Pola shutdown yang benar
        executor.shutdown(); // Tidak menerima task baru, tunggu yang ada selesai

        boolean selesaiTepat = executor.awaitTermination(5, TimeUnit.SECONDS);

        if (!selesaiTepat) {
            System.out.println("⚠️  Timeout! Paksa shutdown...");
            executor.shutdownNow(); // Paksa berhenti jika timeout
        }

        System.out.printf("Task selesai: %d/10%n", taskSelesai.get());
        assertTrue(executor.isTerminated(), "Executor harus sudah terminated");
        assertEquals(10, taskSelesai.get(), "Semua 10 task harus selesai");
        System.out.println("✅ Executor shutdown dengan bersih, semua task selesai");
    }

    /**
     * Perbedaan shutdown() vs shutdownNow()
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void perbedaanShutdownDanShutdownNow() throws InterruptedException {
        // --- shutdown() : tunggu task yang sedang berjalan ---
        ExecutorService executor1 = Executors.newFixedThreadPool(2);
        AtomicInteger selesai1 = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            executor1.execute(() -> {
                try { Thread.sleep(200); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return; // keluar kalau diinterrupt
                }
                selesai1.incrementAndGet();
            });
        }

        executor1.shutdown();
        // shutdown() → task yang SUDAH MULAI akan selesai, yang belum mulai dicancel
        executor1.awaitTermination(3, TimeUnit.SECONDS);
        System.out.printf("shutdown()     → %d task selesai (dari 5 yang disubmit)%n",
                selesai1.get());

        // --- shutdownNow() : interrupt semua task langsung ---
        ExecutorService executor2 = Executors.newFixedThreadPool(2);
        AtomicInteger selesai2 = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            executor2.execute(() -> {
                try { Thread.sleep(500); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return; // langsung keluar saat diinterrupt
                }
                selesai2.incrementAndGet();
            });
        }

        Thread.sleep(100); // biarkan beberapa task mulai dulu
        executor2.shutdownNow(); // interrupt semua
        executor2.awaitTermination(2, TimeUnit.SECONDS);
        System.out.printf("shutdownNow()  → %d task selesai (sisanya diinterrupt)%n",
                selesai2.get());

        System.out.println("✅ Gunakan shutdown() untuk graceful, shutdownNow() untuk paksa berhenti");
    }
}
```

---

## Masalah 4: Tidak Tahu Cara Konfigurasi ThreadPool yang Tepat

Ini yang membedakan senior dari mid-level — tahu cara **tuning** ThreadPool.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolConfigTest {

    /**
     * ThreadPoolExecutor manual — kontrol penuh atas semua parameter
     *
     * Rumus umum untuk menentukan jumlah thread:
     *
     * CPU-bound task  → jumlah thread = jumlah CPU core
     * I/O-bound task  → jumlah thread = jumlah CPU core × (1 + waktu-tunggu/waktu-cpu)
     *
     * Contoh: core=4, task I/O 80% tunggu, 20% kerja
     * → 4 × (1 + 0.8/0.2) = 4 × 5 = 20 thread
     */
    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void threadPoolExecutorManual_kontrolPenuh() throws InterruptedException {
        int jumlahCore = Runtime.getRuntime().availableProcessors();
        System.out.printf("Jumlah CPU core: %d%n", jumlahCore);

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                jumlahCore,                          // corePoolSize
                jumlahCore * 2,                      // maximumPoolSize
                60L,                                 // keepAliveTime
                TimeUnit.SECONDS,                    // keepAliveTime unit
                new LinkedBlockingQueue<>(50),        // bounded queue (kapasitas 50!)
                new ThreadFactory() {                // custom thread naming
                    private final AtomicInteger counter = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("worker-" + counter.getAndIncrement());
                        t.setDaemon(false);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // rejected handler
        );

        AtomicInteger taskSelesai = new AtomicInteger(0);
        AtomicInteger taskDijalankanOlehCaller = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(30);

        for (int i = 0; i < 30; i++) {
            final int taskId = i;
            pool.execute(() -> {
                String nama = Thread.currentThread().getName();
                // CallerRunsPolicy → task bisa dijalankan oleh thread pemanggil (main/test)
                if (!nama.startsWith("worker-")) {
                    taskDijalankanOlehCaller.incrementAndGet();
                }
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                taskSelesai.incrementAndGet();
                latch.countDown();
            });
        }

        latch.await();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.printf("Task selesai             : %d%n", taskSelesai.get());
        System.out.printf("Dijalankan caller thread : %d%n", taskDijalankanOlehCaller.get());
        System.out.printf("Pool size saat ini       : %d%n", pool.getPoolSize());
        System.out.printf("Task completed (pool)    : %d%n", pool.getCompletedTaskCount());

        assertEquals(30, taskSelesai.get(), "Semua 30 task harus selesai");
        System.out.println("✅ ThreadPoolExecutor manual berjalan dengan baik!");
    }

    /**
     * Perbandingan 4 RejectedExecutionHandler bawaan Java
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void rejectedHandlers_perbandingan() throws InterruptedException {
        // Queue sangat kecil (2) dan thread sangat sedikit (1) → mudah penuh
        // untuk demo rejected handler

        // 1. AbortPolicy (DEFAULT) → lempar RejectedExecutionException
        ThreadPoolExecutor pool1 = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.AbortPolicy()
        );
        AtomicInteger ditolakAbort = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            try {
                pool1.execute(() -> {
                    try { Thread.sleep(200); } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (RejectedExecutionException e) {
                ditolakAbort.incrementAndGet();
            }
        }
        pool1.shutdown();
        pool1.awaitTermination(3, TimeUnit.SECONDS);
        System.out.printf("AbortPolicy       → %d task ditolak dengan exception%n",
                ditolakAbort.get());

        // 2. DiscardPolicy → task ditolak diam-diam (tidak ada exception!)
        ThreadPoolExecutor pool2 = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.DiscardPolicy()
        );
        AtomicInteger taskMasuk2 = new AtomicInteger(0);
        AtomicInteger taskSelesai2 = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            pool2.execute(() -> {
                taskMasuk2.incrementAndGet();
                try { Thread.sleep(200); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                taskSelesai2.incrementAndGet();
            });
        }
        pool2.shutdown();
        pool2.awaitTermination(3, TimeUnit.SECONDS);
        System.out.printf("DiscardPolicy     → hanya %d task yang jalan dari 10 (sisanya hilang diam-diam!)%n",
                taskSelesai2.get());

        // 3. CallerRunsPolicy → task dijalankan oleh thread pemanggil (slowdown backpressure)
        ThreadPoolExecutor pool3 = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        AtomicInteger taskSelesai3 = new AtomicInteger(0);
        CountDownLatch latch3 = new CountDownLatch(6);
        for (int i = 0; i < 6; i++) {
            pool3.execute(() -> {
                taskSelesai3.incrementAndGet();
                latch3.countDown();
            });
        }
        latch3.await(5, TimeUnit.SECONDS);
        pool3.shutdown();
        pool3.awaitTermination(3, TimeUnit.SECONDS);
        System.out.printf("CallerRunsPolicy  → semua %d task selesai (caller ikut bantu eksekusi)%n",
                taskSelesai3.get());

        System.out.println("\n💡 Pilih rejected handler sesuai kebutuhan:");
        System.out.println("   AbortPolicy   → kamu ingin tahu kalau system overload (default)");
        System.out.println("   DiscardPolicy → task tidak kritikal, boleh dibuang");
        System.out.println("   CallerRuns    → backpressure alami, system tidak overload");
    }
}
```

---

## Masalah 5: Monitoring ThreadPool — Yang Senior Wajib Tahu

Di production, kamu harus bisa **monitor** kondisi ThreadPool, bukan hanya membuatnya.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPoolMonitoringTest {

    /**
     * Cara monitor ThreadPool secara real-time
     * Berguna untuk health check endpoint di production
     */
    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void monitoringThreadPool_realtime() throws InterruptedException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2, 5, 30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(20)
        );

        AtomicBoolean jalan = new AtomicBoolean(true);

        // Thread monitor — seperti health check endpoint
        Thread monitor = new Thread(() -> {
            while (jalan.get() && !Thread.currentThread().isInterrupted()) {
                System.out.printf(
                        "[Monitor] Active: %2d | Pool: %2d | Queue: %3d | Completed: %3d | Max: %2d%n",
                        pool.getActiveCount(),        // thread yang sedang bekerja
                        pool.getPoolSize(),           // total thread yang ada di pool
                        pool.getQueue().size(),       // task yang menunggu di queue
                        pool.getCompletedTaskCount(), // total task yang sudah selesai
                        pool.getLargestPoolSize()     // peak jumlah thread (berguna untuk tuning)
                );
                try { Thread.sleep(300); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "PoolMonitor");
        monitor.setDaemon(true);
        monitor.start();

        // Simulasi workload: kirim 30 task secara bertahap
        CountDownLatch latch = new CountDownLatch(30);
        for (int i = 0; i < 30; i++) {
            final int taskId = i;
            pool.execute(() -> {
                try {
                    // Simulasi task dengan durasi yang bervariasi
                    Thread.sleep(200 + (taskId % 5) * 100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
            Thread.sleep(50); // kirim task sedikit demi sedikit
        }

        latch.await();
        jalan.set(false);

        System.out.println("\n=== Statistik Akhir ===");
        System.out.printf("Total task selesai : %d%n", pool.getCompletedTaskCount());
        System.out.printf("Peak thread count  : %d%n", pool.getLargestPoolSize());
        System.out.printf("Core pool size     : %d%n", pool.getCorePoolSize());

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("✅ Monitoring selesai!");
    }

    /**
     * Custom ThreadFactory — wajib di production untuk:
     * 1. Naming yang deskriptif (mudah debug di thread dump)
     * 2. Set sebagai daemon atau bukan
     * 3. Custom UncaughtExceptionHandler
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void customThreadFactory_productionReady() throws InterruptedException {
        ThreadFactory factory = r -> {
            Thread t = new Thread(r);
            t.setName("payment-worker-" + t.getId()); // nama yang deskriptif
            t.setDaemon(false);

            // Handle exception yang tidak tertangkap di dalam task
            t.setUncaughtExceptionHandler((thread, ex) -> {
                System.out.printf("❌ [%s] Uncaught exception: %s%n",
                        thread.getName(), ex.getMessage());
                // Di production: kirim alert ke monitoring system (Datadog, PagerDuty, dll)
            });
            return t;
        };

        ExecutorService executor = new ThreadPoolExecutor(
                2, 4, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                factory
        );

        CountDownLatch latch = new CountDownLatch(4);

        // Task normal
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                System.out.printf("[%s] Task normal selesai%n",
                        Thread.currentThread().getName());
                latch.countDown();
            });
        }

        // Task yang throw exception — tanpa UncaughtExceptionHandler, ini hilang begitu saja!
        executor.execute(() -> {
            latch.countDown();
            throw new RuntimeException("Simulasi error di payment processing!");
            // UncaughtExceptionHandler akan menangkap ini
        });

        latch.await();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("✅ Custom ThreadFactory berjalan — cek nama thread di output!");
    }
}
```

---

## Rangkuman: Kapan Pakai Executor yang Mana?

```java
import org.junit.jupiter.api.Test;

public class PanduanMemilihExecutorTest {

    @Test
    void panduanMemilihExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();

        // ✅ CPU-bound task (komputasi berat, tidak banyak I/O)
        // Jumlah thread = jumlah core agar tidak over-subscribe CPU
        ExecutorService cpuBound = Executors.newFixedThreadPool(cores);

        // ✅ I/O-bound task (HTTP call, database query, file I/O)
        // Thread sering idle nunggu I/O → bisa lebih banyak dari core
        ExecutorService ioBound = Executors.newFixedThreadPool(cores * 4);

        // ✅ Task yang HARUS sequential (logging, audit trail)
        ExecutorService sequential = Executors.newSingleThreadExecutor();

        // ✅ Task terjadwal (cron job, periodic health check)
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        // scheduled.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);

        // ❌ Hindari CachedThreadPool untuk task yang lambat atau jumlah tidak terbatas
        // ExecutorService bahaya = Executors.newCachedThreadPool();

        System.out.println("Jumlah CPU core   : " + cores);
        System.out.println("Rekomendasi CPU   : " + cores + " thread");
        System.out.println("Rekomendasi I/O   : " + (cores * 4) + " thread");
        System.out.println("✅ Pilih executor sesuai karakteristik task!");

        // Cleanup
        cpuBound.shutdown();
        ioBound.shutdown();
        sequential.shutdown();
        scheduled.shutdown();
    }
}
```

---

## Ringkasan Prinsip Chapter 4

1. **Jangan buat Thread manual untuk setiap task** — gunakan ThreadPool, thread di-reuse dan hemat memori
2. **Pilih executor yang tepat** — Fixed untuk workload stabil, Single untuk sequential, hindari Cached untuk task lambat
3. **Selalu shutdown executor** — resource leak kalau tidak di-shutdown, gunakan pola `shutdown() + awaitTermination() + shutdownNow()`
4. **Gunakan bounded queue** — queue tidak terbatas (`LinkedBlockingQueue()` tanpa argumen) bisa menyebabkan OOM
5. **Pilih RejectedHandler yang tepat** — AbortPolicy untuk deteksi overload, CallerRuns untuk backpressure alami
6. **Monitor dengan `getActiveCount`, `getQueue().size()`, `getLargestPoolSize()`** — wajib untuk health check di production
7. **Gunakan custom ThreadFactory** — nama thread yang deskriptif sangat membantu saat baca thread dump di production
8. **Rumus thread count** — CPU-bound: jumlah core, I/O-bound: core × (1 + wait-time/cpu-time)

---

*Selanjutnya → Chapter 5: Future, Callable & CompletableFuture*
