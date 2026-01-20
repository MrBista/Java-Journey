# Panduan Lengkap Generic di Java

Saya akan mengajarkan generic di Java secara mendalam dari dasar hingga tingkat lanjut. Mari kita mulai dari konsep fundamental.

## 1. Apa itu Generic?

Generic adalah fitur Java yang memungkinkan kita membuat class, interface, dan method yang dapat bekerja dengan berbagai tipe data, namun tetap type-safe (aman tipe). Generic diperkenalkan di Java 5 untuk mengurangi kesalahan tipe data saat runtime.

**Analogi sederhana:** Bayangkan kamu punya kotak penyimpanan. Tanpa generic, kotak itu bisa menyimpan apa saja (sepatu, buku, makanan) dan kamu harus selalu mengecek isinya. Dengan generic, kamu bisa memberi label khusus seperti "Kotak Sepatu" atau "Kotak Buku", sehingga kamu tahu pasti apa yang ada di dalamnya.

## 2. Mengapa Generic Penting?

**Tanpa Generic (cara lama):**
```java
ArrayList list = new ArrayList();
list.add("Hello");
list.add(123); // Bisa menambahkan tipe apapun
String s = (String) list.get(1); // Runtime Error! Karena get(1) adalah Integer
```

**Dengan Generic:**
```java
ArrayList<String> list = new ArrayList<String>();
list.add("Hello");
// list.add(123); // Compile Error! Tidak bisa menambahkan Integer
String s = list.get(0); // Tidak perlu casting, aman!
```

## 3. Sintaks Dasar Generic

### 3.1 Generic Class

```java
// Class generic sederhana
public class Box<T> {
    private T content;
    
    public void setContent(T content) {
        this.content = content;
    }
    
    public T getContent() {
        return content;
    }
}

// Penggunaan:
Box<String> stringBox = new Box<String>();
stringBox.setContent("Hello");
String value = stringBox.getContent(); // Tidak perlu casting

Box<Integer> intBox = new Box<Integer>();
intBox.setContent(100);
Integer number = intBox.getContent();
```

**Penjelasan:**
- `T` adalah type parameter (bisa diberi nama apa saja, tapi konvensinya: T = Type, E = Element, K = Key, V = Value)
- Saat membuat objek, kita tentukan tipe konkretnya: `Box<String>`, `Box<Integer>`
- Compiler akan memastikan hanya tipe yang sesuai yang bisa digunakan

### 3.2 Multiple Type Parameters

```java
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
}

// Penggunaan:
Pair<String, Integer> pair1 = new Pair<>("Umur", 25);
Pair<Integer, String> pair2 = new Pair<>(1, "Satu");
```

### 3.3 Generic Methods

Method generic bisa didefinisikan di class biasa maupun generic class:

```java
public class Utility {
    // Generic method
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
    
    // Generic method dengan return type
    public static <T> T getFirst(T[] array) {
        if (array.length > 0) {
            return array[0];
        }
        return null;
    }
}

// Penggunaan:
String[] names = {"Alice", "Bob", "Charlie"};
Integer[] numbers = {1, 2, 3};

Utility.printArray(names);    // Alice Bob Charlie
Utility.printArray(numbers);  // 1 2 3

String firstName = Utility.getFirst(names);
Integer firstNumber = Utility.getFirst(numbers);
```

## 4. Bounded Type Parameters

Kadang kita ingin membatasi tipe yang bisa digunakan. Ada dua jenis: upper bound dan lower bound.

### 4.1 Upper Bounded Wildcards (`extends`)

```java
// Hanya menerima Number dan turunannya
public class NumberBox<T extends Number> {
    private T number;
    
    public void setNumber(T number) {
        this.number = number;
    }
    
    public double getDoubleValue() {
        return number.doubleValue(); // Bisa memanggil method dari Number
    }
}

// Penggunaan:
NumberBox<Integer> intBox = new NumberBox<>();
NumberBox<Double> doubleBox = new NumberBox<>();
// NumberBox<String> stringBox = new NumberBox<>(); // ERROR! String bukan Number
```

**Contoh praktis dengan multiple bounds:**
```java
// T harus implement Comparable DAN Serializable
public class ComparableBox<T extends Comparable<T> & Serializable> {
    private T value;
    
    public boolean isGreaterThan(T other) {
        return value.compareTo(other) > 0;
    }
}
```

### 4.2 Lower Bounded Wildcards (`super`)

```java
public class CollectionUtil {
    // Menerima List yang bisa menampung Integer atau superclass-nya (Number, Object)
    public static void addIntegers(List<? super Integer> list) {
        list.add(10);
        list.add(20);
        // Aman menambahkan Integer ke list apapun yang bisa menampung Integer
    }
}

// Penggunaan:
List<Integer> intList = new ArrayList<>();
List<Number> numberList = new ArrayList<>();
List<Object> objectList = new ArrayList<>();

CollectionUtil.addIntegers(intList);     // OK
CollectionUtil.addIntegers(numberList);  // OK
CollectionUtil.addIntegers(objectList);  // OK
```

## 5. Wildcards (?)

### 5.1 Unbounded Wildcard (`?`)

```java
public static void printList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

// Bisa menerima List dengan tipe apapun
printList(new ArrayList<String>());
printList(new ArrayList<Integer>());
printList(new ArrayList<Double>());
```

### 5.2 Upper Bounded Wildcard (`? extends Type`)

```java
public static double sumNumbers(List<? extends Number> list) {
    double sum = 0;
    for (Number num : list) {
        sum += num.doubleValue();
    }
    return sum;
}

// Penggunaan:
List<Integer> integers = Arrays.asList(1, 2, 3);
List<Double> doubles = Arrays.asList(1.5, 2.5, 3.5);

System.out.println(sumNumbers(integers)); // 6.0
System.out.println(sumNumbers(doubles));  // 7.5
```

### 5.3 Lower Bounded Wildcard (`? super Type`)

```java
public static void fillWithNumbers(List<? super Integer> list) {
    for (int i = 1; i <= 5; i++) {
        list.add(i);
    }
}

List<Integer> ints = new ArrayList<>();
List<Number> nums = new ArrayList<>();
List<Object> objs = new ArrayList<>();

fillWithNumbers(ints);  // OK
fillWithNumbers(nums);  // OK
fillWithNumbers(objs);  // OK
```

## 6. PECS Principle (Producer Extends, Consumer Super)

Ini adalah aturan penting untuk menentukan kapan menggunakan `extends` atau `super`:

- **Producer Extends**: Jika kamu hanya **membaca** dari struktur data, gunakan `? extends T`
- **Consumer Super**: Jika kamu hanya **menulis** ke struktur data, gunakan `? super T`

```java
public class PECSExample {
    // Producer - kita hanya MEMBACA dari source
    public static <T> void copy(
        List<? extends T> source,  // Producer: extends
        List<? super T> destination  // Consumer: super
    ) {
        for (T item : source) {
            destination.add(item);
        }
    }
}

// Penggunaan:
List<Integer> integers = Arrays.asList(1, 2, 3);
List<Number> numbers = new ArrayList<>();

PECSExample.copy(integers, numbers); // OK: Integer extends Number
```

## 7. Type Erasure

Java menggunakan type erasure, yang berarti informasi generic dihapus saat runtime:

```java
List<String> stringList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();

// Saat runtime, keduanya menjadi List saja
System.out.println(stringList.getClass() == intList.getClass()); // true
```

**Implikasi Type Erasure:**

```java
// TIDAK BISA:
// 1. Membuat instance dari type parameter
// T obj = new T(); // ERROR!

// 2. Membuat array generic
// T[] array = new T[10]; // ERROR!

// 3. Instanceof dengan generic
// if (obj instanceof List<String>) // ERROR!

// BISA:
if (obj instanceof List<?>) // OK
```

## 8. Generic Interface

```java
public interface Repository<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
    void delete(T entity);
}

// Implementasi:
public class UserRepository implements Repository<User, Long> {
    private Map<Long, User> database = new HashMap<>();
    
    @Override
    public void save(User entity) {
        database.put(entity.getId(), entity);
    }
    
    @Override
    public User findById(Long id) {
        return database.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(database.values());
    }
    
    @Override
    public void delete(User entity) {
        database.remove(entity.getId());
    }
}
```

## 9. Contoh Praktis Lengkap

Saya akan membuat contoh aplikasi mini yang mendemonstrasikan berbagai aspek generic:

```java
// Generic Stack Implementation
public class GenericStack<E> {
    private List<E> elements;
    private int maxSize;
    
    public GenericStack(int maxSize) {
        this.elements = new ArrayList<>();
        this.maxSize = maxSize;
    }
    
    public void push(E element) {
        if (elements.size() >= maxSize) {
            throw new RuntimeException("Stack is full!");
        }
        elements.add(element);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty!");
        }
        return elements.remove(elements.size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty!");
        }
        return elements.get(elements.size() - 1);
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public int size() {
        return elements.size();
    }
}

// Generic Tree Node
class TreeNode<T extends Comparable<T>> {
    private T data;
    private TreeNode<T> left;
    private TreeNode<T> right;
    
    public TreeNode(T data) {
        this.data = data;
    }
    
    public void insert(T value) {
        if (value.compareTo(data) < 0) {
            if (left == null) {
                left = new TreeNode<>(value);
            } else {
                left.insert(value);
            }
        } else {
            if (right == null) {
                right = new TreeNode<>(value);
            } else {
                right.insert(value);
            }
        }
    }
    
    public boolean contains(T value) {
        if (value.compareTo(data) == 0) {
            return true;
        } else if (value.compareTo(data) < 0) {
            return left != null && left.contains(value);
        } else {
            return right != null && right.contains(value);
        }
    }
}

// Penggunaan:
public class Main {
    public static void main(String[] args) {
        // Stack
        GenericStack<String> stringStack = new GenericStack<>(5);
        stringStack.push("Java");
        stringStack.push("Generic");
        System.out.println(stringStack.pop()); // Generic
        
        // Tree
        TreeNode<Integer> root = new TreeNode<>(50);
        root.insert(30);
        root.insert(70);
        root.insert(20);
        System.out.println(root.contains(30)); // true
    }
}
```

## 10. Best Practices

1. **Gunakan nama konvensi untuk type parameters:**
    - T = Type
    - E = Element
    - K = Key
    - V = Value
    - N = Number

2. **Preferensi List<String> daripada List:**
   Selalu gunakan generic untuk type safety.

3. **Gunakan bounded type ketika perlu:**
   Jika kamu butuh method tertentu, batasi dengan `extends`.

4. **PECS untuk wildcard:**
   Producer Extends, Consumer Super.

5. **Hindari raw types:**
   ```java
   List list = new ArrayList(); // Jangan!
   List<Object> list = new ArrayList<>(); // Lebih baik
   ```

## 11. Latihan

Coba buat sendiri:

1. Generic class `Pair<K, V>` dengan method untuk swap key dan value
2. Generic method untuk mencari elemen maksimum dalam array
3. Generic class `Cache<K, V>` sederhana dengan expiration time
4. Generic method untuk merge dua List dengan tipe yang compatible

Apakah ada bagian yang ingin kamu pelajari lebih dalam? Atau ingin mencoba latihan bersama?



Ini adalah langkah yang tepat. Karena Anda sudah memiliki pengalaman dengan **Go** (yang menggunakan pendekatan *monomorphization* di mana kode diduplikasi untuk setiap tipe) dan sekarang mendalami **Java Spring Boot**, memahami keanehan Java Generics sangat krusial.

Masalah utama di Java adalah **Type Erasure** yang kita bahas sebelumnya. Karena tipe data "dihapus" saat runtime, Java harus melakukan banyak "akrobat" di belakang layar.

Mari kita masuk ke level **Deep Dive**. Ini adalah materi yang membedakan "Java User" biasa dengan "Java Expert".

---

### 1. Invariance vs Covariance (Konsep Paling Sering Salah Paham)

Sebelum masuk ke sintaks, Anda harus paham sifat dasar wadah data di Java.

* **Array di Java bersifat Covariant.**
  Artinya: Jika `Integer` adalah anak dari `Number`, maka `Integer[]` adalah anak dari `Number[]`.
  *Bahaya:* Ini bisa menyebabkan runtime error.
```java
Number[] nums = new Integer[10];
nums[0] = 3.14; // Compile OK, tapi Runtime Error (ArrayStoreException)!
// Karena kita mencoba memasukkan Double ke array Integer.

```


* **Generics di Java bersifat Invariant.**
  Artinya: `List<Integer>` **BUKAN** anak dari `List<Number>`. Mereka adalah tipe yang sama sekali berbeda di mata compiler.
  *Tujuan:* Mencegah runtime error yang terjadi pada Array di atas.
```java
List<Integer> ints = new ArrayList<>();
// List<Number> nums = ints; // COMPILER ERROR!
// Java melarang ini di awal agar kita tidak tidak sengaja memasukkan Double ke list Integer.

```



**Poin Kunci:** Inilah alasan kenapa kita butuh Wildcard (`? extends T`). Wildcard adalah cara kita memaksa Generics menjadi *Covariant* (bisa menerima subtype) atau *Contravariant* (bisa menerima supertype) secara manual.

---

### 2. Recursive Type Bound (Pattern `Comparable`)

Anda akan sering melihat ini di library Java atau Spring. Pola ini terlihat membingungkan: `<T extends Comparable<T>>`.

**Teori:**
Ini digunakan ketika sebuah tipe `T` perlu membandingkan dirinya sendiri dengan objek lain yang bertipe `T` juga (bukan dengan objek sembarang).

**Contoh Kasus:** Kita ingin membuat method untuk mencari nilai maksimum dalam array.

```java
// T harus mengimplementasikan Comparable, DAN yang dibandingkan harus T juga.
public static <T extends Comparable<T>> T findMax(T[] array) {
    if (array == null || array.length == 0) return null;
    
    T max = array[0];
    for (T item : array) {
        // compareTo adalah method milik interface Comparable
        if (item.compareTo(max) > 0) {
            max = item;
        }
    }
    return max;
}

// Penggunaan
// findMax(new String[]{"A", "Z", "B"}); // Valid, karena String implement Comparable<String>

```

*Jika kita hanya menulis `<T extends Comparable>`, kita berisiko membandingkan `T` dengan tipe lain yang tidak kompatibel.*

---

### 3. Multiple Bounds (Intersection Types)

Di Java, sebuah class hanya bisa extend 1 class lain, tapi bisa implement banyak interface. Generics mendukung aturan ini menggunakan tanda `&`.

**Sintaks:** `<T extends ClassA & InterfaceB & InterfaceC>`
*(Syarat: Class harus ditulis paling pertama, baru diikuti interface).*

**Contoh Implementasi:**
Bayangkan Anda punya fungsi yang harus menerima objek yang merupakan turunan `Number` (bisa dihitung) TAPI juga harus `Runnable` (bisa dijalankan di thread).

```java
public class Processor {
    // T harus turunan Number DAN implement Runnable
    public <T extends Number & Runnable> void process(T t) {
        // Bisa akses method Number
        System.out.println("Value: " + t.intValue());
        
        // Bisa akses method Runnable
        new Thread(t).start();
    }
}

```

---

### 4. Under The Hood: Bridge Methods

Ini adalah apa yang terjadi di level **Bytecode**. Karena *Type Erasure*, compiler terkadang harus membuat method "palsu" (synthetic) agar Polymorphism tetap jalan. Ini disebut **Bridge Method**.

**Kasus:**

```java
public class Node<T> {
    public void setData(T data) {
        System.out.println("Node.setData");
    }
}

public class MyNode extends Node<Integer> {
    // Kita meng-override method generic dengan tipe spesifik Integer
    @Override
    public void setData(Integer data) {
        System.out.println("MyNode.setData");
    }
}

```

**Masalahnya:**
Setelah *Erasure*, `Node` berubah menjadi:
`public void setData(Object data)`

Tapi `MyNode` punya:
`public void setData(Integer data)`

Secara teknis, tanda tangan method-nya beda (`Object` vs `Integer`). Jadi `MyNode` sebenarnya **tidak meng-override**, tapi melakukan **overloading**. Ini merusak Polymorphism!

**Solusi Compiler (Bridge Method):**
Compiler diam-diam membuat method tambahan di dalam `MyNode` yang terlihat seperti ini:

```java
// Method rahasia yang digenerate compiler di dalam class MyNode
public void setData(Object data) {
    // Melempar ke method kita yang asli
    setData((Integer) data); 
}

```

Method jembatan inilah yang dipanggil oleh JVM saat runtime ketika kita memperlakukan `MyNode` sebagai `Node`.

---

### 5. Heap Pollution (Polusi Memori)

Istilah ini terdengar menyeramkan. Ini terjadi ketika variabel tipe parameter menunjuk ke objek yang bukan tipe sebenarnya. Ini sering terjadi jika kita mencampur **Generic** dengan **Varargs** (variable arguments `...`).

**Contoh Bahaya:**

```java
public class Pollution {
    
    // Warning: Possible heap pollution from parameterized vararg type
    public static void dangerousMethod(List<String>... lists) {
        Object[] objects = lists; // Valid karena array covariant
        List<Integer> ints = new ArrayList<>();
        ints.add(42);
        
        objects[0] = ints; // Hancur sudah! Kita memasukkan List Integer ke slot List String
        
        // Saat baris ini dijalankan, akan terjadi ClassCastException
        // Karena compiler mengira ini String, padahal isinya Integer
        String s = lists[0].get(0); 
    }
}

```

**Solusi:**
Jika Anda yakin method Anda aman (tidak menulis ke array varargs), gunakan anotasi `@SafeVarargs` untuk menghilangkan warning compiler.

---

### 6. Runtime Generic Type Capture (Super Type Token)

Karena *Type Erasure*, kita tidak bisa melakukan `new T()`. Tapi, framework seperti **Jackson** (untuk JSON) atau **Spring Data** butuh tahu tipe aslinya saat runtime untuk deserialization. Bagaimana caranya?

Kita menggunakan trik: **Anonymous Subclass**.

Idenya: Walaupun tipe generic dari *instance* dihapus, metadata tipe generic dari *class definition* (superclass) tetap tersimpan di bytecode.

**Pola `TypeReference` (Sering dipakai di Spring/Jackson):**

```java
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// Class abstrak untuk menangkap tipe T
abstract class TypeReference<T> {
    Type type;

    public TypeReference() {
        // Trik Reflection untuk mengambil T
        Type superclass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    public Type getType() { return type; }
}

public class Main {
    public static void main(String[] args) {
        // Kita membuat anonymous subclass ({}) dari TypeReference
        TypeReference<String> refString = new TypeReference<String>() {};
        System.out.println(refString.getType()); // Output: class java.lang.String

        // Ini sangat berguna untuk List yang kompleks
        TypeReference<List<Integer>> refList = new TypeReference<List<Integer>>() {};
        System.out.println(refList.getType()); // Output: java.util.List<java.lang.Integer>
    }
}

```

*Inilah cara Spring `RestTemplate` atau `ObjectMapper` tahu cara mengubah JSON menjadi `List<User>`.*

---

### Rangkuman Visual Hirarki

Agar tidak lupa, ingat hirarki ini:

1. **Generic Class/Interface**: `Class<T>` (Wadah yang aman).
2. **Raw Type**: `Class` (Tanpa `<>`, peninggalan masa lalu, hindari!).
3. **Wildcard**: `<?>` (Fleksibilitas).
4. **Bound**: `extends` (Batas Atas/Producer), `super` (Batas Bawah/Consumer).

### Langkah Implementasi untuk Anda

Untuk memastikan Anda paham sampai ke tulang-tulangnya, cobalah buat **Generic Result Wrapper** untuk Spring Boot response yang menangani sukses dan error, dengan fitur *Method Chaining*.

Kodenya harus bisa dipanggil seperti ini:

```java
Result<User> res = Result.success(new User("Andi"))
                         .or(new User("Default"));

```

Apakah Anda ingin saya berikan tantangan coding lengkap untuk membuat "Custom Generic Collection" sederhana agar materinya menempel?