Mantap! Senang mendengarnya sudah berhasil.

Pertanyaan yang bagus. Ini sering membingungkan di awal karena kita terbiasa berpikir "Output Maven = JAR".

Sebenarnya, dalam *multi-module project*, **tidak semua** menjadi `.pom`. Biasanya hanya **Parent (Root)** saja yang packaging-nya `pom`, sedangkan anak-anaknya (modules) tetap `jar`.

Kenapa Parent harus `pom`? Berikut analogi dan alasan teknisnya:

### 1. Analogi: Manajer vs. Pekerja

Bayangkan sebuah proyek konstruksi:

* **Parent (Packaging: POM):** Adalah **Manajer Proyek**. Dia tidak memegang pacul atau semen (tidak punya kode Java). Tugasnya hanya memegang daftar siapa saja pekerjanya dan memastikan semua bekerja sesuai standar.
* **Modules (Packaging: JAR):** Adalah **Tukang/Pekerja**. Merekalah yang benar-benar melakukan pekerjaan (punya kode Java) dan menghasilkan barang jadi.

Jika Manajer ikut-ikutan jadi tukang (packaging: jar), dia malah sibuk ngurusin semen sendiri dan lupa mengkoordinir anak buahnya.

### 2. Alasan Teknis: Aggregator & Inheritance

Dalam Maven, packaging `pom` memiliki fungsi khusus yang tidak dimiliki `jar`:

#### a. Sebagai Aggregator (Pengumpul)

Saat kamu menjalankan `mvn package` di folder paling luar (Root), Maven akan mengecek file `pom.xml`.

* Jika packaging `jar`: Maven akan mencari folder `src/main/java` di root situ untuk dicompile. Padahal di root isinya cuma folder-folder module lain.
* Jika packaging `pom`: Maven paham, "Oke, ini cuma wadah. Saya harus baca tag `<modules>` untuk tahu folder mana saja yang harus saya kunjungi dan saya build."

```xml
<packaging>pom</packaging>
<modules>
    <module>module-core</module>
    <module>module-service</module>
</modules>

```

#### b. Sebagai Blueprint (Inheritance)

Packaging `pom` berfungsi sebagai tempat menaruh konfigurasi umum yang akan **diwariskan** ke anak-anaknya.
Contoh: Kamu set versi Java 17 di Parent. Maka semua module anak otomatis pakai Java 17 tanpa perlu ditulis ulang satu-satu.

### 3. Struktur Folder yang Benar

Jadi, struktur packaging yang umum dalam *Multi-Module* adalah seperti ini:

```text
Root Project (Parent)
│   pom.xml  -----> <packaging>pom</packaging>
│                   (Isinya: daftar modules, versi library, plugin management)
│
├── Module-Core (Child 1)
│   │   pom.xml --> <packaging>jar</packaging>
│   │               (Isinya: kode logika utama, entity, dll)
│   └── src/main/java...
│
└── Module-Web (Child 2)
    │   pom.xml --> <packaging>jar</packaging>
    │               (Isinya: controller, main application)
    └── src/main/java...

```

### Kesimpulan

Jadi, packaging itu `pom` hanya karena dia bertindak sebagai **"Lem"** yang menyatukan module-module lain, bukan sebagai pembungkus kode program.

Apakah struktur project kamu saat ini sudah seperti diagram di atas (Parent `pom`, Child `jar`)?