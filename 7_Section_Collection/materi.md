main materi: https://docs.google.com/presentation/d/1IybRjjqE2hIrrq2aLbZ1qw_7o6vhgg27dVZVc4gQF9g/edit?slide=id.p#slide=id.p


# Panduan Lengkap Java Collections Framework

Saya akan mengajarkan Collection di Java secara mendalam dan menyeluruh. Mari kita mulai dari konsep dasar hingga implementasi lanjutan.

## 1. Pengantar Collections Framework

### 1.1 Apa itu Collections?
Collections adalah framework yang menyediakan arsitektur untuk menyimpan dan memanipulasi sekelompok objek. Sebelum Collections Framework, Java hanya memiliki Array, Vector, Hashtable, dan Stack yang terbatas dan tidak konsisten.

### 1.2 Mengapa Perlu Collections?
- **Konsistensi**: API yang seragam untuk berbagai struktur data
- **Performa**: Implementasi yang sudah dioptimasi
- **Interoperabilitas**: Mudah berbagi data antar komponen
- **Fleksibilitas**: Banyak pilihan struktur data sesuai kebutuhan

### 1.3 Hierarki Collections Framework

```
                    Iterable
                       ↓
                   Collection
                       ↓
        ┌──────────────┼──────────────┐
        ↓              ↓              ↓
      List            Set           Queue
        ↓              ↓              ↓
   - ArrayList    - HashSet      - PriorityQueue
   - LinkedList   - LinkedHashSet - LinkedList
   - Vector       - TreeSet      - ArrayDeque
   - Stack
   
   Map (bukan turunan Collection)
     ↓
   - HashMap
   - LinkedHashMap
   - TreeMap
   - Hashtable
```

## 2. Interface Collection (Induk Semua Collection)

### 2.1 Method-method Dasar Collection

**Method Utama:**
- `add(E e)` - Menambah elemen
- `remove(Object o)` - Menghapus elemen
- `contains(Object o)` - Cek keberadaan elemen
- `size()` - Ukuran collection
- `isEmpty()` - Cek apakah kosong
- `clear()` - Hapus semua elemen
- `iterator()` - Mendapat iterator
- `toArray()` - Konversi ke array

### 2.2 Contoh Dasar

```java
import java.util.*;

public class CollectionBasic {
    public static void main(String[] args) {
        // Menggunakan ArrayList sebagai implementasi Collection
        Collection<String> collection = new ArrayList<>();
        
        // Menambah elemen
        collection.add("Apel");
        collection.add("Jeruk");
        collection.add("Mangga");
        
        System.out.println("Ukuran: " + collection.size()); // 3
        System.out.println("Berisi Apel? " + collection.contains("Apel")); // true
        
        // Iterasi
        for (String buah : collection) {
            System.out.println(buah);
        }
        
        collection.remove("Jeruk");
        System.out.println("Setelah hapus: " + collection); // [Apel, Mangga]
    }
}
```

## 3. Interface List (Koleksi Berurutan)

### 3.1 Karakteristik List
- **Ordered**: Mempertahankan urutan insersi
- **Indexed**: Akses elemen via indeks (0, 1, 2, ...)
- **Allows Duplicates**: Boleh ada elemen duplikat
- **Null Elements**: Boleh menyimpan null

### 3.2 Method Tambahan List
- `get(int index)` - Ambil elemen di indeks
- `set(int index, E element)` - Ganti elemen di indeks
- `add(int index, E element)` - Sisipkan di indeks
- `remove(int index)` - Hapus di indeks
- `indexOf(Object o)` - Cari indeks pertama
- `lastIndexOf(Object o)` - Cari indeks terakhir
- `subList(int from, int to)` - Ambil sub-list

## 3.3 ArrayList

### Teori ArrayList
- **Implementasi**: Array dinamis yang bisa bertambah ukurannya
- **Kapasitas Default**: 10 elemen
- **Pertumbuhan**: Ketika penuh, kapasitas bertambah 50% (capacity * 1.5)
- **Time Complexity**:
    - Get/Set: O(1) - sangat cepat
    - Add (di akhir): O(1) amortized
    - Add (di tengah): O(n) - harus shift elemen
    - Remove: O(n) - harus shift elemen
    - Contains: O(n) - harus scan

**Kapan Menggunakan ArrayList:**
- Akses random/acak sering dilakukan
- Jarang insert/delete di tengah
- Ukuran tidak terlalu sering berubah

```java
import java.util.*;

public class ArrayListExample {
    public static void main(String[] args) {
        // Membuat ArrayList
        ArrayList<Integer> numbers = new ArrayList<>();
        
        // Menambah elemen
        numbers.add(10);
        numbers.add(20);
        numbers.add(30);
        numbers.add(20); // duplikat diperbolehkan
        
        System.out.println("List: " + numbers); // [10, 20, 30, 20]
        
        // Akses by index
        System.out.println("Index 1: " + numbers.get(1)); // 20
        
        // Update elemen
        numbers.set(2, 35);
        System.out.println("Setelah update: " + numbers); // [10, 20, 35, 20]
        
        // Insert di tengah
        numbers.add(1, 15);
        System.out.println("Setelah insert: " + numbers); // [10, 15, 20, 35, 20]
        
        // Cari index
        System.out.println("Index of 20: " + numbers.indexOf(20)); // 2
        System.out.println("Last index of 20: " + numbers.lastIndexOf(20)); // 4
        
        // Remove
        numbers.remove(2); // hapus index 2
        numbers.remove(Integer.valueOf(35)); // hapus value 35
        System.out.println("Setelah remove: " + numbers);
        
        // Iterasi berbagai cara
        // 1. For-each
        for (Integer num : numbers) {
            System.out.print(num + " ");
        }
        
        // 2. For biasa
        for (int i = 0; i < numbers.size(); i++) {
            System.out.print(numbers.get(i) + " ");
        }
        
        // 3. Iterator
        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        
        // 4. Lambda (Java 8+)
        numbers.forEach(num -> System.out.print(num + " "));
    }
}
```

### 3.4 LinkedList

### Teori LinkedList
- **Implementasi**: Doubly-linked list (node dengan pointer ke next dan previous)
- **Struktur**: Setiap node menyimpan data dan referensi ke node lain
- **Time Complexity**:
    - Get/Set: O(n) - harus traverse dari awal/akhir
    - Add (di awal/akhir): O(1) - sangat cepat
    - Add (di tengah): O(n) - harus traverse dulu
    - Remove (di awal/akhir): O(1)
    - Remove (di tengah): O(n)

**Kapan Menggunakan LinkedList:**
- Sering insert/delete di awal atau akhir
- Implementasi Queue atau Deque
- Tidak perlu akses random
- Ukuran sering berubah

```java
import java.util.*;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();
        
        // Operasi List biasa
        list.add("B");
        list.add("C");
        list.addFirst("A"); // khusus LinkedList
        list.addLast("D");  // khusus LinkedList
        
        System.out.println(list); // [A, B, C, D]
        
        // Method khusus LinkedList untuk Queue/Deque
        list.offerFirst("START");
        list.offerLast("END");
        System.out.println(list); // [START, A, B, C, D, END]
        
        // Peek (lihat tanpa hapus)
        System.out.println("First: " + list.peekFirst()); // START
        System.out.println("Last: " + list.peekLast());   // END
        
        // Poll (ambil dan hapus)
        System.out.println("Poll first: " + list.pollFirst()); // START
        System.out.println("Poll last: " + list.pollLast());   // END
        System.out.println(list); // [A, B, C, D]
        
        // Sebagai Stack (LIFO)
        LinkedList<String> stack = new LinkedList<>();
        stack.push("1");
        stack.push("2");
        stack.push("3");
        System.out.println("Pop: " + stack.pop()); // 3 (LIFO)
        
        // Sebagai Queue (FIFO)
        LinkedList<String> queue = new LinkedList<>();
        queue.offer("First");
        queue.offer("Second");
        queue.offer("Third");
        System.out.println("Poll: " + queue.poll()); // First (FIFO)
    }
}
```

### 3.5 Vector dan Stack

### Teori Vector
- **Mirip ArrayList** tapi synchronized (thread-safe)
- **Pertumbuhan**: Kapasitas double (2x) ketika penuh
- **Legacy Class**: Sudah jarang digunakan, lebih baik ArrayList + Collections.synchronizedList()
- **Performa**: Lebih lambat karena overhead synchronization

### Teori Stack
- **Extends Vector**: Juga synchronized
- **LIFO**: Last In First Out
- **Method Khusus**: push(), pop(), peek(), empty(), search()
- **Modern Alternative**: Deque (ArrayDeque atau LinkedList)

```java
import java.util.*;

public class VectorStackExample {
    public static void main(String[] args) {
        // Vector (jarang digunakan sekarang)
        Vector<Integer> vector = new Vector<>();
        vector.add(1);
        vector.add(2);
        System.out.println("Capacity: " + vector.capacity()); // 10 default
        
        // Stack
        Stack<String> stack = new Stack<>();
        stack.push("Pertama");
        stack.push("Kedua");
        stack.push("Ketiga");
        
        System.out.println("Peek: " + stack.peek());     // Ketiga (tidak dihapus)
        System.out.println("Pop: " + stack.pop());       // Ketiga (dihapus)
        System.out.println("Pop: " + stack.pop());       // Kedua
        System.out.println("Empty? " + stack.empty());   // false
        System.out.println("Search 'Pertama': " + stack.search("Pertama")); // 1
        
        // Alternatif modern menggunakan Deque
        Deque<String> modernStack = new ArrayDeque<>();
        modernStack.push("A");
        modernStack.push("B");
        System.out.println("Modern pop: " + modernStack.pop()); // B
    }
}
```

## 4. Interface Set (Koleksi Unik)

### 4.1 Karakteristik Set
- **No Duplicates**: Tidak boleh ada elemen duplikat
- **Unordered** (kecuali LinkedHashSet dan TreeSet)
- **Null Elements**: Tergantung implementasi
- **No Index**: Tidak ada akses berdasarkan posisi

### 4.2 HashSet

### Teori HashSet
- **Implementasi**: Hash table (HashMap internal)
- **Unordered**: Urutan tidak terjamin
- **Null**: Boleh satu null
- **Time Complexity**:
    - Add: O(1) average
    - Remove: O(1) average
    - Contains: O(1) average
- **Hash Collision**: Menggunakan chaining atau open addressing

**Kapan Menggunakan HashSet:**
- Butuh uniqueness
- Tidak peduli urutan
- Butuh performa cepat untuk add/remove/contains

```java
import java.util.*;

public class HashSetExample {
    public static void main(String[] args) {
        HashSet<String> set = new HashSet<>();
        
        // Menambah elemen
        set.add("Apel");
        set.add("Jeruk");
        set.add("Mangga");
        set.add("Apel"); // duplikat, tidak ditambahkan
        
        System.out.println(set); // [Mangga, Apel, Jeruk] - urutan acak
        System.out.println("Size: " + set.size()); // 3, bukan 4
        
        // Cek keberadaan - sangat cepat O(1)
        System.out.println("Ada Apel? " + set.contains("Apel")); // true
        
        // Remove
        set.remove("Jeruk");
        System.out.println(set);
        
        // Iterasi
        for (String buah : set) {
            System.out.println(buah);
        }
        
        // Operasi Set
        HashSet<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        HashSet<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
        
        // Union (gabungan)
        HashSet<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        System.out.println("Union: " + union); // [1,2,3,4,5,6,7,8]
        
        // Intersection (irisan)
        HashSet<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        System.out.println("Intersection: " + intersection); // [4,5]
        
        // Difference (selisih)
        HashSet<Integer> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        System.out.println("Difference: " + difference); // [1,2,3]
    }
}
```

### 4.3 LinkedHashSet

### Teori LinkedHashSet
- **Extends HashSet**: Menambah linked list untuk ordering
- **Insertion Order**: Mempertahankan urutan insersi
- **Performa**: Sedikit lebih lambat dari HashSet
- **Memory**: Lebih boros karena linked list

**Kapan Menggunakan LinkedHashSet:**
- Butuh uniqueness DAN urutan insersi
- Iterasi prediktabel

```java
import java.util.*;

public class LinkedHashSetExample {
    public static void main(String[] args) {
        // HashSet - urutan tidak terjamin
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("Zebra");
        hashSet.add("Apel");
        hashSet.add("Mangga");
        hashSet.add("Jeruk");
        System.out.println("HashSet: " + hashSet); // urutan acak
        
        // LinkedHashSet - urutan sesuai insersi
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("Zebra");
        linkedHashSet.add("Apel");
        linkedHashSet.add("Mangga");
        linkedHashSet.add("Jeruk");
        System.out.println("LinkedHashSet: " + linkedHashSet); 
        // [Zebra, Apel, Mangga, Jeruk] - urutan sesuai input
        
        // Tetap tidak boleh duplikat
        linkedHashSet.add("Apel"); // tidak ditambahkan
        System.out.println("Size: " + linkedHashSet.size()); // tetap 4
    }
}
```

### 4.4 TreeSet

### Teori TreeSet
- **Implementasi**: Red-Black Tree (self-balancing BST)
- **Sorted Order**: Elemen terurut natural atau via Comparator
- **Null**: TIDAK boleh null (NullPointerException)
- **Time Complexity**:
    - Add: O(log n)
    - Remove: O(log n)
    - Contains: O(log n)
- **Navigable**: Method untuk navigasi (first, last, ceiling, floor, dll)

**Kapan Menggunakan TreeSet:**
- Butuh elemen terurut
- Perlu navigasi (range query)
- Tidak masalah sedikit lebih lambat

```java
import java.util.*;

public class TreeSetExample {
    public static void main(String[] args) {
        // Natural ordering (ascending)
        TreeSet<Integer> treeSet = new TreeSet<>();
        treeSet.add(50);
        treeSet.add(20);
        treeSet.add(80);
        treeSet.add(10);
        treeSet.add(40);
        
        System.out.println(treeSet); // [10, 20, 40, 50, 80] - sorted!
        
        // Method navigasi
        System.out.println("First: " + treeSet.first());      // 10
        System.out.println("Last: " + treeSet.last());        // 80
        System.out.println("Higher than 40: " + treeSet.higher(40));   // 50
        System.out.println("Lower than 40: " + treeSet.lower(40));     // 20
        System.out.println("Ceiling 45: " + treeSet.ceiling(45));      // 50
        System.out.println("Floor 45: " + treeSet.floor(45));          // 40
        
        // SubSet (range)
        SortedSet<Integer> subSet = treeSet.subSet(20, 60);
        System.out.println("SubSet [20, 60): " + subSet); // [20, 40, 50]
        
        // HeadSet dan TailSet
        System.out.println("Head (<50): " + treeSet.headSet(50)); // [10, 20, 40]
        System.out.println("Tail (>=50): " + treeSet.tailSet(50)); // [50, 80]
        
        // Descending order
        NavigableSet<Integer> descendingSet = treeSet.descendingSet();
        System.out.println("Descending: " + descendingSet); // [80, 50, 40, 20, 10]
        
        // Custom Comparator (case-insensitive String)
        TreeSet<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.add("alice");
        names.add("Bob");
        names.add("CHARLIE");
        System.out.println(names); // [alice, Bob, CHARLIE]
        
        // Custom object dengan Comparable
        TreeSet<Person> people = new TreeSet<>();
        people.add(new Person("Alice", 25));
        people.add(new Person("Bob", 30));
        people.add(new Person("Charlie", 20));
        System.out.println(people); // sorted by age
    }
}

class Person implements Comparable<Person> {
    String name;
    int age;
    
    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public int compareTo(Person other) {
        return this.age - other.age; // sort by age
    }
    
    @Override
    public String toString() {
        return name + "(" + age + ")";
    }
}
```

## 5. Interface Queue (Antrian)

### 5.1 Karakteristik Queue
- **FIFO**: First In First Out (default)
- **Head & Tail**: Operasi di ujung-ujung
- **Two Set Methods**:
    - Throw exception: add(), remove(), element()
    - Return special value: offer(), poll(), peek()

### 5.2 PriorityQueue

### Teori PriorityQueue
- **Implementasi**: Binary heap (complete binary tree)
- **Priority**: Elemen keluar berdasarkan priority, bukan FIFO
- **Natural Order**: Default atau via Comparator
- **Not Thread-Safe**: Untuk multi-thread gunakan PriorityBlockingQueue
- **Null**: TIDAK boleh null

**Kapan Menggunakan PriorityQueue:**
- Butuh elemen dengan prioritas tertinggi/terendah
- Implementasi Dijkstra, A*, scheduling

```java
import java.util.*;

public class PriorityQueueExample {
    public static void main(String[] args) {
        // Default: min-heap (smallest first)
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.offer(50);
        minHeap.offer(20);
        minHeap.offer(80);
        minHeap.offer(10);
        
        System.out.println("Min Heap:");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " "); // 10 20 50 80
        }
        System.out.println();
        
        // Max-heap (largest first) dengan Comparator
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        maxHeap.offer(50);
        maxHeap.offer(20);
        maxHeap.offer(80);
        maxHeap.offer(10);
        
        System.out.println("Max Heap:");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " "); // 80 50 20 10
        }
        System.out.println();
        
        // Custom object - task scheduling
        PriorityQueue<Task> taskQueue = new PriorityQueue<>();
        taskQueue.offer(new Task("Low priority task", 3));
        taskQueue.offer(new Task("High priority task", 1));
        taskQueue.offer(new Task("Medium priority task", 2));
        
        System.out.println("Task execution order:");
        while (!taskQueue.isEmpty()) {
            System.out.println(taskQueue.poll());
        }
    }
}

class Task implements Comparable<Task> {
    String name;
    int priority;
    
    Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }
    
    @Override
    public int compareTo(Task other) {
        return this.priority - other.priority; // lower number = higher priority
    }
    
    @Override
    public String toString() {
        return name + " (Priority: " + priority + ")";
    }
}
```

### 5.3 ArrayDeque

### Teori ArrayDeque
- **Deque**: Double-Ended Queue (operasi di kedua ujung)
- **Implementasi**: Resizable array
- **No Capacity Limit**: Auto-grow
- **Faster than LinkedList**: Untuk Queue dan Stack
- **Not Thread-Safe**: Untuk multi-thread gunakan LinkedBlockingDeque

**Kapan Menggunakan ArrayDeque:**
- Butuh Queue atau Stack (lebih cepat dari LinkedList/Stack)
- Operasi di kedua ujung

```java
import java.util.*;

public class ArrayDequeExample {
    public static void main(String[] args) {
        ArrayDeque<String> deque = new ArrayDeque<>();
        
        // Operasi di head (depan)
        deque.addFirst("B");
        deque.offerFirst("A");
        
        // Operasi di tail (belakang)
        deque.addLast("C");
        deque.offerLast("D");
        
        System.out.println(deque); // [A, B, C, D]
        
        // Peek (lihat tanpa hapus)
        System.out.println("First: " + deque.peekFirst()); // A
        System.out.println("Last: " + deque.peekLast());   // D
        
        // Poll (ambil dan hapus)
        System.out.println("Poll first: " + deque.pollFirst()); // A
        System.out.println("Poll last: " + deque.pollLast());   // D
        System.out.println(deque); // [B, C]
        
        // Sebagai Stack (LIFO)
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println("Stack pop: " + stack.pop()); // 3
        System.out.println("Stack peek: " + stack.peek()); // 2
        
        // Sebagai Queue (FIFO)
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.offer("First");
        queue.offer("Second");
        queue.offer("Third");
        System.out.println("Queue poll: " + queue.poll()); // First
        System.out.println("Queue peek: " + queue.peek()); // Second
    }
}
```

## 6. Interface Map (Key-Value Pairs)

### 6.1 Karakteristik Map
- **BUKAN turunan Collection**: Interface terpisah
- **Key-Value Pairs**: Setiap entry punya key dan value
- **Unique Keys**: Key tidak boleh duplikat, value boleh
- **Null**: Tergantung implementasi

### 6.2 Method Dasar Map
- `put(K key, V value)` - Tambah/update entry
- `get(Object key)` - Ambil value by key
- `remove(Object key)` - Hapus entry
- `containsKey(Object key)` - Cek key ada
- `containsValue(Object value)` - Cek value ada
- `keySet()` - Dapat semua keys
- `values()` - Dapat semua values
- `entrySet()` - Dapat semua entries

### 6.3 HashMap

### Teori HashMap
- **Implementasi**: Hash table dengan buckets
- **Unordered**: Tidak ada jaminan urutan
- **Null**: Boleh satu null key dan banyak null values
- **Time Complexity**:
    - Put: O(1) average, O(n) worst case
    - Get: O(1) average, O(n) worst case
    - Remove: O(1) average
- **Load Factor**: Default 0.75 (resize ketika 75% penuh)
- **Initial Capacity**: Default 16

**Kapan Menggunakan HashMap:**
- Butuh lookup cepat by key
- Tidak peduli urutan
- Single-threaded

```java
import java.util.*;

public class HashMapExample {
    public static void main(String[] args) {
        HashMap<String, Integer> scores = new HashMap<>();
        
        // Menambah data
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        scores.put("Alice", 98); // update nilai Alice
        
        System.out.println(scores); // {Bob=87, Alice=98, Charlie=92}
        
        // Mengambil data
        System.out.println("Alice score: " + scores.get("Alice")); // 98
        System.out.println("David score: " + scores.get("David")); // null
        
        // getOrDefault
        System.out.println("David score: " + scores.getOrDefault("David", 0)); // 0
        
        // Cek keberadaan
        System.out.println("Has Bob? " + scores.containsKey("Bob")); // true
        System.out.println("Has score 87? " + scores.containsValue(87)); // true
        
        // Remove
        scores.remove("Bob");
        System.out.println(scores);
        
        // Iterasi - cara 1: keySet
        System.out.println("\nIterasi keySet:");
        for (String name : scores.keySet()) {
            System.out.println(name + ": " + scores.get(name));
        }
        
        // Iterasi - cara 2: values
        System.out.println("\nIterasi values:");
        for (Integer score : scores.values()) {
            System.out.println(score);
        }
        
        // Iterasi - cara 3: entrySet (paling efisien)
        System.out.println("\nIterasi entrySet:");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // Java 8+ forEach
        scores.forEach((name, score) -> System.out.println(name + ": " + score));
        
        // Operasi berguna lainnya
        scores.putIfAbsent("David", 85); // hanya put jika key belum ada
        scores.replace("Alice", 98, 100); // ganti jika value match
        scores.computeIfAbsent("Eve", k -> 90); // compute jika absent
        scores.merge("Alice", 5, Integer::sum); // merge dengan function
        
        System.out.println(scores);
    }
}
```

### 6.4 LinkedHashMap

### Teori LinkedHashMap
- **Extends HashMap**: Menambah doubly-linked list
- **Insertion Order**: Mempertahankan urutan insersi
- **Access Order**: Bisa diset untuk urutan akses (LRU cache)
- **Performa**: Sedikit lebih lambat dari HashMap

**Kapan Menggunakan LinkedHashMap:**
- Butuh Map dengan urutan prediktabel
- Implementasi LRU cache

```java
import java.util.*;

public class LinkedHashMapExample {
    public static void main(String[] args) {
        // HashMap - urutan tidak terjamin
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Z", 1);
        hashMap.put("A", 2);
        hashMap.put("M", 3);
        System.out.println("HashMap: " + hashMap); // urutan acak
        
        // LinkedHashMap - insertion order
        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("Z", 1);
        linkedHashMap.put("A", 2);
        linkedHashMap.put("M", 3);
        System.out.println("LinkedHashMap: " + linkedHashMap); // {Z=1, A=2, M=3}
        
        // Access order (untuk LRU cache)
        LinkedHashMap<String, Integer> lruMap = new LinkedHashMap<>(16, 0.75f, true);
        lruMap.put("A", 1);
        lruMap.put("B", 2);
        lruMap.put("C", 3);
        System.out.println("Before access: " + lruMap); // {A=1, B=2, C=3}
        
        lruMap.get("A"); // akses A
        System.out.println("After access A: " + lruMap); // {B=2, C=3, A=