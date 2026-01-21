package com.andri.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Selamat datang di pengenalan Java Collections!
 * Di file ini, kita akan melihat contoh penggunaan 3 jenis collection paling umum:
 * 1. List -> Tumpukan berkas yang teratur.
 * 2. Set  -> Kantong item yang unik.
 * 3. Map  -> Kamus dengan pasangan key-value.
 */
public class CollectionIntro {

    public static void main(String[] args) {
        System.out.println("======= Contoh 1: List (Daftar Berurutan) =======");
        demonstrateList();

        System.out.println("\n======= Contoh 2: Set (Kumpulan Unik) =======");
        demonstrateSet();

        System.out.println("\n======= Contoh 3: Map (Peta/Kamus) =======");
        demonstrateMap();
    }

    /**
     * LIST: Seperti daftar belanja.
     * - Berurutan (Urutan elemen sesuai urutan saat dimasukkan).
     * - Bisa berisi elemen duplikat.
     * - Elemen diakses menggunakan nomor urut (indeks) yang dimulai dari 0.
     *
     * Implementasi paling umum: ArrayList.
     */
    public static void demonstrateList() {
        // Membuat List untuk menyimpan nama-nama buah.
        List<String> daftarBuah = new ArrayList<>();

        // Menambahkan elemen ke dalam List
        daftarBuah.add("Apel");
        daftarBuah.add("Jeruk");
        daftarBuah.add("Mangga");
        daftarBuah.add("Apel"); // <-- Kita bisa menambahkan "Apel" lagi (duplikat diizinkan)

        System.out.println("Isi daftar buah: " + daftarBuah);
        System.out.println("Jumlah buah di daftar: " + daftarBuah.size()); // size() untuk melihat ukuran

        // Mengambil elemen berdasarkan nomor urut (indeks)
        String buahPertama = daftarBuah.get(0); // Indeks dimulai dari 0
        System.out.println("Buah pertama dalam daftar adalah: " + buahPertama);

        // Menghapus elemen dari daftar
        daftarBuah.remove("Jeruk");
        System.out.println("Setelah 'Jeruk' dihapus: " + daftarBuah);

        // Mengecek apakah sebuah elemen ada di dalam daftar
        boolean adaMangga = daftarBuah.contains("Mangga");
        System.out.println("Apakah ada 'Mangga' di daftar? " + adaMangga);
    }

    /**
     * SET: Seperti kantong berisi item unik.
     * - Tidak berurutan (Urutan elemen tidak dijamin).
     * - Tidak bisa berisi elemen duplikat.
     *
     * Implementasi paling umum: HashSet.
     */
    public static void demonstrateSet() {
        // Membuat Set untuk menyimpan nomor-nomor unik.
        Set<Integer> nomorUnik = new HashSet<>();

        // Menambahkan elemen ke dalam Set
        nomorUnik.add(10);
        nomorUnik.add(20);
        nomorUnik.add(30);
        nomorUnik.add(10); // <-- Angka 10 sudah ada, penambahan ini akan diabaikan.

        System.out.println("Isi kumpulan nomor unik: " + nomorUnik);
        System.out.println("Jumlah nomor unik: " + nomorUnik.size());

        // Mengecek apakah sebuah elemen ada di dalam Set
        boolean adaAngka20 = nomorUnik.contains(20);
        System.out.println("Apakah ada angka 20? " + adaAngka20);

        // Menghapus elemen
        nomorUnik.remove(30);
        System.out.println("Setelah angka 30 dihapus: " + nomorUnik);
    }

    /**
     * MAP: Seperti kamus atau daftar kontak telepon.
     * - Menyimpan data dalam bentuk pasangan Kunci (Key) dan Nilai (Value).
     * - Setiap Kunci harus unik.
     * - Tidak berurutan (tergantung implementasi, HashMap tidak berurutan).
     *
     * Implementasi paling umum: HashMap.
     */
    public static void demonstrateMap() {
        // Membuat Map untuk menyimpan data ibukota negara.
        // Key -> Nama Negara (String), Value -> Nama Ibukota (String)
        Map<String, String> ibukotaNegara = new HashMap<>();

        // Menambahkan data ke dalam Map menggunakan put(key, value)
        ibukotaNegara.put("Indonesia", "Jakarta");
        ibukotaNegara.put("Jepang", "Tokyo");
        ibukotaNegara.put("Thailand", "Bangkok");
        ibukotaNegara.put("Indonesia", "Nusantara"); // <-- Key "Indonesia" sudah ada, maka valuenya akan di-update.

        System.out.println("Isi peta ibukota negara: " + ibukotaNegara);

        // Mengambil data (value) menggunakan kuncinya (key)
        String ibukotaJepang = ibukotaNegara.get("Jepang");
        System.out.println("Ibukota Jepang adalah: " + ibukotaJepang);

        // Mengecek apakah sebuah kunci ada di dalam Map
        boolean adaPrancis = ibukotaNegara.containsKey("Prancis");
        System.out.println("Apakah ada data untuk negara 'Prancis'? " + adaPrancis);

        // Menghapus data berdasarkan kunci
        ibukotaNegara.remove("Thailand");
        System.out.println("Setelah 'Thailand' dihapus: " + ibukotaNegara);

        // Untuk melihat semua kunci (keys)
        System.out.println("Daftar negara yang ada: " + ibukotaNegara.keySet());

        // Untuk melihat semua nilai (values)
        System.out.println("Daftar ibukota yang ada: " + ibukotaNegara.values());
    }
}
