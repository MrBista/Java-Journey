# Panduan Teori Lengkap Generic di Java

Baik, saya akan fokus pada pemahaman konseptual dan teoritis yang mendalam tentang generic di Java.

## 1. Filosofi dan Motivasi Generic

### 1.1 Mengapa Generic Diciptakan?

Sebelum Java 5 (2004), programmer menghadapi beberapa masalah serius:

**Problem 1: Type Safety yang Lemah**
Collections di Java hanya bisa menyimpan Object, yang berarti apapun bisa dimasukkan ke dalamnya. Ini menciptakan situasi berbahaya dimana kesalahan tipe data baru terdeteksi saat runtime, bukan compile time. Bayangkan kamu membuat program banking yang mencampur data String dengan Integer tanpa sengaja - program akan crash saat digunakan customer, bukan saat development.

**Problem 2: Casting yang Berlebihan**
Setiap kali mengambil data dari collection, programmer harus melakukan explicit casting. Ini tidak hanya membuat code verbose dan sulit dibaca, tapi juga rawan error karena casting yang salah akan menyebabkan ClassCastException saat runtime.

**Problem 3: Duplikasi Code**
Jika kamu ingin membuat struktur data yang type-safe untuk berbagai tipe, kamu harus membuat class terpisah untuk setiap tipe (StringList, IntegerList, DoubleList, dll). Ini melanggar prinsip DRY (Don't Repeat Yourself) dan membuat maintenance nightmare.

**Problem 4: Kehilangan Informasi Tipe**
Compiler tidak bisa membantu mendeteksi bug karena tidak tahu tipe data apa yang seharusnya ada dalam collection. Semua type checking harus dilakukan manual oleh programmer.

### 1.2 Solusi yang Ditawarkan Generic

Generic memindahkan type checking dari runtime ke compile time. Ini adalah paradigm shift yang sangat penting dalam programming. Dengan generic:

- **Compile-time Safety**: Kesalahan tipe terdeteksi saat kompilasi, bukan saat program berjalan
- **Eliminasi Casting**: Compiler sudah tahu tipe datanya, tidak perlu casting manual
- **Code Reusability**: Satu class generic bisa bekerja dengan berbagai tipe data
- **Better Documentation**: Tipe parameter di signature method/class menjadi self-documenting
- **Algoritma yang Lebih Generic**: Bisa menulis algoritma yang bekerja untuk berbagai tipe tanpa kehilangan type safety

## 2. Konsep Fundamental Type Parameters

### 2.1 Type Parameter vs Type Argument

Ini adalah distinsi penting yang sering membingungkan:

**Type Parameter** adalah variabel yang kamu deklarasikan saat mendefinisikan generic class/method. Ini seperti parameter di method biasa - placeholder yang akan diisi nanti. Contoh: `T` dalam `class Box<T>`.

**Type Argument** adalah tipe konkret yang kamu berikan saat menggunakan generic class/method. Ini seperti argument yang kamu pass ke method. Contoh: `String` dalam `Box<String>`.

Analogi: Jika class adalah blueprint rumah, type parameter adalah "pilihan warna cat" yang bisa disesuaikan, dan type argument adalah "biru" atau "merah" yang kamu pilih saat membangun rumah tersebut.

### 2.2 Parameterized Types

Parameterized type adalah tipe yang menerima type arguments. `ArrayList<String>` adalah parameterized type dimana `ArrayList` adalah raw type dan `String` adalah type argument.

**Penting dipahami**: `ArrayList<String>` dan `ArrayList<Integer>` adalah dua tipe yang berbeda di compile time, meskipun keduanya menjadi `ArrayList` saja di runtime (karena type erasure).

### 2.3 Raw Types

Raw type adalah penggunaan generic class tanpa type argument, seperti `ArrayList` tanpa `<>`. Ini adalah legacy feature untuk backward compatibility dengan code pre-Java 5.

**Mengapa raw types berbahaya?**
- Kehilangan semua keuntungan type safety
- Bisa mencampur berbagai tipe data tanpa warning
- Compiler tidak bisa membantu detect bugs
- Menghasilkan unchecked warnings

**Kapan raw types masih acceptable?**
Hampir tidak pernah dalam code modern. Satu-satunya exception adalah dalam literal Class, seperti `List.class` (bukan `List<String>.class` karena type erasure).

## 3. Teori Bounded Type Parameters

### 3.1 Upper Bounds (extends)

Upper bound mendefinisikan "batas atas" dari hierarki tipe. Ketika kamu menulis `<T extends Number>`, kamu mengatakan "T bisa berupa Number atau subclass dari Number, tapi tidak boleh superclass dari Number atau class yang tidak related".

**Mengapa ini penting secara teoritis?**

Dengan bounded type parameter, compiler mendapatkan informasi tambahan tentang apa yang bisa dilakukan dengan objek tipe T. Jika T extends Number, compiler tahu bahwa T pasti memiliki semua method yang ada di Number class, seperti `doubleValue()`, `intValue()`, dll.

Ini memungkinkan kamu menulis code yang lebih powerful dalam generic class/method karena kamu tidak terbatas hanya pada method dari Object class.

**Multiple Bounds**

Java mengizinkan multiple bounds dengan sintaks `<T extends ClassA & InterfaceB & InterfaceC>`. Perhatikan bahwa class (jika ada) harus disebutkan pertama, kemudian interfaces.

Mengapa harus class dulu? Karena Java hanya mengizinkan single inheritance untuk class, tapi multiple implementation untuk interface. Compiler perlu tahu hierarki class yang jelas.

### 3.2 Lower Bounds (super)

Lower bound hanya bisa digunakan dengan wildcards, tidak bisa dengan type parameters dalam deklarasi class. Ini adalah design decision yang disengaja.

`<? super Integer>` artinya "tipe tidak diketahui, tapi pasti Integer atau superclass dari Integer". Ini bisa berupa Integer, Number, atau Object.

**Mengapa lower bound penting?**

Lower bound memungkinkan kamu menulis code yang fleksibel untuk operasi "write" atau "consumer". Ketika kamu tahu bahwa collection bisa menampung Integer atau lebih umum, kamu aman untuk memasukkan Integer ke dalamnya.

### 3.3 Unbounded (tidak ada batasan)

Unbounded type parameter seperti `<T>` berarti T bisa berupa class apapun. Compiler hanya tahu bahwa T adalah subclass dari Object, sehingga hanya method dari Object yang bisa dipanggil pada objek tipe T.

## 4. Teori Wildcards (?)

### 4.1 Mengapa Wildcards Diperlukan?

Wildcards diperlukan karena generic dalam Java adalah **invariant**. Ini konsep yang sangat penting.

**Invariance** berarti meskipun `Integer` adalah subclass dari `Number`, `List<Integer>` **BUKAN** subclass dari `List<Number>`. Mereka adalah dua tipe yang completely unrelated di mata compiler.

Mengapa? Karena **heap pollution**. Jika `List<Integer>` adalah subtype dari `List<Number>`, kamu bisa melakukan:

```java
List<Integer> ints = new ArrayList<>();
List<Number> nums = ints; // Jika ini diizinkan (tapi TIDAK!)
nums.add(3.14); // Menambahkan Double
Integer x = ints.get(0); // Runtime error! Dapat Double, expect Integer
```

Wildcards memberikan cara untuk membuat tipe yang lebih fleksibel dengan tetap menjaga type safety.

### 4.2 Unbounded Wildcard (?)

`List<?>` dibaca sebagai "List of unknown type". Ini berbeda dengan `List<Object>`.

**`List<?>`** artinya: "Saya tidak tahu (atau tidak peduli) tipe spesifiknya, tapi ada tipe tertentu yang konsisten". Kamu bisa membaca dari list ini (mendapat Object), tapi tidak bisa menulis ke dalamnya (kecuali null).

**`List<Object>`** artinya: "List yang specifically menyimpan Object". Ini adalah tipe konkret.

**Kapan menggunakan unbounded wildcard?**
Ketika kamu hanya perlu mengakses method yang tidak bergantung pada tipe parameter, seperti `size()`, `clear()`, atau iterasi dengan tipe Object.

### 4.3 Upper Bounded Wildcard (? extends T)

`List<? extends Number>` artinya "List of some type yang merupakan Number atau subclassnya, tapi kita tidak tahu pastinya".

**Covariance**

Upper bounded wildcard memberikan **covariance** yang terbatas. Artinya `List<Integer>` bisa dianggap sebagai `List<? extends Number>`.

**Get Principle (Producer)**

Dari `List<? extends Number>`, kamu aman untuk **membaca** dan mendapatkan Number (atau assign ke variabel Number), karena apapun tipe aktualnya (Integer, Double, Float), pasti bisa di-assign ke Number.

**Put Principle (Consumer) - TIDAK BISA**

Kamu **tidak bisa menulis** ke `List<? extends Number>` (kecuali null), karena compiler tidak tahu tipe pastinya. Jika aktualnya `List<Integer>`, kamu tidak boleh memasukkan Double. Jika aktualnya `List<Double>`, kamu tidak boleh memasukkan Integer.

### 4.4 Lower Bounded Wildcard (? super T)

`List<? super Integer>` artinya "List of some type yang merupakan Integer atau superclassnya".

**Contravariance**

Lower bounded wildcard memberikan **contravariance**. Artinya `List<Number>` bisa dianggap sebagai `List<? super Integer>`.

**Put Principle (Consumer)**

Ke `List<? super Integer>`, kamu aman untuk **menulis** Integer, karena apapun tipe aktualnya (Integer, Number, Object), pasti bisa menampung Integer.

**Get Principle (Producer) - TERBATAS**

Kamu hanya bisa **membaca** sebagai Object dari `List<? super Integer>`, karena compiler tidak tahu tipe pastinya. Tipe aktualnya bisa Object, sehingga compiler tidak bisa guarantee lebih spesifik dari Object.

## 5. PECS Principle (Producer Extends, Consumer Super) - Teori Mendalam

PECS adalah mnemonic untuk mengingat kapan menggunakan extends vs super. Ini didasarkan pada **Liskov Substitution Principle** dan **variance**.

### 5.1 Producer Extends

Jika struktur data adalah **producer** (kamu mengambil/membaca data darinya), gunakan `? extends T`.

**Mengapa?** Karena kamu ingin **covariance** - kemampuan menerima subtypes. Jika method mengharapkan `List<? extends Number>`, kamu bisa pass `List<Integer>` atau `List<Double>`, yang masuk akal karena kamu hanya membaca Number dari dalamnya.

### 5.2 Consumer Super

Jika struktur data adalah **consumer** (kamu memasukkan/menulis data ke dalamnya), gunakan `? super T`.

**Mengapa?** Karena kamu ingin **contravariance** - kemampuan menerima supertypes. Jika method mengharapkan `List<? super Integer>`, kamu bisa pass `List<Number>` atau `List<Object>`, yang masuk akal karena keduanya bisa menampung Integer.

### 5.3 Jika Keduanya?

Jika kamu perlu membaca DAN menulis, jangan gunakan wildcards. Gunakan type parameter konkret seperti `List<T>`.

### 5.4 Hubungan dengan Get-Put Principle

PECS adalah aplikasi dari **Get-Put Principle**:
- Jika hanya **get** (read), gunakan `extends` (covariant)
- Jika hanya **put** (write), gunakan `super` (contravariant)
- Jika get dan put, gunakan exact type (invariant)

## 6. Type Erasure - Teori Mendalam

### 6.1 Apa Itu Type Erasure?

Type erasure adalah proses dimana compiler menghapus semua informasi tentang type parameters saat menghasilkan bytecode. Ini adalah implementation strategy yang dipilih Java untuk menjaga **backward compatibility** dengan code pre-generic.

**Proses Type Erasure:**

1. **Replace type parameters dengan bounds** (atau Object jika unbounded)
2. **Insert type casts** dimana diperlukan untuk menjaga type safety
3. **Generate bridge methods** untuk mempertahankan polymorphism

### 6.2 Mengapa Java Menggunakan Type Erasure?

Ada beberapa alasan fundamental:

**Backward Compatibility**: Code lama yang tidak menggunakan generic harus tetap bisa bekerja dengan code baru yang menggunakan generic, dan sebaliknya. JVM yang lama harus bisa menjalankan class file yang mengandung generic.

**Single Class File**: `ArrayList<String>` dan `ArrayList<Integer>` menghasilkan satu class file yang sama: `ArrayList.class`. Ini menghemat memory dan menjaga simplicity.

**Reified Generics Alternative**: Alternative design (seperti di C#) adalah reified generics dimana informasi tipe disimpan di runtime. Ini lebih powerful tapi akan break compatibility dengan JVM yang existing.

### 6.3 Implikasi Type Erasure

**1. Tidak Bisa Membuat Instance dari Type Parameter**

Karena T di-erase menjadi Object (atau bound-nya), compiler tidak tahu class apa yang harus di-instantiate. `new T()` tidak memiliki makna di runtime.

**2. Tidak Bisa Membuat Array dari Parameterized Type**

Array di Java adalah **reified** (mempertahankan informasi tipe di runtime), sedangkan generics adalah **erased**. Kombinasi keduanya akan menciptakan inconsistency. Array perlu tahu tipe element-nya di runtime untuk ArrayStoreException checking, tapi generic tidak punya informasi ini.

**3. Tidak Bisa Menggunakan instanceof dengan Parameterized Type**

`instanceof` adalah operasi runtime, tapi informasi generic sudah di-erase. Compiler tidak bisa mengetahui tipe parameter saat runtime.

**4. Cannot Overload Methods yang Hanya Berbeda di Type Parameter**

Setelah erasure, `method(List<String>)` dan `method(List<Integer>)` akan menjadi `method(List)` yang sama, sehingga terjadi collision.

**5. Static Context dan Type Parameters**

Static members tidak bisa menggunakan type parameters dari class karena type parameters adalah per-instance information, sedangkan static adalah per-class.

### 6.4 Bridge Methods

Compiler generate bridge methods untuk menjaga polymorphism setelah type erasure. Ini adalah internal mechanism yang programmer tidak perlu handle secara manual, tapi penting untuk dipahami.

Ketika kamu override generic method, signature bisa berbeda setelah erasure. Bridge method adalah synthetic method yang di-generate compiler untuk memastikan polymorphism tetap bekerja.

## 7. Variance dalam Generic - Teori Mendalam

Variance adalah konsep yang menggambarkan bagaimana subtyping relationship antara complex types (seperti `List<String>`) relate dengan subtyping relationship antara component types (seperti `String` dan `Object`).

### 7.1 Invariance (Default di Java)

Generic di Java adalah invariant secara default. Artinya tidak ada hubungan subtyping antara `GenericClass<A>` dan `GenericClass<B>`, meskipun A adalah subtype dari B.

**Mengapa Invariance?**

Untuk menjaga **type safety**. Jika generic adalah covariant, kamu bisa memasukkan tipe yang salah. Jika contravariant, kamu bisa mengambil tipe yang salah.

### 7.2 Covariance (dengan extends)

Covariance berarti jika A adalah subtype dari B, maka `GenericClass<A>` adalah subtype dari `GenericClass<B>`.

Di Java, ini dicapai dengan `? extends`: `List<? extends Number>` adalah supertype dari `List<Integer>`.

**Use Case**: Read-only collections, producers.

### 7.3 Contravariance (dengan super)

Contravariance berarti jika A adalah subtype dari B, maka `GenericClass<B>` adalah subtype dari `GenericClass<A>`.

Di Java, ini dicapai dengan `? super`: `List<? super Integer>` adalah supertype dari `List<Number>`.

**Use Case**: Write-only collections, consumers.

### 7.4 Bivariance

Theoretically, bivariance berarti type bisa di-substitute dengan apapun. Java tidak support true bivariance, tapi unbounded wildcard `?` mendekati konsep ini dengan sangat limited operations.

## 8. Recursive Type Bounds

Ini adalah pattern advanced dimana type parameter dibounded oleh expression yang mengandung type parameter itu sendiri.

**Pattern Umum**: `<T extends Comparable<T>>`

**Makna**: T harus implement Comparable dengan dirinya sendiri sebagai type argument. Ini memastikan bahwa objek tipe T bisa dibandingkan dengan objek tipe T lainnya.

**Mengapa Ini Berguna?**

Ini memungkinkan kamu menulis algoritma seperti sorting atau finding maximum yang bekerja dengan tipe apapun yang bisa dibandingkan dengan dirinya sendiri, tanpa kehilangan type safety.

**Enum Trick**: Semua enum secara otomatis implements `Comparable<E extends Enum<E>>`, yang merupakan contoh recursive bound.

## 9. Generic Exceptions

Java melarang generic exception classes. Kamu tidak bisa membuat `class MyException<T> extends Exception`.

**Mengapa?**

Karena exception handling di Java bergantung pada tipe exception yang exact di runtime (untuk catch blocks), tapi generic information sudah di-erase. Ini akan membuat exception handling menjadi ambiguous atau impossible.

Namun, kamu **bisa** menggunakan type parameters dalam throws clause method.

## 10. Generic dan Reflection

Meskipun type erasure menghapus informasi tipe di runtime, Java Reflection API menyediakan beberapa cara untuk mengakses informasi generic melalui **Type** interface dan implementasinya.

**ParameterizedType**: Merepresentasikan parameterized type seperti `List<String>`.

**TypeVariable**: Merepresentasikan type variable seperti `T`.

**GenericArrayType**: Merepresentasikan array type dengan generic component.

**WildcardType**: Merepresentasikan wildcard type.

Informasi ini tersedia di class file melalui signature attributes, meskipun tidak digunakan oleh JVM saat runtime untuk type checking.

## 11. Design Patterns dengan Generic

### 11.1 Builder Pattern dengan Generics

Generic memungkinkan builder pattern yang lebih type-safe dimana setiap step bisa return type yang lebih spesifik, menggunakan recursive type bound untuk "self type".

### 11.2 Factory Pattern dengan Generics

Generic factory dapat produce instances dari berbagai tipe dengan tetap menjaga type safety, menggunakan Class<T> tokens.

### 11.3 Strategy Pattern dengan Generics

Generic memungkinkan strategy yang type-safe untuk berbagai input/output types.

## 12. Relationship dengan Type System Java

Generic adalah bagian dari **nominal type system** Java. Dalam nominal typing, compatibility ditentukan oleh nama tipe dan declaration hierarchy, bukan oleh struktur (seperti structural typing).

**Subtype Relation**: Generic menambahkan komplexitas pada subtype relation melalui wildcards dan bounds.

**Type Inference**: Java compiler melakukan type inference untuk menentukan type arguments dalam banyak situasi (diamond operator, generic method calls).

---

Apakah ada konsep tertentu yang ingin kamu pahami lebih dalam lagi? Atau aspek teori mana yang masih membingungkan?