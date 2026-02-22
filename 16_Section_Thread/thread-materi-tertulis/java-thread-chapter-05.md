# Java Thread — Chapter 5: Future, Callable & CompletableFuture

> **Target:** Mid Java Developer yang ingin naik ke Senior
> **Pendekatan:** Masalah → Solusi Salah → Solusi Benar → Kenapa
> **Catatan:** Semua contoh kode bisa langsung dijalankan via JUnit 5

---

## Mulai dari Masalah Nyata

Kamu punya task yang jalan di background thread. Setelah selesai, kamu butuh hasilnya.
Bagaimana caranya?

Dengan `Runnable`, kamu tidak bisa — karena `run()` return void.
Di sinilah `Callable` dan `Future` hadir.

Tapi sebelum langsung ke kode, pahami dulu hierarki dan hubungan antar ketiganya:

```
Runnable          → task tanpa return value, tidak bisa throw checked exception
Callable<T>       → task dengan return value T, bisa throw checked exception

Future<T>         → representasi hasil yang "akan datang" dari Callable
                    bisa di-get, di-cancel, dicek statusnya

CompletableFuture<T> → Future yang lebih powerful:
                    - bisa di-complete secara manual
                    - bisa di-chain (thenApply, thenCompose, dll)
                    - bisa dikombinasi (thenCombine, allOf, anyOf)
                    - bisa handle error (exceptionally, handle)
```

---

## Bagian 1: Callable — Runnable yang Bisa Return Nilai

### Apa Bedanya Runnable dan Callable?

```java
// Runnable — tidak bisa return nilai, tidak bisa throw checked exception
public interface Runnable {
    void run(); // return void, tidak ada throws
}

// Callable<T> — bisa return nilai, bisa throw checked exception
public interface Callable<T> {
    T call() throws Exception; // return T, ada throws Exception
}
```

Implikasinya:
- Kalau task kamu perlu **mengembalikan hasil** → pakai Callable
- Kalau task kamu perlu **melempar checked exception** → pakai Callable
- Kalau task kamu hanya "jalankan dan lupakan" → Runnable cukup

---

## Bagian 2: Future — Representasi Hasil yang Akan Datang

### Memahami Future Secara Konseptual

`Future<T>` bukan hasilnya langsung — dia adalah **janji** bahwa hasil akan ada nanti.

```
submit(callable) dipanggil
        │
        ▼
Future<T> langsung dikembalikan  ← kamu terima ini sekarang
        │                           (meskipun callable belum selesai)
        │
        │   ... callable berjalan di background thread ...
        │
        ▼
future.get() dipanggil
        │
        ├── Kalau callable sudah selesai → langsung dapat hasilnya
        └── Kalau belum selesai → thread yang panggil get() MENUNGGU (blocking)
```

### Masalah 1: future.get() Tanpa Timeout — Bom Waktu

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class FutureBasicTest {

    /**
     * ❌ future.get() tanpa timeout — berbahaya di production
     *
     * Kalau callable-nya hang (koneksi DB putus, external API tidak respond),
     * thread yang memanggil get() akan menunggu SELAMANYA.
     * Di aplikasi web, ini berarti HTTP thread-mu habis → server tidak bisa menerima request baru.
     */
    // @Test
    // @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void futureGet_tanpaTimeout_akanHang() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<String> future = executor.submit(() -> {
            Thread.sleep(Long.MAX_VALUE); // simulasi task yang hang
            return "tidak akan sampai sini";
        });

        // ❌ Ini akan menunggu selamanya!
        String hasil = future.get();
        System.out.println(hasil); // tidak pernah tercetak

        executor.shutdown();
    }

    /**
     * ✅ future.get() dengan timeout — selalu pakai ini
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void futureGet_denganTimeout_amanDiProduction() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Task yang selesai dalam waktu normal
        Future<Integer> futureNormal = executor.submit(() -> {
            Thread.sleep(500);
            return 42;
        });

        // Task yang terlalu lambat (simulasi external API lambat)
        Future<String> futureLambat = executor.submit(() -> {
            Thread.sleep(10_000); // 10 detik
            return "terlalu lambat";
        });

        // ✅ Selalu pakai timeout
        try {
            Integer hasil = futureNormal.get(2, TimeUnit.SECONDS);
            System.out.println("Task normal selesai, hasil: " + hasil);
            assertEquals(42, hasil);
        } catch (TimeoutException e) {
            System.out.println("Task normal timeout — tidak seharusnya terjadi");
            fail("Task normal seharusnya selesai dalam 2 detik");
        }

        try {
            String hasil = futureLambat.get(1, TimeUnit.SECONDS); // timeout 1 detik
            fail("Seharusnya timeout!");
        } catch (TimeoutException e) {
            System.out.println("✅ Task lambat berhasil di-timeout setelah 1 detik");
            futureLambat.cancel(true); // penting: cancel task yang sudah timeout!
            assertTrue(futureLambat.isCancelled());
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
        System.out.println("✅ Selalu gunakan get(timeout, unit) bukan get()!");
    }
}
```

---

### Masalah 2: Exception di Callable Hilang Begitu Saja

Ini jebakan yang sangat umum. Exception di dalam Callable tidak langsung dilempar —
dia dibungkus di dalam Future dan baru "meledak" saat kamu panggil `get()`.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class FutureExceptionTest {

    /**
     * ❌ Tidak handle ExecutionException — exception hilang tanpa jejak
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void callable_exceptionTidakDihandle_hilangBegituSaja() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Future<String> future = executor.submit(() -> {
            // Simulasi error — misal koneksi database gagal
            throw new RuntimeException("Koneksi database gagal!");
            // Exception ini TIDAK langsung dilempar ke caller!
            // Dia tersimpan di dalam Future
        });

        // Kalau tidak panggil get(), exception ini tidak pernah kamu tahu!
        Thread.sleep(200); // task sudah selesai (dengan error) tapi kamu tidak tahu

        System.out.println("Task sudah selesai tapi kamu tidak tahu ada error!");
        System.out.println("isDone: " + future.isDone()); // true — tapi karena exception!

        executor.shutdown();
    }

    /**
     * ✅ Handle ExecutionException dengan benar
     *
     * ExecutionException adalah wrapper — penyebab sebenarnya ada di getCause()
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void callable_exceptionDihandleDenganBenar() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Task yang sukses
        Future<String> futureSukses = executor.submit(() -> {
            Thread.sleep(100);
            return "Data dari database";
        });

        // Task yang gagal
        Future<String> futureGagal = executor.submit(() -> {
            Thread.sleep(100);
            throw new RuntimeException("Koneksi database timeout!");
        });

        // Handle sukses
        try {
            String hasil = futureSukses.get(2, TimeUnit.SECONDS);
            System.out.println("✅ Sukses: " + hasil);
        } catch (ExecutionException e) {
            // Penyebab sebenarnya ada di getCause(), bukan di e sendiri!
            System.out.println("❌ Gagal: " + e.getCause().getMessage());
        } catch (TimeoutException e) {
            System.out.println("⏰ Timeout!");
        }

        // Handle gagal
        try {
            String hasil = futureGagal.get(2, TimeUnit.SECONDS);
            System.out.println("Sukses: " + hasil);
        } catch (ExecutionException e) {
            // ✅ Selalu getCause() untuk dapat exception aslinya
            Throwable penyebabAsli = e.getCause();
            System.out.println("✅ Exception tertangkap: " + penyebabAsli.getMessage());
            assertInstanceOf(RuntimeException.class, penyebabAsli);
            assertEquals("Koneksi database timeout!", penyebabAsli.getMessage());
        } catch (TimeoutException e) {
            fail("Tidak seharusnya timeout");
        }

        executor.shutdown();
        System.out.println("✅ Semua exception berhasil ditangkap dengan benar!");
    }
}
```

---

### Masalah 3: Tidak Tahu Cara Cancel Future dengan Benar

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class FutureCancelTest {

    /**
     * cancel(false) vs cancel(true) — beda perilaku, wajib tahu!
     *
     * cancel(false) → hanya batalkan kalau task belum mulai berjalan
     * cancel(true)  → interrupt task meskipun sedang berjalan
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void perbedaanCancelFalseVsCancelTrue() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicBoolean taskSatuSelesai = new AtomicBoolean(false);
        AtomicBoolean taskDuaSelesai  = new AtomicBoolean(false);

        // Task 1 — cancel(false): hanya cancel kalau belum mulai
        Future<?> future1 = executor.submit(() -> {
            try {
                Thread.sleep(2000); // sedang berjalan
                taskSatuSelesai.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Task 1 diinterrupt saat tidur");
            }
        });

        // Task 2 — cancel(true): interrupt meskipun sedang berjalan
        Future<?> future2 = executor.submit(() -> {
            try {
                Thread.sleep(2000); // sedang berjalan
                taskDuaSelesai.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Task 2 diinterrupt saat tidur");
            }
        });

        Thread.sleep(200); // biarkan kedua task mulai berjalan

        boolean cancel1 = future1.cancel(false); // tidak interrupt yang sedang jalan
        boolean cancel2 = future2.cancel(true);  // interrupt meskipun sedang jalan

        Thread.sleep(500);

        System.out.println("cancel(false) berhasil cancel: " + cancel1);
        System.out.println("cancel(true)  berhasil cancel: " + cancel2);
        System.out.println("Task 1 selesai (harusnya iya, cancel(false) tidak interrupt): "
                + taskSatuSelesai.get());
        System.out.println("Task 2 selesai (harusnya tidak, diinterrupt): "
                + taskDuaSelesai.get());

        // cancel(false) pada task yang SUDAH berjalan = tidak bisa cancel
        // task akan tetap jalan sampai selesai
        assertTrue(future1.isCancelled() || taskSatuSelesai.get(),
                "Future1 harus cancelled atau task tetap selesai");

        // cancel(true) akan interrupt task yang sedang tidur
        assertTrue(future2.isCancelled(), "Future2 harus cancelled");

        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);
    }
}
```

---

## Bagian 3: CompletableFuture — Future yang Sesungguhnya

`Future` punya keterbatasan besar:
- `get()` bersifat **blocking** — thread yang memanggil harus menunggu
- Tidak bisa di-chain — tidak bisa "kalau A selesai, langsung lakukan B"
- Tidak bisa dikombinasi dengan mudah — "tunggu A dan B, lalu gabungkan hasilnya"
- Tidak bisa set value dari luar

`CompletableFuture` hadir untuk menyelesaikan semua keterbatasan ini.

### Anatomi CompletableFuture

```
CompletableFuture<T>
        │
        ├── implements Future<T>        ← bisa get(), isDone(), dll
        │
        └── implements CompletionStage<T> ← ini yang memberi kekuatan chaining
                │
                ├── thenApply()     → transform hasil (sync)
                ├── thenApplyAsync() → transform hasil (async, di thread lain)
                ├── thenAccept()    → consume hasil tanpa return value
                ├── thenRun()       → jalankan setelah selesai, tidak perlu hasilnya
                ├── thenCompose()   → chain ke CompletableFuture lain (flatMap)
                ├── thenCombine()   → gabungkan dua CompletableFuture
                ├── exceptionally() → handle error, berikan nilai default
                ├── handle()        → handle sukses DAN error sekaligus
                ├── whenComplete()  → observe sukses/error tanpa mengubah hasil
                ├── allOf()         → tunggu semua selesai
                └── anyOf()         → ambil yang paling cepat selesai
```

---

### Masalah 4: Callback Hell — Kode Async yang Tidak Terbaca

Sebelum CompletableFuture, async chaining terlihat seperti ini:

```java
// ❌ Tanpa CompletableFuture — nested callback, susah dibaca dan di-maintain
executor.submit(() -> {
    String userId = fetchUserId(); // step 1
    executor.submit(() -> {
        UserProfile profile = fetchProfile(userId); // step 2
        executor.submit(() -> {
            List<Order> orders = fetchOrders(profile.getId()); // step 3
            // logika bisnis di sini...
            // error handling tersebar di mana-mana
        });
    });
});
```

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class CompletableFutureChainTest {

    // Simulasi service calls yang butuh waktu
    static String fetchUserId() throws Exception {
        Thread.sleep(200);
        return "user-123";
    }

    static String fetchUserName(String userId) throws Exception {
        Thread.sleep(200);
        return "Budi Santoso (id=" + userId + ")";
    }

    static String fetchUserEmail(String userName) throws Exception {
        Thread.sleep(200);
        return userName.toLowerCase().replace(" ", ".") + "@email.com";
    }

    /**
     * ✅ CompletableFuture chaining — linear, mudah dibaca
     *
     * thenApply     → transform nilai, sync (di thread yang sama)
     * thenApplyAsync → transform nilai, async (di thread pool)
     * thenCompose   → chain ke CompletableFuture lain (flatMap, hindari nesting)
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void completableFuture_chaining_linearDanMudahDibaca() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        long mulai = System.currentTimeMillis();

        String hasilAkhir = CompletableFuture
            .supplyAsync(
                () -> {
                    try { return fetchUserId(); }
                    catch (Exception e) { throw new CompletionException(e); }
                },
                executor
            )
            .thenApplyAsync(
                userId -> {
                    System.out.println("[" + Thread.currentThread().getName() + "] Dapat userId: " + userId);
                    try { return fetchUserName(userId); }
                    catch (Exception e) { throw new CompletionException(e); }
                },
                executor
            )
            .thenApplyAsync(
                userName -> {
                    System.out.println("[" + Thread.currentThread().getName() + "] Dapat userName: " + userName);
                    try { return fetchUserEmail(userName); }
                    catch (Exception e) { throw new CompletionException(e); }
                },
                executor
            )
            .get(5, TimeUnit.SECONDS);

        long durasi = System.currentTimeMillis() - mulai;

        System.out.println("Hasil akhir : " + hasilAkhir);
        System.out.println("Durasi      : " + durasi + "ms (sequential ~600ms)");

        assertTrue(hasilAkhir.contains("@email.com"), "Harus berisi email");
        assertTrue(durasi < 2000, "Harus selesai dalam 2 detik");
        System.out.println("✅ Chaining berhasil — linear dan mudah dibaca!");

        executor.shutdown();
    }

    /**
     * thenApply vs thenCompose — ini yang sering bikin bingung
     *
     * thenApply   → T → U                    (transform biasa)
     * thenCompose → T → CompletableFuture<U>  (flatMap, untuk avoid nesting)
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void thenApplyVsThenCompose_kapanPakaiYangMana() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Misalnya kamu punya method yang return CompletableFuture
        // (seperti saat pakai async HTTP client)
        CompletableFuture<String> fetchAsync(String input) {
            return CompletableFuture.supplyAsync(() -> "result-of-" + input, executor);
        }

        // ❌ thenApply dengan function yang return CompletableFuture
        //    → hasilnya CompletableFuture<CompletableFuture<String>> — nested!
        CompletableFuture<CompletableFuture<String>> nested =
            CompletableFuture.supplyAsync(() -> "input", executor)
                .thenApply(s -> fetchAsync(s)); // nested! susah di-get

        // ✅ thenCompose → flatten otomatis, hasilnya CompletableFuture<String>
        CompletableFuture<String> flat =
            CompletableFuture.supplyAsync(() -> "input", executor)
                .thenCompose(s -> fetchAsync(s)); // flat! langsung dapat String

        String hasil = flat.get(3, TimeUnit.SECONDS);
        System.out.println("thenCompose hasil: " + hasil);
        assertEquals("result-of-input", hasil);

        executor.shutdown();
        System.out.println("✅ Gunakan thenCompose kalau function return CompletableFuture!");
    }
}
```

---

### Masalah 5: Tidak Handle Error di Chain — Exception Hilang

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class CompletableFutureErrorHandlingTest {

    /**
     * ❌ Chain tanpa error handling — exception hilang di tengah chain
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void tanpaErrorHandling_exceptionHilang() throws Exception {
        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                throw new RuntimeException("Error di step 1!");
            })
            .thenApply(s -> s.toUpperCase()) // ini tidak jalan kalau step sebelumnya error
            .thenApply(s -> "hasil: " + s);  // ini juga tidak jalan

        // Exception baru ketahuan saat get() → dan dibungkus dalam CompletionException
        try {
            future.get(2, TimeUnit.SECONDS);
            fail("Seharusnya exception!");
        } catch (ExecutionException e) {
            // Di CompletableFuture, exception dibungkus CompletionException
            // yang dibungkus lagi ExecutionException saat get() dipanggil
            System.out.println("Exception class : " + e.getCause().getClass().getSimpleName());
            System.out.println("Pesan           : " + e.getCause().getMessage());
        }
    }

    /**
     * ✅ exceptionally() — berikan nilai default saat terjadi error
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void exceptionally_nilaiDefaultSaatError() throws Exception {
        CompletableFuture<String> future = CompletableFuture
            .<String>supplyAsync(() -> {
                if (Math.random() > 0) { // selalu error untuk demo
                    throw new RuntimeException("Gagal fetch dari API!");
                }
                return "data dari API";
            })
            .thenApply(s -> s.toUpperCase())
            .exceptionally(throwable -> {
                // Dipanggil hanya kalau ada error di chain sebelumnya
                System.out.println("Error: " + throwable.getMessage());
                return "DATA DEFAULT"; // nilai fallback
            });

        String hasil = future.get(2, TimeUnit.SECONDS);
        System.out.println("Hasil: " + hasil);
        assertEquals("DATA DEFAULT", hasil);
        System.out.println("✅ exceptionally() memberikan nilai default saat error");
    }

    /**
     * ✅ handle() — process sukses DAN error dalam satu tempat
     *
     * Bedanya dengan exceptionally():
     * - exceptionally() hanya dipanggil kalau ada error
     * - handle() selalu dipanggil, baik sukses maupun error
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void handle_prosessSuksesAndError() throws Exception {
        // Skenario 1: sukses
        CompletableFuture<String> futureSukses = CompletableFuture
            .supplyAsync(() -> "data asli")
            .handle((hasil, error) -> {
                if (error != null) {
                    return "fallback karena error: " + error.getMessage();
                }
                return hasil.toUpperCase(); // proses normal
            });

        // Skenario 2: error
        CompletableFuture<String> futureError = CompletableFuture
            .<String>supplyAsync(() -> {
                throw new RuntimeException("API down!");
            })
            .handle((hasil, error) -> {
                if (error != null) {
                    return "fallback karena error: " + error.getMessage();
                }
                return hasil.toUpperCase();
            });

        String hasilSukses = futureSukses.get(2, TimeUnit.SECONDS);
        String hasilError  = futureError.get(2, TimeUnit.SECONDS);

        System.out.println("Sukses : " + hasilSukses);
        System.out.println("Error  : " + hasilError);

        assertEquals("DATA ASLI", hasilSukses);
        assertTrue(hasilError.startsWith("fallback"));
        System.out.println("✅ handle() menangani keduanya dengan bersih");
    }
}
```

---

### Masalah 6: Menjalankan Task Paralel Tapi Masih Sequential

Ini pola yang paling sering salah diimplementasikan.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CompletableFutureParallelTest {

    // Simulasi 3 service call yang masing-masing butuh 1 detik
    static String callServiceA() throws Exception {
        Thread.sleep(1000); return "Hasil-A";
    }
    static String callServiceB() throws Exception {
        Thread.sleep(1000); return "Hasil-B";
    }
    static String callServiceC() throws Exception {
        Thread.sleep(1000); return "Hasil-C";
    }

    /**
     * ❌ Sequential — total waktu = 3 detik (1+1+1)
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void sequential_lambat() throws Exception {
        long mulai = System.currentTimeMillis();

        // Ini jalan satu per satu meskipun pakai CompletableFuture!
        // Karena supplyAsync tanpa executor pakai ForkJoinPool.commonPool
        // dan kita get() satu per satu → blocking satu per satu
        String a = CompletableFuture.supplyAsync(() -> {
            try { return callServiceA(); } catch (Exception e) { throw new CompletionException(e); }
        }).get(); // ← blocking di sini sampai A selesai

        String b = CompletableFuture.supplyAsync(() -> {
            try { return callServiceB(); } catch (Exception e) { throw new CompletionException(e); }
        }).get(); // ← blocking di sini sampai B selesai

        String c = CompletableFuture.supplyAsync(() -> {
            try { return callServiceC(); } catch (Exception e) { throw new CompletionException(e); }
        }).get(); // ← blocking di sini sampai C selesai

        long durasi = System.currentTimeMillis() - mulai;
        System.out.printf("Sequential: %s, %s, %s — durasi: %dms%n", a, b, c, durasi);
        assertTrue(durasi >= 3000, "Sequential harus ~3000ms");
    }

    /**
     * ✅ Parallel dengan allOf() — total waktu = ~1 detik (berjalan bersamaan)
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void parallel_denganAllOf_cepatHanya1Detik() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        long mulai = System.currentTimeMillis();

        // Mulai semua task DULU tanpa menunggu
        CompletableFuture<String> futureA = CompletableFuture.supplyAsync(() -> {
            try { return callServiceA(); } catch (Exception e) { throw new CompletionException(e); }
        }, executor);

        CompletableFuture<String> futureB = CompletableFuture.supplyAsync(() -> {
            try { return callServiceB(); } catch (Exception e) { throw new CompletionException(e); }
        }, executor);

        CompletableFuture<String> futureC = CompletableFuture.supplyAsync(() -> {
            try { return callServiceC(); } catch (Exception e) { throw new CompletionException(e); }
        }, executor);

        // allOf() → tunggu SEMUA selesai
        CompletableFuture.allOf(futureA, futureB, futureC)
                .get(5, TimeUnit.SECONDS);

        // Ambil hasil setelah semua selesai (get() di sini tidak blocking lagi)
        String a = futureA.get();
        String b = futureB.get();
        String c = futureC.get();

        long durasi = System.currentTimeMillis() - mulai;
        System.out.printf("Parallel: %s, %s, %s — durasi: %dms%n", a, b, c, durasi);

        assertTrue(durasi < 2000, "Parallel harus ~1000ms, bukan 3000ms");
        assertEquals("Hasil-A", a);
        assertEquals("Hasil-B", b);
        assertEquals("Hasil-C", c);
        System.out.println("✅ 3x lebih cepat dengan parallel execution!");

        executor.shutdown();
    }

    /**
     * ✅ anyOf() — ambil hasil yang paling cepat selesai
     *
     * Use case: kirim request ke beberapa replica, pakai yang paling cepat respond
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void anyOf_ambilYangPalingCepat() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<String> server1 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(1000); return "Response dari Server-1 (lambat)"; }
            catch (Exception e) { throw new CompletionException(e); }
        }, executor);

        CompletableFuture<String> server2 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(300); return "Response dari Server-2 (cepat)"; }
            catch (Exception e) { throw new CompletionException(e); }
        }, executor);

        CompletableFuture<String> server3 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(600); return "Response dari Server-3 (sedang)"; }
            catch (Exception e) { throw new CompletionException(e); }
        }, executor);

        // anyOf() → selesai begitu SALAH SATU selesai
        long mulai = System.currentTimeMillis();
        Object hasilTercepat = CompletableFuture
                .anyOf(server1, server2, server3)
                .get(3, TimeUnit.SECONDS);

        long durasi = System.currentTimeMillis() - mulai;
        System.out.println("Hasil tercepat: " + hasilTercepat);
        System.out.printf("Durasi        : %dms%n", durasi);

        assertEquals("Response dari Server-2 (cepat)", hasilTercepat);
        assertTrue(durasi < 600, "Harus selesai dalam ~300ms (Server-2)");

        executor.shutdown();
        System.out.println("✅ anyOf() mengambil respons tercepat!");
    }

    /**
     * ✅ allOf() dengan List<CompletableFuture> — pola yang sering dipakai di production
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void allOf_denganListDynamic_polaNyata() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Simulasi: fetch detail untuk list of product IDs
        List<Integer> productIds = Arrays.asList(1, 2, 3, 4, 5);

        long mulai = System.currentTimeMillis();

        // Buat semua CompletableFuture sekaligus
        List<CompletableFuture<String>> futures = productIds.stream()
            .map(id -> CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(500); // simulasi DB call
                    return "Product-" + id + " data";
                } catch (InterruptedException e) {
                    throw new CompletionException(e);
                }
            }, executor))
            .collect(Collectors.toList());

        // Tunggu semua selesai
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(5, TimeUnit.SECONDS);

        // Kumpulkan semua hasil
        List<String> hasilSemua = futures.stream()
            .map(CompletableFuture::join) // join() = get() tanpa checked exception
            .collect(Collectors.toList());

        long durasi = System.currentTimeMillis() - mulai;

        System.out.println("Semua hasil: " + hasilSemua);
        System.out.printf("Durasi     : %dms (harusnya ~500ms, bukan %dms sequential)%n",
                durasi, 500 * productIds.size());

        assertEquals(5, hasilSemua.size());
        assertTrue(durasi < 1500, "5 task paralel harus ~500ms, bukan 2500ms");
        System.out.println("✅ Fetch 5 produk paralel — jauh lebih cepat dari sequential!");

        executor.shutdown();
    }
}
```

---

### Masalah 7: Salah Paham thenApply vs thenApplyAsync

Ini sangat penting untuk performa di production.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.*;

public class ThenApplyVsAsyncTest {

    /**
     * thenApply    → callback jalan di thread yang SAMA yang menyelesaikan stage sebelumnya
     * thenApplyAsync → callback jalan di thread yang BERBEDA (dari executor/ForkJoinPool)
     *
     * Kenapa penting?
     * Kalau callback-mu berat (CPU-intensive atau I/O), pakai thenApplyAsync
     * agar thread pool-mu tidak tersumbat.
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void thenApply_jalanDiThreadYangSama() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync  di: " + Thread.currentThread().getName());
            return "data";
        }, executor)
        .thenApply(s -> {
            // ⚠️ Jalan di thread yang sama dengan supplyAsync di atas
            System.out.println("thenApply    di: " + Thread.currentThread().getName());
            return s.toUpperCase();
        })
        .thenApplyAsync(s -> {
            // ✅ Jalan di thread yang BERBEDA dari ForkJoinPool (atau executor jika disebutkan)
            System.out.println("thenApplyAsync di: " + Thread.currentThread().getName());
            return "final-" + s;
        })
        .thenApplyAsync(s -> {
            System.out.println("thenApplyAsync2 di: " + Thread.currentThread().getName());
            return s;
        }, executor) // ✅ Bisa juga specify executor sendiri
        .get(5, TimeUnit.SECONDS);

        executor.shutdown();
        System.out.println("\n✅ Perhatikan nama thread di output!");
        System.out.println("   thenApply     → thread sama dengan sebelumnya");
        System.out.println("   thenApplyAsync → thread baru dari pool");
    }
}
```

---

## Rangkuman Mental Model

```
Runnable    → "Lakukan ini, aku tidak butuh hasilnya"
Callable    → "Lakukan ini, kembalikan hasilnya kalau sudah selesai"
Future      → "Ini tanda terima-mu, ambil hasilnya nanti dengan get()"
CompletableFuture → "Ini pipeline async-mu, define dulu apa yang dilakukan
                     setelah setiap step selesai, tanpa blocking"
```

**Kapan pakai apa:**

| Kebutuhan | Solusi |
|---|---|
| Task tanpa hasil, sekali jalan | `executor.execute(runnable)` |
| Task dengan hasil, tunggu di satu titik | `executor.submit(callable)` → `future.get(timeout)` |
| Chain beberapa async step | `CompletableFuture.supplyAsync().thenApply().thenApply()` |
| Jalankan beberapa task paralel, tunggu semua | `CompletableFuture.allOf(...)` |
| Jalankan beberapa task paralel, ambil tercepat | `CompletableFuture.anyOf(...)` |
| Error handling di async chain | `.exceptionally()` atau `.handle()` |
| Transform yang hasilnya CompletableFuture | `.thenCompose()` bukan `.thenApply()` |

---

## Ringkasan Prinsip Chapter 5

1. **`future.get()` tanpa timeout adalah bom waktu** — selalu pakai `get(timeout, unit)`
2. **Exception di Callable tidak langsung terlihat** — tersimpan di Future, baru keluar saat `get()`, selalu `getCause()` bukan `e` langsung
3. **`cancel(false)` tidak bisa stop task yang sudah jalan** — pakai `cancel(true)` kalau mau interrupt
4. **`thenApply` vs `thenCompose`** — kalau function return CompletableFuture, pakai `thenCompose` untuk menghindari nesting
5. **`thenApply` vs `thenApplyAsync`** — untuk callback yang berat, pakai Async agar thread pool tidak tersumbat
6. **`allOf` untuk tunggu semua, `anyOf` untuk ambil tercepat** — start semua future dulu, baru `allOf`/`anyOf`
7. **Sequential vs parallel** — jangan `get()` satu per satu, mulai semua task dulu baru tunggu dengan `allOf`
8. **`exceptionally` untuk nilai default, `handle` untuk proses sukses dan error sekaligus**

---

*Selanjutnya → Chapter 6: Lock, Atomic & Synchronizer*
