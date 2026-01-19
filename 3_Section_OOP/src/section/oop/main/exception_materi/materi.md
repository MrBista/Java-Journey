# 4. Exception

## Dasar Exception Handling

### Apa itu Exception?

Exception adalah kejadian yang mengganggu aliran normal eksekusi program. Saat exception terjadi, program berhenti dan mencari handler yang sesuai. Jika tidak ada handler, program akan berakhir dengan error.

### Kenapa Exception Penting?

- Memisahkan kode error-handling dari kode normal
- Menyediakan mekanisme untuk menangani kesalahan secara terstruktur
- Memungkinkan program terus berjalan meskipun terjadi kesalahan

### Hierarki Exception di Java

```
Throwable
|-- Error (masalah serius, biasanya tidak ditangani)
|   |-- OutOfMemoryError
|   |-- StackOverflowError
|   |-- ...
|
|-- Exception (masalah yang bisa ditangani)
    |-- Checked Exception (harus ditangani secara eksplisit)
    |   |-- IOException
    |   |-- SQLException
    |   |-- ClassNotFoundException
    |   |-- ...
    |
    |-- RuntimeException (tidak perlu deklarasi eksplisit)
        |-- ArithmeticException
        |-- NullPointerException
        |-- IndexOutOfBoundsException
        |-- ...

```

### Perbedaan Checked dan Unchecked Exception

**Checked Exception:**

- Turunan dari `Exception` kecuali `RuntimeException`
- Wajib ditangani dengan try-catch atau dideklarasikan dengan `throws`
- Biasanya terjadi karena faktor eksternal (file, jaringan, database)
- Contoh: `IOException`, `SQLException`

**Unchecked Exception:**

- Turunan dari `RuntimeException`
- Tidak perlu deklarasi eksplisit
- Biasanya disebabkan oleh kesalahan pemrograman
- Contoh: `NullPointerException`, `ArithmeticException`

## Cara Menangani Exception

### 1. Try-Catch Block

Struktur dasar:

```java
try {
    // Kode yang mungkin menimbulkan exception
} catch (ExceptionType e) {
    // Kode untuk menangani exception
}

```

Contoh lengkap:

```java
public class ContohTryCatch {
    public static void main(String[] args) {
        try {
            int hasil = 10 / 0; // Akan menimbulkan ArithmeticException
            System.out.println("Hasil: " + hasil); // Baris ini tidak akan dieksekusi
        } catch (ArithmeticException e) {
            System.out.println("Terjadi kesalahan aritmatika: " + e.getMessage());
            // Output: Terjadi kesalahan aritmatika: / by zero
        }

        System.out.println("Program berlanjut..."); // Program tetap berjalan
    }
}

```

### 2. Multiple Catch Blocks

Menangani berbagai jenis exception:

```java
public class ContohMultipleCatch {
    public static void main(String[] args) {
        try {
            int[] angka = {1, 2, 3};
            System.out.println(angka[5]); // ArrayIndexOutOfBoundsException

            int hasil = 10 / 0; // ArithmeticException (tidak akan dieksekusi)
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Kesalahan akses array: " + e.getMessage());
        } catch (ArithmeticException e) {
            System.out.println("Kesalahan aritmatika: " + e.getMessage());
        } catch (Exception e) {
            // Menangkap semua jenis exception lainnya
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
}

```

**Catatan Penting:** Susun catch blocks dari yang spesifik ke yang umum. Jika menempatkan `Exception` di awal, semua exception akan ditangkap oleh blok pertama.

### 3. Multi-catch (Java 7+)

Menangkap beberapa jenis exception dengan satu catch block:

```java
public class ContohMultiCatch {
    public static void main(String[] args) {
        try {
            // Kode yang mungkin menimbulkan exception
            int hasil = Integer.parseInt("abc"); // NumberFormatException
        } catch (NumberFormatException | ArithmeticException e) {
            // Menangani keduanya dengan cara yang sama
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
}

```

### 4. Finally Block

Eksekusi kode yang selalu dijalankan, terlepas dari apakah exception terjadi atau tidak:

```java
public class ContohFinally {
    public static void main(String[] args) {
        FileReader fr = null;
        try {
            fr = new FileReader("file.txt");
            // Operasi file
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan: " + e.getMessage());
        } finally {
            // Kode ini selalu dijalankan
            System.out.println("Blok finally dieksekusi");

            // Menutup resource secara aman
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    System.out.println("Error saat menutup file: " + e.getMessage());
                }
            }
        }
    }
}

```

### 5. Try-with-Resources (Java 7+)

Otomatis menutup resource yang mengimplementasi interface `AutoCloseable`:

```java
public class ContohTryWithResources {
    public static void main(String[] args) {
        // Resource akan otomatis ditutup setelah blok try selesai
        try (FileReader fr = new FileReader("file.txt");
             BufferedReader br = new BufferedReader(fr)) {

            String line = br.readLine();
            System.out.println("Isi file: " + line);

        } catch (IOException e) {
            System.out.println("Terjadi kesalahan IO: " + e.getMessage());
        }
        // Tidak memerlukan finally untuk menutup resource!
    }
}

```

## Melempar Exception (Throw dan Throws)

### 1. Throw

Digunakan untuk melempar exception secara eksplisit:

```java
public class ContohThrow {
    public static void main(String[] args) {
        try {
            validasiUmur(15);
        } catch (IllegalArgumentException e) {
            System.out.println("Kesalahan: " + e.getMessage());
        }
    }

    static void validasiUmur(int umur) {
        if (umur < 18) {
            throw new IllegalArgumentException("Umur minimal 18 tahun");
        } else {
            System.out.println("Umur valid");
        }
    }
}

```

### 2. Throws

Digunakan untuk mendeklarasikan exception yang mungkin dilempar oleh metode:

```java
public class ContohThrows {
    public static void main(String[] args) {
        try {
            bacaFile("data.txt");
        } catch (IOException e) {
            System.out.println("Error saat membaca file: " + e.getMessage());
        }
    }

    // Metode ini mendeklarasikan bahwa ia bisa melempar IOException
    static void bacaFile(String namaFile) throws IOException {
        FileReader fr = new FileReader(namaFile);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        System.out.println("Isi file: " + line);

        br.close();
    }
}

```

## Membuat Exception Kustom

### 1. Exception Kustom Checked

```java
// Exception kustom yang merupakan checked exception
class NilaiTidakValidException extends Exception {
    public NilaiTidakValidException(String pesan) {
        super(pesan);
    }
}

public class ContohExceptionKustom {
    public static void main(String[] args) {
        try {
            validasiNilai(-10);
        } catch (NilaiTidakValidException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void validasiNilai(int nilai) throws NilaiTidakValidException {
        if (nilai < 0) {
            throw new NilaiTidakValidException("Nilai tidak boleh negatif");
        }
        System.out.println("Nilai valid: " + nilai);
    }
}

```

### 2. Exception Kustom Unchecked

```java
// Exception kustom yang merupakan unchecked exception
class NilaiTidakValidRuntimeException extends RuntimeException {
    public NilaiTidakValidRuntimeException(String pesan) {
        super(pesan);
    }
}

public class ContohRuntimeExceptionKustom {
    public static void main(String[] args) {
        // Tidak perlu try-catch (opsional)
        validasiNilai(-5); // Program akan berhenti jika tidak ditangkap
    }

    static void validasiNilai(int nilai) {
        if (nilai < 0) {
            throw new NilaiTidakValidRuntimeException("Nilai tidak boleh negatif");
        }
        System.out.println("Nilai valid: " + nilai);
    }
}

```

## Exception Umum di Java

### 1. RuntimeException

| Exception | Penyebab |
| --- | --- |
| `ArithmeticException` | Operasi aritmatika ilegal, seperti pembagian dengan nol |
| `NullPointerException` | Mencoba mengakses metode/properti dari referensi null |
| `IndexOutOfBoundsException` | Mencoba mengakses indeks yang tidak valid (array, string, dll) |
| `ArrayIndexOutOfBoundsException` | Mencoba mengakses indeks array yang tidak valid |
| `NumberFormatException` | Gagal mengkonversi string ke tipe numerik |
| `IllegalArgumentException` | Argumen metode tidak valid |
| `ClassCastException` | Mencoba melakukan cast objek ke tipe yang tidak kompatibel |
| `UnsupportedOperationException` | Operasi yang tidak didukung |

### 2. Checked Exception

| Exception | Penyebab |
| --- | --- |
| `IOException` | Exception dasar untuk operasi I/O |
| `FileNotFoundException` | File yang direferensikan tidak ditemukan |
| `SQLException` | Exception dasar untuk kesalahan database |
| `ClassNotFoundException` | Class yang diminta tidak ditemukan |
| `InterruptedException` | Thread diinterupsi saat sedang sleep/wait |

## Metode dari Class Throwable

| Metode | Deskripsi |
| --- | --- |
| `getMessage()` | Mengembalikan detail pesan error |
| `toString()` | Mengembalikan nama singkat exception + pesan |
| `printStackTrace()` | Mencetak stack trace ke System.err |
| `getStackTrace()` | Mengembalikan array StackTraceElement |
| `getCause()` | Mengembalikan exception penyebab (jika ada) |

Contoh penggunaan metode exception:

```java
public class ContohMetodeException {
    public static void main(String[] args) {
        try {
            int hasil = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Pesan: " + e.getMessage());
            System.out.println("ToString: " + e.toString());
            System.out.println("StackTrace:");
            e.printStackTrace();

            // Mendapatkan stack trace sebagai array
            StackTraceElement[] stackElements = e.getStackTrace();
            for (StackTraceElement element : stackElements) {
                System.out.println("\tFile: " + element.getFileName() +
                                 ", Line: " + element.getLineNumber() +
                                 ", Method: " + element.getMethodName());
            }
        }
    }
}

```

## Exception Chaining

Membungkus exception dengan exception lain untuk menyimpan informasi penyebab asli:

```java
public class ContohExceptionChaining {
    public static void main(String[] args) {
        try {
            prosesDataPengguna();
        } catch (DataProcessingException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Disebabkan oleh: " + e.getCause().getMessage());
            e.printStackTrace();
        }
    }

    static void prosesDataPengguna() throws DataProcessingException {
        try {
            bacaDataDariFile();
        } catch (IOException e) {
            // Membungkus IOException dengan exception kustom
            throw new DataProcessingException("Gagal memproses data pengguna", e);
        }
    }

    static void bacaDataDariFile() throws IOException {
        throw new IOException("File data rusak atau tidak ditemukan");
    }
}

class DataProcessingException extends Exception {
    public DataProcessingException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}

```

## Praktik Terbaik Exception Handling

1. **Tangkap Exception Spesifik**: Hindari menangkap `Exception` secara umum kecuali sebagai fallback terakhir.
2. **Jangan Menelan Exception**: Selalu lakukan sesuatu yang berarti dengan exception, minimal log errornya.

    ```java
    // BURUK
    try {
        // kode
    } catch (Exception e) {
        // tidak melakukan apa-apa
    }
    
    // BAIK
    try {
        // kode
    } catch (Exception e) {
        logger.error("Terjadi kesalahan", e);
        // atau setidaknya
        System.err.println("Terjadi kesalahan: " + e.getMessage());
    }
    
    ```

3. **Gunakan Try-with-Resources**: Untuk resource yang perlu ditutup.
4. **Buat Exception Kustom yang Bermakna**: Buat exception yang sesuai dengan domain bisnis aplikasi.
5. **Tambahkan Informasi Kontekstual**:

    ```java
    try {
        // kode
    } catch (IOException e) {
        throw new ServiceException("Gagal memuat konfigurasi untuk user: " + userId, e);
    }
    
    ```

6. **Log Stack Trace Lengkap**: Saat logging, sertakan stack trace lengkap.
7. **Hindari Throw Exception Generic**: Lempar exception yang spesifik.
8. **Tangani Exception di Level yang Tepat**: Jangan tangkap exception di level yang terlalu rendah jika tidak bisa membuat keputusan yang tepat.

## Contoh Praktis: Aplikasi Manajemen File

```java
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Exception kustom
class FileOperationException extends Exception {
    public FileOperationException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}

class FileManager {
    public List<String> bacaFile(String path) throws FileOperationException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (FileNotFoundException e) {
            throw new FileOperationException("File tidak ditemukan: " + path, e);
        } catch (IOException e) {
            throw new FileOperationException("Error saat membaca file: " + path, e);
        }
    }

    public void tulisFile(String path, List<String> lines) throws FileOperationException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileOperationException("Error saat menulis ke file: " + path, e);
        }
    }

    public void copyFile(String sourcePath, String destPath) throws FileOperationException {
        List<String> lines = bacaFile(sourcePath);
        tulisFile(destPath, lines);
    }
}

public class AplikasiManajemenFile {
    public static void main(String[] args) {
        FileManager manager = new FileManager();

        try {
            // Baca file
            List<String> lines = manager.bacaFile("source.txt");
            System.out.println("File berhasil dibaca, total " + lines.size() + " baris");

            // Modifikasi data
            lines.add("Baris baru ditambahkan");

            // Tulis ke file baru
            manager.tulisFile("destination.txt", lines);
            System.out.println("File berhasil ditulis");

        } catch (FileOperationException e) {
            System.err.println("Operasi file gagal: " + e.getMessage());
            System.err.println("Penyebab: " + (e.getCause() != null ? e.getCause().getMessage() : "tidak diketahui"));
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Terjadi kesalahan tidak terduga: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

```

## Kesimpulan

1. Exception handling sangat penting untuk membuat program Java yang robust.
2. Pilih dengan bijak antara checked dan unchecked exception.
3. Selalu tutup resource dengan try-with-resources.
4. Buat exception kustom yang bermakna untuk domain aplikasi Anda.
5. Jangan "menelan" exception tanpa penanganan yang tepat.
6. Gunakan exception chaining untuk menyimpan informasi penyebab.
7. Ikuti praktik terbaik untuk membuat kode yang bisa menangani kesalahan dengan baik.