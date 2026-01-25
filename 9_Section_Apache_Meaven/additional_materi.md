Masalah ini sangat umum terjadi saat kita baru mulai mendalami mekanisme *packaging* di Java.

Secara default, perintah `jar` standar di Java hanya membungkus *class* yang kamu tulis sendiri (`.class` files hasil kompilasi kodemu). Ia **tidak secara otomatis** mengambil kode dari *dependencies* (library luar) yang ada di komputer kamu dan memasukkannya ke dalam JAR tersebut.

Berikut adalah penjelasan mengapa ini terjadi dan solusi standarnya.

### 1. Kenapa Dependencies Tidak Terbawa?

Saat kamu menjalankan aplikasi Java, JVM membutuhkan **Classpath**. Classpath adalah daftar lokasi di mana JVM harus mencari *class*.

* **Saat Development (di IDE):** IDE (seperti IntelliJ atau Eclipse) secara otomatis mengatur Classpath untuk menyertakan kodemu + semua library yang kamu pakai. Makanya, saat di-run di IDE, semuanya berjalan lancar.
* **Saat Build Manual (Default JAR):** Hasil build biasanya disebut **"Skinny JAR"**. Isinya hanya kodemu saja. Jika kamu mencoba menjalankannya dengan `java -jar app.jar`, JVM hanya melihat ke dalam `app.jar`. Karena library luar tidak ada di dalamnya, kamu akan mendapat error `NoClassDefFoundError` atau `ClassNotFoundException`.

---

### 2. Solusi: "Fat JAR" (Uber JAR)

Untuk mendistribusikan aplikasi agar bisa langsung dijalankan di mana saja (tanpa user harus install library manual), kamu memerlukan **Fat JAR** atau **Uber JAR**.

Konsepnya sederhana: Proses build akan membuka ("unzip") semua JAR dari dependencies kamu, mengambil semua file `.class` di dalamnya, lalu menggabungkannya dengan file `.class` milikmu menjadi satu file JAR raksasa.

### 3. Plugin Build Tools

Karena melakukan proses "buka, ambil, gabung" ini sangat rumit jika dilakukan manual, kita menggunakan plugin di Build Tool (Maven atau Gradle).

#### Jika Menggunakan Maven

Ada dua plugin populer, tetapi satu yang paling direkomendasikan untuk *general purpose*:

* **Apache Maven Shade Plugin (Paling Recommended):**
Plugin ini adalah standar industri untuk membuat Fat JAR. Keunggulannya adalah fitur "Relocation", yang bisa mengatasi konflik jika ada dua library yang menggunakan versi class yang berbeda.
* **Cara kerja:** Ia mempaketkan ulang dependencies ke dalam JAR utamamu.


* **Apache Maven Assembly Plugin:**
Ini versi yang lebih tua dan lebih sederhana. Biasanya digunakan jika kamu ingin output yang spesifik (misalnya zip file yang berisi JAR + folder config + dokumentasi), bukan sekadar satu file JAR yang bisa dieksekusi.

**Contoh Konfigurasi `maven-shade-plugin` di `pom.xml`:**

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>com.namapackage.MainClassKamu</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

```

#### Jika Menggunakan Gradle

* **Gradle Shadow Plugin:**
Ini adalah ekuivalen dari Maven Shade di ekosistem Gradle. Sangat mudah digunakan. Cukup tambahkan plugin `id 'com.github.johnrengelman.shadow' version '8.1.1'` (cek versi terbaru) di `build.gradle`, lalu jalankan task `./gradlew shadowJar`.

### 4. Alternatif: Spring Boot

Jika kamu kebetulan sedang membangun aplikasi menggunakan **Spring Boot**, kamu tidak perlu plugin di atas. Spring Boot memiliki plugin khususnya sendiri (`spring-boot-maven-plugin`) yang bekerja dengan cara unik: ia **tidak** meng-unzip dependencies, melainkan memasukkan file JAR dependencies secara utuh ke dalam folder `BOOT-INF/lib` di dalam JAR utamamu, lalu menggunakan *loader* khusus untuk membacanya.

Apakah kamu saat ini menggunakan Maven atau Gradle untuk project Java ini?