# Java Thread — Chapter 3: Deadlock & Cara Menghindarinya

> **Target:** Mid Java Developer yang ingin naik ke Senior
> **Pendekatan:** Masalah → Solusi Salah → Solusi Benar → Kenapa
> **Catatan:** Semua contoh kode bisa langsung dijalankan via JUnit 5

---

## Setup Project

Pastikan kamu punya dependency ini di `pom.xml`:

```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
        </plugin>
    </plugins>
</build>
```

---

## Apa itu Deadlock?

Deadlock terjadi ketika **dua thread atau lebih saling menunggu satu sama lain selamanya** — tidak ada yang bisa lanjut karena masing-masing menunggu resource yang sedang dipegang thread lain.

```
Thread A memegang Lock-1, menunggu Lock-2
Thread B memegang Lock-2, menunggu Lock-1

→ Keduanya menunggu selamanya. Program freeze.
```

Yang membuatnya berbahaya:
- Tidak ada exception yang dilempar — program diam begitu saja
- Tidak selalu terjadi — hanya muncul saat timing-nya pas (sering baru muncul di production)
- Susah direproduksi di local karena tergantung thread scheduling OS

---

## Eksperimen 1: Melihat Deadlock Terjadi

Jalankan test ini dan perhatikan hasilnya — test akan **timeout** karena program freeze.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

public class DeadlockDemoTest {

    /**
     * ❌ DEADLOCK — test ini akan timeout setelah 5 detik
     *
     * Jangan panik kalau test "gagal" — memang ini tujuannya:
     * membuktikan deadlock benar-benar membuat program freeze.
     *
     * Uncomment @Timeout untuk membuktikannya,
     * atau biarkan ter-comment untuk skip test ini.
     */
    // @Test
    // @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void contohDeadlock_akanFreeze() throws InterruptedException {
        Object lockA = new Object();
        Object lockB = new Object();

        Thread threadSatu = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("[Thread-1] Memegang lockA, menunggu lockB...");
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // Thread-1 menunggu lockB, tapi lockB dipegang Thread-2
                synchronized (lockB) {
                    System.out.println("[Thread-1] Berhasil dapat lockB!");
                }
            }
        }, "Thread-1");

        Thread threadDua = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("[Thread-2] Memegang lockB, menunggu lockA...");
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // Thread-2 menunggu lockA, tapi lockA dipegang Thread-1
                synchronized (lockA) {
                    System.out.println("[Thread-2] Berhasil dapat lockA!");
                }
            }
        }, "Thread-2");

        threadSatu.start();
        threadDua.start();

        threadSatu.join(); // Menunggu selamanya...
        threadDua.join(); // Tidak pernah sampai sini

        // Pesan ini tidak akan pernah tercetak
        System.out.println("Selesai!");
    }
}
```

---

## Eksperimen 2: Mendeteksi Deadlock Secara Programatik

Di dunia nyata, kamu perlu bisa **mendeteksi** deadlock, bukan hanya melihat program freeze. Java menyediakan `ThreadMXBean` untuk ini.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeadlockDetectionTest {

    /**
     * ✅ Test ini LULUS — membuktikan kita bisa MENDETEKSI deadlock
     * tanpa program freeze selamanya.
     *
     * Inilah yang dilakukan tools seperti JStack, VisualVM, dan APM.
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void deteksiDeadlock_denganThreadMXBean() throws InterruptedException {
        Object lockA = new Object();
        Object lockB = new Object();
        AtomicBoolean deadlockTerdeteksi = new AtomicBoolean(false);

        // Thread yang akan deadlock
        Thread threadSatu = new Thread(() -> {
            synchronized (lockA) {
                try { Thread.sleep(200); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                synchronized (lockB) {
                    System.out.println("[Thread-1] Tidak akan sampai sini");
                }
            }
        }, "DeadlockThread-1");

        Thread threadDua = new Thread(() -> {
            synchronized (lockB) {
                try { Thread.sleep(200); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                synchronized (lockA) {
                    System.out.println("[Thread-2] Tidak akan sampai sini");
                }
            }
        }, "DeadlockThread-2");

        // Thread detector — inilah cara JStack / APM tools bekerja
        Thread detector = new Thread(() -> {
            ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500); // cek setiap 500ms

                    long[] deadlockedIds = mxBean.findDeadlockedThreads();

                    if (deadlockedIds != null) {
                        ThreadInfo[] infos = mxBean.getThreadInfo(deadlockedIds, true, true);
                        System.out.println("\n⚠️  DEADLOCK TERDETEKSI! ⚠️");
                        for (ThreadInfo info : infos) {
                            System.out.printf("  Thread '%s' [state=%s] menunggu lock yang dipegang oleh '%s'%n",
                                    info.getThreadName(),
                                    info.getThreadState(),
                                    info.getLockOwnerName());
                        }
                        deadlockTerdeteksi.set(true);

                        // Interrupt thread yang deadlock agar program tidak freeze
                        threadSatu.interrupt();
                        threadDua.interrupt();
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "DeadlockDetector");

        detector.setDaemon(true); // detector berhenti kalau main thread selesai
        threadSatu.start();
        threadDua.start();
        detector.start();

        threadSatu.join(5000);
        threadDua.join(5000);

        assertTrue(deadlockTerdeteksi.get(),
                "Deadlock seharusnya terdeteksi oleh ThreadMXBean");

        System.out.println("\n✅ Test lulus — deadlock berhasil dideteksi dan ditangani!");
    }
}
```

**Output yang diharapkan:**
```
⚠️  DEADLOCK TERDETEKSI! ⚠️
  Thread 'DeadlockThread-1' [state=BLOCKED] menunggu lock yang dipegang oleh 'DeadlockThread-2'
  Thread 'DeadlockThread-2' [state=BLOCKED] menunggu lock yang dipegang oleh 'DeadlockThread-1'

✅ Test lulus — deadlock berhasil dideteksi dan ditangani!
```

---

## Solusi 1: Urutan Lock yang Konsisten

Cara paling sederhana menghindari deadlock: **selalu ambil lock dalam urutan yang sama**.

```java
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeadlockSolusiUrutanLockTest {

    static class RekeningBank {
        private final int id;
        private double saldo;

        RekeningBank(int id, double saldoAwal) {
            this.id = id;
            this.saldo = saldoAwal;
        }

        public int getId() { return id; }
        public double getSaldo() { return saldo; }

        /**
         * ❌ Transfer yang bisa deadlock:
         * Thread A transfer dari rekening-1 ke rekening-2 → ambil lock rekening-1 dulu
         * Thread B transfer dari rekening-2 ke rekening-1 → ambil lock rekening-2 dulu
         * → Deadlock!
         */
        public static void transferTidakAman(RekeningBank dari, RekeningBank ke, double jumlah) {
            synchronized (dari) {
                synchronized (ke) {
                    dari.saldo -= jumlah;
                    ke.saldo += jumlah;
                }
            }
        }

        /**
         * ✅ Transfer yang aman:
         * Selalu lock rekening dengan id lebih kecil dulu, apapun arah transfernya.
         * Tidak peduli Thread A atau B, urutannya selalu sama → tidak ada deadlock.
         */
        public static void transferAman(RekeningBank dari, RekeningBank ke, double jumlah) {
            // Tentukan urutan lock berdasarkan id (konsisten, tidak tergantung arah transfer)
            RekeningBank lockPertama  = dari.id < ke.id ? dari : ke;
            RekeningBank lockKedua   = dari.id < ke.id ? ke   : dari;

            synchronized (lockPertama) {
                synchronized (lockKedua) {
                    dari.saldo -= jumlah;
                    ke.saldo   += jumlah;
                }
            }
        }
    }

    /**
     * Test dijalankan 5 kali (RepeatedTest) untuk memastikan
     * tidak ada race condition yang hanya muncul sesekali.
     */
    @RepeatedTest(5)
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void transferParalel_denganUrutanLockKonsisten_tidakDeadlock() throws InterruptedException {
        RekeningBank rekening1 = new RekeningBank(1, 1_000_000);
        RekeningBank rekening2 = new RekeningBank(2, 1_000_000);

        double totalAwal = rekening1.getSaldo() + rekening2.getSaldo();
        int jumlahTransfer = 1000;
        CountDownLatch latch = new CountDownLatch(2);

        // Thread A: transfer rekening1 → rekening2 (1000 kali)
        Thread threadA = new Thread(() -> {
            for (int i = 0; i < jumlahTransfer; i++) {
                RekeningBank.transferAman(rekening1, rekening2, 100);
            }
            latch.countDown();
        }, "TransferA");

        // Thread B: transfer rekening2 → rekening1 (1000 kali) — arah berlawanan!
        Thread threadB = new Thread(() -> {
            for (int i = 0; i < jumlahTransfer; i++) {
                RekeningBank.transferAman(rekening2, rekening1, 100);
            }
            latch.countDown();
        }, "TransferB");

        threadA.start();
        threadB.start();
        latch.await(5, TimeUnit.SECONDS);

        double totalAkhir = rekening1.getSaldo() + rekening2.getSaldo();

        System.out.printf("Rekening 1: Rp %.0f | Rekening 2: Rp %.0f | Total: Rp %.0f%n",
                rekening1.getSaldo(), rekening2.getSaldo(), totalAkhir);

        assertEquals(totalAwal, totalAkhir,
                "Total saldo harus tetap sama setelah semua transfer");
        System.out.println("✅ Tidak ada deadlock, saldo konsisten!");
    }
}
```

---

## Solusi 2: tryLock dengan Timeout (ReentrantLock)

Ketika urutan lock konsisten tidak bisa diterapkan (misalnya urutannya dinamis), gunakan `tryLock()` dengan timeout — thread tidak menunggu selamanya, dan bisa mencoba lagi nanti.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TryLockTimeoutTest {

    static class RekeningDenganTryLock {
        private final int id;
        private double saldo;
        private final ReentrantLock lock = new ReentrantLock();

        RekeningDenganTryLock(int id, double saldoAwal) {
            this.id = id;
            this.saldo = saldoAwal;
        }

        public double getSaldo() { return saldo; }

        /**
         * Transfer dengan tryLock — tidak pernah menunggu selamanya.
         * Kalau tidak bisa dapat lock dalam 100ms, kembalikan false dan coba lagi nanti.
         */
        public static boolean transfer(RekeningDenganTryLock dari,
                                       RekeningDenganTryLock ke,
                                       double jumlah) throws InterruptedException {
            boolean dapatLockDari = false;
            boolean dapatLockKe   = false;

            try {
                dapatLockDari = dari.lock.tryLock(100, TimeUnit.MILLISECONDS);
                dapatLockKe   = ke.lock.tryLock(100, TimeUnit.MILLISECONDS);

                if (dapatLockDari && dapatLockKe) {
                    dari.saldo -= jumlah;
                    ke.saldo   += jumlah;
                    return true; // transfer berhasil
                }

                return false; // tidak dapat lock, coba lagi nanti

            } finally {
                // WAJIB di finally — pastikan lock selalu dilepas!
                if (dapatLockDari) dari.lock.unlock();
                if (dapatLockKe)   ke.lock.unlock();
            }
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void transferDenganTryLock_tidakPernahDeadlock() throws InterruptedException {
        RekeningDenganTryLock rekening1 = new RekeningDenganTryLock(1, 500_000);
        RekeningDenganTryLock rekening2 = new RekeningDenganTryLock(2, 500_000);

        double totalAwal = rekening1.getSaldo() + rekening2.getSaldo();

        AtomicInteger berhasil = new AtomicInteger(0);
        AtomicInteger gagal    = new AtomicInteger(0);
        int targetTransfer = 500;
        CountDownLatch latch = new CountDownLatch(2);

        Thread threadA = new Thread(() -> {
            int sukses = 0;
            while (sukses < targetTransfer) {
                try {
                    if (RekeningDenganTryLock.transfer(rekening1, rekening2, 1000)) {
                        sukses++;
                        berhasil.incrementAndGet();
                    } else {
                        gagal.incrementAndGet();
                        Thread.sleep(10); // tunggu sebentar sebelum retry
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            latch.countDown();
        }, "TransferA");

        Thread threadB = new Thread(() -> {
            int sukses = 0;
            while (sukses < targetTransfer) {
                try {
                    if (RekeningDenganTryLock.transfer(rekening2, rekening1, 1000)) {
                        sukses++;
                        berhasil.incrementAndGet();
                    } else {
                        gagal.incrementAndGet();
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            latch.countDown();
        }, "TransferB");

        threadA.start();
        threadB.start();
        latch.await();

        double totalAkhir = rekening1.getSaldo() + rekening2.getSaldo();

        System.out.printf("Transfer berhasil : %d%n", berhasil.get());
        System.out.printf("Transfer retry    : %d%n", gagal.get());
        System.out.printf("Total saldo awal  : Rp %.0f%n", totalAwal);
        System.out.printf("Total saldo akhir : Rp %.0f%n", totalAkhir);

        assertEquals(totalAwal, totalAkhir, "Total saldo harus tetap sama");
        assertTrue(berhasil.get() == targetTransfer * 2,
                "Semua transfer harus berhasil (walaupun ada retry)");
        System.out.println("✅ Tidak ada deadlock, semua transfer selesai!");
    }
}
```

---

## Solusi 3: Hindari Nested Lock — Refactor Strukturnya

Deadlock paling sering terjadi karena **nested synchronized** (synchronized di dalam synchronized). Kalau bisa, hilangkan kebutuhan nested lock dengan refactor.

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HindariNestedLockTest {

    /**
     * ❌ Desain yang rentan deadlock — object saling mengunci satu sama lain
     */
    static class KeranjangBelanjaRentan {
        private final List<String> items = new ArrayList<>();
        private final Object lock = new Object();

        public void tambahItem(String item) {
            synchronized (lock) {
                items.add(item);
            }
        }

        // ❌ Method ini mengunci 'this', lalu mencoba mengunci keranjang lain
        // → potensi deadlock kalau dua thread saling pindah bersamaan
        public void pindahSemuaKe_TIDAK_AMAN(KeranjangBelanjaRentan tujuan) {
            synchronized (this.lock) {
                synchronized (tujuan.lock) { // ← nested lock, bahaya!
                    tujuan.items.addAll(this.items);
                    this.items.clear();
                }
            }
        }
    }

    /**
     * ✅ Desain yang aman — ambil data dulu (dengan lock), proses di luar lock
     */
    static class KeranjangBelanjaAman {
        private final List<String> items = new ArrayList<>();
        private final Object lock = new Object();

        public void tambahItem(String item) {
            synchronized (lock) {
                items.add(item);
            }
        }

        // Ambil semua item dan hapus — dalam satu operasi atomic
        public List<String> ambilSemua() {
            synchronized (lock) {
                List<String> copy = new ArrayList<>(items);
                items.clear();
                return copy; // kembalikan copy, release lock
            }
        }

        public void tambahSemua(List<String> itemBaru) {
            synchronized (lock) {
                items.addAll(itemBaru);
            }
        }

        public int jumlahItem() {
            synchronized (lock) {
                return items.size();
            }
        }

        // ✅ Tidak ada nested lock — operasi dipisah, tidak ada dua lock sekaligus
        public static void pindahSemua(KeranjangBelanjaAman dari, KeranjangBelanjaAman ke) {
            List<String> itemDipindah = dari.ambilSemua(); // lock dari, lalu release
            ke.tambahSemua(itemDipindah);                  // lock ke, lalu release
            // Tidak pernah pegang dua lock sekaligus!
        }
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void pindahKeranjang_tanpaNestedLock_tidakDeadlock() throws InterruptedException {
        KeranjangBelanjaAman keranjangA = new KeranjangBelanjaAman();
        KeranjangBelanjaAman keranjangB = new KeranjangBelanjaAman();

        // Isi keranjang awal
        for (int i = 1; i <= 10; i++) keranjangA.tambahItem("Produk-" + i);
        for (int i = 1; i <= 5;  i++) keranjangB.tambahItem("Bonus-"  + i);

        int totalItemAwal = keranjangA.jumlahItem() + keranjangB.jumlahItem();
        CountDownLatch latch = new CountDownLatch(2);

        // Thread A: pindah dari A ke B
        Thread threadA = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                KeranjangBelanjaAman.pindahSemua(keranjangA, keranjangB);
            }
            latch.countDown();
        });

        // Thread B: pindah dari B ke A (arah berlawanan — ini yang bikin deadlock pada desain lama)
        Thread threadB = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                KeranjangBelanjaAman.pindahSemua(keranjangB, keranjangA);
            }
            latch.countDown();
        });

        threadA.start();
        threadB.start();
        latch.await(5, TimeUnit.SECONDS);

        int totalItemAkhir = keranjangA.jumlahItem() + keranjangB.jumlahItem();

        System.out.printf("Total item awal  : %d%n", totalItemAwal);
        System.out.printf("Total item akhir : %d%n", totalItemAkhir);

        assertEquals(totalItemAwal, totalItemAkhir,
                "Total item harus tetap sama setelah semua pemindahan");
        System.out.println("✅ Tidak ada deadlock, semua item berhasil dipindahkan!");
    }
}
```

---

## Bonus: Cara Baca Thread Dump saat Deadlock di Production

Ketika terjadi deadlock di production, tool pertama yang kamu pakai adalah **thread dump**. Kamu bisa generate-nya dengan:

```bash
# Cari PID aplikasi Java
jps -l

# Generate thread dump
jstack <PID>

# Atau kirim signal ke process
kill -3 <PID>
```

Thread dump saat deadlock terlihat seperti ini:

```
Found one Java-level deadlock:
=============================

"TransferA":
  waiting to lock monitor 0x00007f (object 0x000000076b3b3ac8, a RekeningBank),
  which is held by "TransferB"

"TransferB":
  waiting to lock monitor 0x00007e (object 0x000000076b3b3a90, a RekeningBank),
  which is held by "TransferA"

Java stack information for the threads listed above:
===================================================
"TransferA":
  at DeadlockDemo.transfer(DeadlockDemo.java:42)
  - waiting to lock <0x000000076b3b3ac8> (a RekeningBank)
  - locked <0x000000076b3b3a90> (a RekeningBank)

"TransferB":
  at DeadlockDemo.transfer(DeadlockDemo.java:42)
  - waiting to lock <0x000000076b3b3a90> (a RekeningBank)
  - locked <0x000000076b3b3ac8> (a RekeningBank)
```

Cara baca thread dump:
- `waiting to lock` → lock yang sedang ditunggu
- `locked` → lock yang sedang dipegang
- Kalau kamu lihat siklus (A nunggu B, B nunggu A) → itulah deadlock-nya

---

## Ringkasan Prinsip Chapter 3

1. **Deadlock tidak melempar exception** — program diam tanpa pesan error, itulah kenapa berbahaya
2. **Deteksi dengan ThreadMXBean** — ini yang dipakai JStack, VisualVM, dan semua APM tools
3. **Urutan lock yang konsisten** — solusi paling sederhana, lock selalu berdasarkan urutan tetap
4. **tryLock dengan timeout** — gunakan ReentrantLock kalau urutan lock tidak bisa konsisten
5. **Hindari nested lock** — kalau bisa, pisahkan operasi agar tidak pernah pegang dua lock sekaligus
6. **Baca thread dump** — skill wajib senior untuk debug deadlock di production

---

*Selanjutnya → Chapter 4: ThreadPool & ExecutorService*
