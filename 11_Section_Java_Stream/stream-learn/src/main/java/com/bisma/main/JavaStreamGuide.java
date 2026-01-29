package com.bisma.main;/*
 * ============================================================================
 * PANDUAN LENGKAP JAVA STREAM - Dari Dasar hingga Advanced
 * ============================================================================
 * Panduan ini mencakup semua yang perlu kamu ketahui tentang Java Stream
 */

import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class JavaStreamGuide {

    public static void main(String[] args) {
        System.out.println("=== JAVA STREAM COMPLETE GUIDE ===\n");
        
        // Panggil semua metode pembelajaran
        part1_IntroductionToStreams();
        part2_CreatingStreams();
        part3_IntermediateOperations();
        part4_TerminalOperations();
        part5_CollectorsInDepth();
        part6_AdvancedOperations();
        part7_ParallelStreams();
        part8_PrimitiveStreams();
        part9_RealWorldExamples();
        part10_BestPractices();
    }

    // ========================================================================
    // PART 1: PENGENALAN STREAM
    // ========================================================================
    public static void part1_IntroductionToStreams() {
        System.out.println("\n--- PART 1: PENGENALAN STREAM ---");
        
        /*
         * APA ITU STREAM?
         * - Stream adalah urutan elemen yang mendukung operasi aggregate
         * - BUKAN struktur data (tidak menyimpan data)
         * - TIDAK mengubah sumber data asli
         * - Lazy evaluation: operasi hanya dijalankan saat diperlukan
         * 
         * STRUKTUR STREAM:
         * Source → Intermediate Operations → Terminal Operation
         */
        
        List<String> names = Arrays.asList("Ali", "Budi", "Citra", "Dewi");
        
        // Tanpa Stream (Imperative)
        System.out.println("Tanpa Stream:");
        List<String> result1 = new ArrayList<>();
        for (String name : names) {
            if (name.length() > 3) {
                result1.add(name.toUpperCase());
            }
        }
        System.out.println(result1);
        
        // Dengan Stream (Declarative)
        System.out.println("\nDengan Stream:");
        List<String> result2 = names.stream()
            .filter(name -> name.length() > 3)
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println(result2);
        
        /*
         * KEUNTUNGAN STREAM:
         * 1. Kode lebih ringkas dan mudah dibaca
         * 2. Mendukung operasi paralel dengan mudah
         * 3. Lazy evaluation untuk efisiensi
         * 4. Functional programming style
         */
    }

    // ========================================================================
    // PART 2: MEMBUAT STREAM
    // ========================================================================
    public static void part2_CreatingStreams() {
        System.out.println("\n--- PART 2: MEMBUAT STREAM ---");
        
        // 1. Dari Collection
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Stream<Integer> stream1 = list.stream();
        System.out.println("Dari List: " + stream1.count());
        
        // 2. Dari Array
        String[] array = {"a", "b", "c"};
        Stream<String> stream2 = Arrays.stream(array);
        System.out.println("Dari Array: " + stream2.count());
        
        // 3. Menggunakan Stream.of()
        Stream<String> stream3 = Stream.of("x", "y", "z");
        System.out.println("Stream.of: " + stream3.count());
        
        // 4. Dari file
        try {
            Stream<String> lines = java.nio.file.Files.lines(
                java.nio.file.Paths.get("file.txt")
            );
            // lines.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("File tidak ditemukan (contoh saja)");
        }
        
        // 5. Stream kosong
        Stream<String> emptyStream = Stream.empty();
        System.out.println("Empty stream: " + emptyStream.count());
        
        // 6. Stream infinite dengan iterate
        Stream<Integer> infiniteStream1 = Stream.iterate(0, n -> n + 2);
        System.out.println("Iterate (10 pertama): " + 
            infiniteStream1.limit(10).collect(Collectors.toList()));
        
        // 7. Stream infinite dengan generate
        Stream<Double> infiniteStream2 = Stream.generate(Math::random);
        System.out.println("Generate (5 pertama): " + 
            infiniteStream2.limit(5).collect(Collectors.toList()));
        
        // 8. IntStream, LongStream, DoubleStream
        IntStream intStream = IntStream.range(1, 6); // 1 sampai 5
        System.out.println("IntStream.range: " + 
            intStream.boxed().collect(Collectors.toList()));
        
        IntStream intStream2 = IntStream.rangeClosed(1, 5); // 1 sampai 5 (inklusif)
        System.out.println("IntStream.rangeClosed: " + 
            intStream2.boxed().collect(Collectors.toList()));
        
        // 9. Stream Builder
        Stream.Builder<String> builder = Stream.builder();
        builder.add("A").add("B").add("C");
        Stream<String> builtStream = builder.build();
        System.out.println("Stream Builder: " + 
            builtStream.collect(Collectors.toList()));
    }

    // ========================================================================
    // PART 3: INTERMEDIATE OPERATIONS (Lazy Operations)
    // ========================================================================
    public static void part3_IntermediateOperations() {
        System.out.println("\n--- PART 3: INTERMEDIATE OPERATIONS ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. filter() - Menyaring elemen
        System.out.println("\n1. filter()");
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evenNumbers);
        
        // Multiple filters
        List<Integer> filtered = numbers.stream()
            .filter(n -> n > 3)
            .filter(n -> n < 8)
            .collect(Collectors.toList());
        System.out.println("Between 3 and 8: " + filtered);
        
        // 2. map() - Transformasi elemen
        System.out.println("\n2. map()");
        List<Integer> squared = numbers.stream()
            .map(n -> n * n)
            .collect(Collectors.toList());
        System.out.println("Squared: " + squared);
        
        List<String> names = Arrays.asList("ali", "budi", "citra");
        List<String> uppercase = names.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("Uppercase: " + uppercase);
        
        // 3. flatMap() - Flatten nested structures
        System.out.println("\n3. flatMap()");
        List<List<Integer>> nested = Arrays.asList(
            Arrays.asList(1, 2),
            Arrays.asList(3, 4),
            Arrays.asList(5, 6)
        );
        
        List<Integer> flattened = nested.stream()
            .flatMap(list -> list.stream())
            .collect(Collectors.toList());
        System.out.println("Flattened: " + flattened);
        
        // Contoh praktis: split kata
        List<String> sentences = Arrays.asList("Hello World", "Java Stream");
        List<String> words = sentences.stream()
            .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
            .collect(Collectors.toList());
        System.out.println("Words: " + words);
        
        // 4. distinct() - Menghilangkan duplikat
        System.out.println("\n4. distinct()");
        List<Integer> withDuplicates = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5);
        List<Integer> unique = withDuplicates.stream()
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Unique: " + unique);
        
        // 5. sorted() - Mengurutkan
        System.out.println("\n5. sorted()");
        List<Integer> unsorted = Arrays.asList(5, 2, 8, 1, 9);
        
        // Natural order
        List<Integer> sorted = unsorted.stream()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Sorted: " + sorted);
        
        // Reverse order
        List<Integer> reverseSorted = unsorted.stream()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        System.out.println("Reverse sorted: " + reverseSorted);
        
        // Custom comparator
        List<String> nameList = Arrays.asList("Alice", "Bob", "Charlie", "Dave");
        List<String> sortedByLength = nameList.stream()
            .sorted(Comparator.comparing(String::length))
            .collect(Collectors.toList());
        System.out.println("Sorted by length: " + sortedByLength);
        
        // 6. peek() - Debugging/side effects
        System.out.println("\n6. peek()");
        List<Integer> peeked = numbers.stream()
            .filter(n -> n > 5)
            .peek(n -> System.out.println("Filtered: " + n))
            .map(n -> n * 2)
            .peek(n -> System.out.println("Mapped: " + n))
            .collect(Collectors.toList());
        
        // 7. limit() - Membatasi jumlah elemen
        System.out.println("\n7. limit()");
        List<Integer> limited = numbers.stream()
            .limit(5)
            .collect(Collectors.toList());
        System.out.println("Limited to 5: " + limited);
        
        // 8. skip() - Melewati n elemen pertama
        System.out.println("\n8. skip()");
        List<Integer> skipped = numbers.stream()
            .skip(5)
            .collect(Collectors.toList());
        System.out.println("Skip first 5: " + skipped);
        
        // Kombinasi skip dan limit (pagination)
        System.out.println("Page 2 (size 3): " + 
            numbers.stream().skip(3).limit(3).collect(Collectors.toList()));
        
        // 9. takeWhile() - Java 9+ (ambil sampai kondisi false)
        System.out.println("\n9. takeWhile()");
        List<Integer> takeWhileResult = numbers.stream()
            .takeWhile(n -> n < 6)
            .collect(Collectors.toList());
        System.out.println("Take while < 6: " + takeWhileResult);
        
        // 10. dropWhile() - Java 9+ (buang sampai kondisi false)
        System.out.println("\n10. dropWhile()");
        List<Integer> dropWhileResult = numbers.stream()
            .dropWhile(n -> n < 6)
            .collect(Collectors.toList());
        System.out.println("Drop while < 6: " + dropWhileResult);
    }

    // ========================================================================
    // PART 4: TERMINAL OPERATIONS (Eager Operations)
    // ========================================================================
    public static void part4_TerminalOperations() {
        System.out.println("\n--- PART 4: TERMINAL OPERATIONS ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. forEach() - Iterasi
        System.out.println("\n1. forEach()");
        System.out.print("Numbers: ");
        numbers.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 2. forEachOrdered() - Iterasi dengan urutan (penting untuk parallel)
        System.out.println("\n2. forEachOrdered()");
        System.out.print("Ordered: ");
        numbers.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();
        
        // 3. toArray() - Convert ke array
        System.out.println("\n3. toArray()");
        Object[] array1 = numbers.stream().toArray();
        Integer[] array2 = numbers.stream().toArray(Integer[]::new);
        System.out.println("Array: " + Arrays.toString(array2));
        
        // 4. reduce() - Reduksi ke single value
        System.out.println("\n4. reduce()");
        
        // Sum dengan reduce
        Optional<Integer> sum = numbers.stream()
            .reduce((a, b) -> a + b);
        System.out.println("Sum: " + sum.get());
        
        // Dengan identity value
        Integer sum2 = numbers.stream()
            .reduce(0, (a, b) -> a + b);
        System.out.println("Sum with identity: " + sum2);
        
        // Product
        Integer product = numbers.stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("Product: " + product);
        
        // Max dengan reduce
        Optional<Integer> max = numbers.stream()
            .reduce(Integer::max);
        System.out.println("Max: " + max.get());
        
        // 5. collect() - Kumpulkan ke collection
        System.out.println("\n5. collect()");
        List<Integer> list = numbers.stream().collect(Collectors.toList());
        Set<Integer> set = numbers.stream().collect(Collectors.toSet());
        System.out.println("Collected to List: " + list);
        System.out.println("Collected to Set: " + set);
        
        // 6. min() dan max()
        System.out.println("\n6. min() dan max()");
        Optional<Integer> min = numbers.stream().min(Integer::compare);
        Optional<Integer> maximum = numbers.stream().max(Integer::compare);
        System.out.println("Min: " + min.get());
        System.out.println("Max: " + maximum.get());
        
        // 7. count()
        System.out.println("\n7. count()");
        long count = numbers.stream().filter(n -> n > 5).count();
        System.out.println("Count > 5: " + count);
        
        // 8. anyMatch(), allMatch(), noneMatch()
        System.out.println("\n8. Match operations");
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("Any even: " + anyEven);
        System.out.println("All positive: " + allPositive);
        System.out.println("None negative: " + noneNegative);
        
        // 9. findFirst() dan findAny()
        System.out.println("\n9. findFirst() dan findAny()");
        Optional<Integer> first = numbers.stream()
            .filter(n -> n > 5)
            .findFirst();
        System.out.println("First > 5: " + first.get());
        
        Optional<Integer> any = numbers.parallelStream()
            .filter(n -> n > 5)
            .findAny();
        System.out.println("Any > 5: " + any.get());
    }

    // ========================================================================
    // PART 5: COLLECTORS IN DEPTH
    // ========================================================================
    public static void part5_CollectorsInDepth() {
        System.out.println("\n--- PART 5: COLLECTORS IN DEPTH ---");
        
        List<Person> people = Arrays.asList(
            new Person("Ali", 25, "Jakarta", 5000000),
            new Person("Budi", 30, "Bandung", 7000000),
            new Person("Citra", 25, "Jakarta", 6000000),
            new Person("Dewi", 35, "Surabaya", 8000000),
            new Person("Eko", 30, "Jakarta", 7500000)
        );
        
        // 1. toList(), toSet()
        System.out.println("\n1. toList(), toSet()");
        List<String> names = people.stream()
            .map(Person::getName)
            .collect(Collectors.toList());
        System.out.println("Names: " + names);
        
        Set<Integer> ages = people.stream()
            .map(Person::getAge)
            .collect(Collectors.toSet());
        System.out.println("Unique ages: " + ages);
        
        // 2. toCollection() - ke collection spesifik
        System.out.println("\n2. toCollection()");
        LinkedList<String> linkedList = people.stream()
            .map(Person::getName)
            .collect(Collectors.toCollection(LinkedList::new));
        System.out.println("LinkedList: " + linkedList);
        
        // 3. toMap() - Convert ke Map
        System.out.println("\n3. toMap()");
        Map<String, Integer> nameToAge = people.stream()
            .collect(Collectors.toMap(
                Person::getName,
                Person::getAge
            ));
        System.out.println("Name to Age map: " + nameToAge);
        
        // Handle duplicate keys
        Map<Integer, String> ageToName = people.stream()
            .collect(Collectors.toMap(
                Person::getAge,
                Person::getName,
                (existing, replacement) -> existing // keep first
            ));
        System.out.println("Age to Name map: " + ageToName);
        
        // 4. joining() - Gabung string
        System.out.println("\n4. joining()");
        String allNames = people.stream()
            .map(Person::getName)
            .collect(Collectors.joining());
        System.out.println("Joined: " + allNames);
        
        String namesWithComma = people.stream()
            .map(Person::getName)
            .collect(Collectors.joining(", "));
        System.out.println("With comma: " + namesWithComma);
        
        String namesWithPrefix = people.stream()
            .map(Person::getName)
            .collect(Collectors.joining(", ", "People: [", "]"));
        System.out.println("With prefix/suffix: " + namesWithPrefix);
        
        // 5. counting()
        System.out.println("\n5. counting()");
        Long totalCount = people.stream()
            .collect(Collectors.counting());
        System.out.println("Total count: " + totalCount);
        
        // 6. summingInt(), summingLong(), summingDouble()
        System.out.println("\n6. summing operations");
        Integer totalAge = people.stream()
            .collect(Collectors.summingInt(Person::getAge));
        System.out.println("Total age: " + totalAge);
        
        Double totalSalary = people.stream()
            .collect(Collectors.summingDouble(Person::getSalary));
        System.out.println("Total salary: " + totalSalary);
        
        // 7. averagingInt(), averagingLong(), averagingDouble()
        System.out.println("\n7. averaging operations");
        Double avgAge = people.stream()
            .collect(Collectors.averagingInt(Person::getAge));
        System.out.println("Average age: " + avgAge);
        
        Double avgSalary = people.stream()
            .collect(Collectors.averagingDouble(Person::getSalary));
        System.out.println("Average salary: " + avgSalary);
        
        // 8. summarizingInt(), summarizingLong(), summarizingDouble()
        System.out.println("\n8. summarizing operations");
        IntSummaryStatistics ageStats = people.stream()
            .collect(Collectors.summarizingInt(Person::getAge));
        System.out.println("Age statistics: " + ageStats);
        System.out.println("  Count: " + ageStats.getCount());
        System.out.println("  Sum: " + ageStats.getSum());
        System.out.println("  Min: " + ageStats.getMin());
        System.out.println("  Max: " + ageStats.getMax());
        System.out.println("  Average: " + ageStats.getAverage());
        
        // 9. groupingBy() - Group elements
        System.out.println("\n9. groupingBy()");
        Map<String, List<Person>> byCity = people.stream()
            .collect(Collectors.groupingBy(Person::getCity));
        System.out.println("Grouped by city: " + byCity);
        
        Map<Integer, List<Person>> byAge = people.stream()
            .collect(Collectors.groupingBy(Person::getAge));
        System.out.println("Grouped by age: " + byAge);
        
        // groupingBy with downstream collector
        Map<String, Long> countByCity = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.counting()
            ));
        System.out.println("Count by city: " + countByCity);
        
        Map<String, Double> avgSalaryByCity = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.averagingDouble(Person::getSalary)
            ));
        System.out.println("Avg salary by city: " + avgSalaryByCity);
        
        // 10. partitioningBy() - Partition into true/false
        System.out.println("\n10. partitioningBy()");
        Map<Boolean, List<Person>> partitionedByAge = people.stream()
            .collect(Collectors.partitioningBy(p -> p.getAge() >= 30));
        System.out.println("Age >= 30: " + partitionedByAge.get(true));
        System.out.println("Age < 30: " + partitionedByAge.get(false));
        
        Map<Boolean, Long> countPartitioned = people.stream()
            .collect(Collectors.partitioningBy(
                p -> p.getAge() >= 30,
                Collectors.counting()
            ));
        System.out.println("Count partitioned: " + countPartitioned);
        
        // 11. maxBy(), minBy()
        System.out.println("\n11. maxBy(), minBy()");
        Optional<Person> oldest = people.stream()
            .collect(Collectors.maxBy(Comparator.comparing(Person::getAge)));
        System.out.println("Oldest: " + oldest.get());
        
        Optional<Person> youngest = people.stream()
            .collect(Collectors.minBy(Comparator.comparing(Person::getAge)));
        System.out.println("Youngest: " + youngest.get());
        
        // 12. mapping() - Map then collect
        System.out.println("\n12. mapping()");
        Map<String, List<String>> namesByCity = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.mapping(Person::getName, Collectors.toList())
            ));
        System.out.println("Names by city: " + namesByCity);
        
        // 13. filtering() - Filter then collect (Java 9+)
        System.out.println("\n13. filtering()");
        Map<String, List<Person>> highEarnersByCity = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.filtering(
                    p -> p.getSalary() > 6000000,
                    Collectors.toList()
                )
            ));
        System.out.println("High earners by city: " + highEarnersByCity);
    }

    // ========================================================================
    // PART 6: ADVANCED OPERATIONS
    // ========================================================================
    public static void part6_AdvancedOperations() {
        System.out.println("\n--- PART 6: ADVANCED OPERATIONS ---");
        
        // 1. Optional handling
        System.out.println("\n1. Optional handling");
        List<String> names = Arrays.asList("Ali", "Budi", "Citra");
        
        Optional<String> first = names.stream()
            .filter(n -> n.startsWith("D"))
            .findFirst();
        
        // Cara 1: isPresent() dan get()
        if (first.isPresent()) {
            System.out.println("Found: " + first.get());
        } else {
            System.out.println("Not found");
        }
        
        // Cara 2: orElse()
        String result = first.orElse("Default");
        System.out.println("Result: " + result);
        
        // Cara 3: orElseGet() - lazy evaluation
        String result2 = first.orElseGet(() -> "Default from supplier");
        System.out.println("Result2: " + result2);
        
        // Cara 4: orElseThrow()
        try {
            String result3 = first.orElseThrow(() -> 
                new NoSuchElementException("Not found!"));
        } catch (NoSuchElementException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        
        // Cara 5: ifPresent()
        first.ifPresent(n -> System.out.println("If present: " + n));
        
        // Cara 6: ifPresentOrElse() - Java 9+
        first.ifPresentOrElse(
            n -> System.out.println("Present: " + n),
            () -> System.out.println("Empty!")
        );
        
        // 2. Chaining multiple operations
        System.out.println("\n2. Complex chaining");
        List<Person> people = Arrays.asList(
            new Person("Ali", 25, "Jakarta", 5000000),
            new Person("Budi", 30, "Bandung", 7000000),
            new Person("Citra", 25, "Jakarta", 6000000),
            new Person("Dewi", 35, "Surabaya", 8000000),
            new Person("Eko", 30, "Jakarta", 7500000)
        );
        
        // Complex query: Top 2 highest paid people from Jakarta
        List<Person> topJakartaEarners = people.stream()
            .filter(p -> p.getCity().equals("Jakarta"))
            .sorted(Comparator.comparing(Person::getSalary).reversed())
            .limit(2)
            .collect(Collectors.toList());
        System.out.println("Top 2 Jakarta earners: " + topJakartaEarners);
        
        // 3. Nested grouping
        System.out.println("\n3. Nested grouping");
        Map<String, Map<Integer, List<Person>>> nestedGroup = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.groupingBy(Person::getAge)
            ));
        System.out.println("Nested grouping (City -> Age): " + nestedGroup);
        
        // 4. Custom Collector
        System.out.println("\n4. Custom operations");
        
        // Combine multiple aggregations
        Map<String, String> cityInfo = people.stream()
            .collect(Collectors.groupingBy(
                Person::getCity,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                        long count = list.size();
                        double avgSalary = list.stream()
                            .mapToDouble(Person::getSalary)
                            .average()
                            .orElse(0);
                        return String.format("Count: %d, Avg Salary: %.0f", 
                            count, avgSalary);
                    }
                )
            ));
        System.out.println("City info: " + cityInfo);
        
        // 5. Teeing (Java 12+) - Split stream into two
        System.out.println("\n5. Teeing (Java 12+)");
        
        // Get both min and max in one pass
        Optional<Person> result4 = people.stream()
            .collect(Collectors.teeing(
                Collectors.minBy(Comparator.comparing(Person::getAge)),
                Collectors.maxBy(Comparator.comparing(Person::getAge)),
                (min, max) -> {
                    System.out.println("Youngest: " + min.get());
                    System.out.println("Oldest: " + max.get());
                    return max; // return one of them
                }
            ));
    }

    // ========================================================================
    // PART 7: PARALLEL STREAMS
    // ========================================================================
    public static void part7_ParallelStreams() {
        System.out.println("\n--- PART 7: PARALLEL STREAMS ---");
        
        /*
         * PARALLEL STREAM:
         * - Memproses data secara paralel menggunakan multiple threads
         * - Menggunakan ForkJoinPool
         * - Bagus untuk operasi CPU-intensive dengan data besar
         * - HATI-HATI: tidak selalu lebih cepat!
         */
        
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            numbers.add(i);
        }
        
        // Sequential stream
        System.out.println("\n1. Sequential vs Parallel");
        long start = System.currentTimeMillis();
        long sumSeq = numbers.stream()
            .mapToLong(n -> n * n)
            .sum();
        long timeSeq = System.currentTimeMillis() - start;
        System.out.println("Sequential - Sum: " + sumSeq + ", Time: " + timeSeq + "ms");
        
        // Parallel stream
        start = System.currentTimeMillis();
        long sumPar = numbers.parallelStream()
            .mapToLong(n -> n * n)
            .sum();
        long timePar = System.currentTimeMillis() - start;
        System.out.println("Parallel - Sum: " + sumPar + ", Time: " + timePar + "ms");
        
        // Membuat parallel stream
        System.out.println("\n2. Creating parallel streams");
        
        // Cara 1: parallelStream()
        numbers.parallelStream().forEach(n -> {});
        
        // Cara 2: parallel() method
        numbers.stream().parallel().forEach(n -> {});
        
        // 3. Thread safety
        System.out.println("\n3. Thread safety issues");
        
        // WRONG - not thread safe!
        List<Integer> resultWrong = new ArrayList<>(); // ArrayList is not thread-safe
        // numbers.parallelStream().forEach(n -> resultWrong.add(n)); // DON'T DO THIS!
        
        // CORRECT - use collect()
        List<Integer> resultCorrect = numbers.parallelStream()
            .collect(Collectors.toList());
        System.out.println("Correct parallel collection size: " + resultCorrect.size());
        
        // 4. Convert back to sequential
        System.out.println("\n4. Sequential after parallel");
        long count = numbers.parallelStream()
            .filter(n -> n > 500)
            .sequential() // convert back
            .count();
        System.out.println("Count > 500: " + count);
        
        // 5. When to use parallel streams
        System.out.println("\n5. When to use parallel streams");
        System.out.println("Use parallel when:");
        System.out.println("- Large dataset (thousands+ elements)");
        System.out.println("- CPU-intensive operations");
        System.out.println("- Operations are independent (no shared state)");
        System.out.println("- Order doesn't matter");
        System.out.println("\nAvoid parallel when:");
        System.out.println("- Small dataset");
        System.out.println("- Simple operations (overhead > benefit)");
        System.out.println("- Order matters");
        System.out.println("- I/O operations (use async instead)");
    }

    // ========================================================================
    // PART 8: PRIMITIVE STREAMS
    // ========================================================================
    public static void part8_PrimitiveStreams() {
        System.out.println("\n--- PART 8: PRIMITIVE STREAMS ---");
        
        /*
         * PRIMITIVE STREAMS:
         * - IntStream, LongStream, DoubleStream
         * - Lebih efisien (no boxing/unboxing)
         * - Punya method khusus: sum(), average(), etc.
         */
        
        // 1. IntStream
        System.out.println("\n1. IntStream");
        
        // range() - eksklusif end
        IntStream range1 = IntStream.range(1, 6); // 1,2,3,4,5
        System.out.println("range(1,6): " + 
            range1.boxed().collect(Collectors.toList()));
        
        // rangeClosed() - inklusif end
        IntStream range2 = IntStream.rangeClosed(1, 5); // 1,2,3,4,5
        System.out.println("rangeClosed(1,5): " + 
            range2.boxed().collect(Collectors.toList()));
        
        // IntStream operations
        int sum = IntStream.range(1, 11).sum();
        System.out.println("Sum 1-10: " + sum);
        
        OptionalDouble avg = IntStream.range(1, 11).average();
        System.out.println("Average 1-10: " + avg.getAsDouble());
        
        IntSummaryStatistics stats = IntStream.range(1, 11).summaryStatistics();
        System.out.println("Statistics: " + stats);
        
        // 2. mapToInt, mapToLong, mapToDouble
        System.out.println("\n2. Mapping to primitives");
        
        List<String> numbers = Arrays.asList("1", "2", "3", "4", "5");
        
        int total = numbers.stream()
            .mapToInt(Integer::parseInt)
            .sum();
        System.out.println("Total: " + total);
        
        List<Person> people = Arrays.asList(
            new Person("Ali", 25, "Jakarta", 5000000),
            new Person("Budi", 30, "Bandung", 7000000),
            new Person("Citra", 25, "Jakarta", 6000000)
        );
        
        double avgSalary = people.stream()
            .mapToDouble(Person::getSalary)
            .average()
            .orElse(0);
        System.out.println("Average salary: " + avgSalary);
        
        // 3. boxed() - Convert primitive stream to object stream
        System.out.println("\n3. boxed()");
        
        List<Integer> intList = IntStream.range(1, 6)
            .boxed()
            .collect(Collectors.toList());
        System.out.println("Boxed: " + intList);
        
        // 4. mapToObj() - Convert to object stream
        System.out.println("\n4. mapToObj()");
        
        List<String> strings = IntStream.range(1, 6)
            .mapToObj(i -> "Number: " + i)
            .collect(Collectors.toList());
        System.out.println("MapToObj: " + strings);
        
        // 5. DoubleStream
        System.out.println("\n5. DoubleStream");
        
        double[] prices = {10.5, 20.3, 15.7, 30.2};
        double totalPrice = Arrays.stream(prices).sum();
        double avgPrice = Arrays.stream(prices).average().orElse(0);
        
        System.out.println("Total price: " + totalPrice);
        System.out.println("Average price: " + avgPrice);
    }

    // ========================================================================
    // PART 9: REAL WORLD EXAMPLES
    // ========================================================================
    public static void part9_RealWorldExamples() {
        System.out.println("\n--- PART 9: REAL WORLD EXAMPLES ---");
        
        // Sample data
        List<Order> orders = Arrays.asList(
            new Order("ORD001", "Ali", 150000, "COMPLETED"),
            new Order("ORD002", "Budi", 250000, "PENDING"),
            new Order("ORD003", "Ali", 300000, "COMPLETED"),
            new Order("ORD004", "Citra", 100000, "CANCELLED"),
            new Order("ORD005", "Budi", 400000, "COMPLETED"),
            new Order("ORD006", "Ali", 200000, "PENDING")
        );
        
        // Example 1: Sales Report
        System.out.println("\n=== EXAMPLE 1: Sales Report ===");
        
        // Total completed sales
        double totalSales = orders.stream()
            .filter(o -> o.getStatus().equals("COMPLETED"))
            .mapToDouble(Order::getAmount)
            .sum();
        System.out.println("Total completed sales: Rp " + totalSales);
        
        // Sales by customer
        Map<String, Double> salesByCustomer = orders.stream()
            .filter(o -> o.getStatus().equals("COMPLETED"))
            .collect(Collectors.groupingBy(
                Order::getCustomer,
                Collectors.summingDouble(Order::getAmount)
            ));
        System.out.println("Sales by customer: " + salesByCustomer);
        
        // Top customer
        Optional<Map.Entry<String, Double>> topCustomer = salesByCustomer.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());
        topCustomer.ifPresent(entry -> 
            System.out.println("Top customer: " + entry.getKey() + 
                " with Rp " + entry.getValue()));
        
        // Example 2: Order Status Summary
        System.out.println("\n=== EXAMPLE 2: Order Status Summary ===");
        
        Map<String, Long> statusCount = orders.stream()
            .collect(Collectors.groupingBy(
                Order::getStatus,
                Collectors.counting()
            ));
        System.out.println("Order count by status: " + statusCount);
        
        Map<String, Double> avgAmountByStatus = orders.stream()
            .collect(Collectors.groupingBy(
                Order::getStatus,
                Collectors.averagingDouble(Order::getAmount)
            ));
        System.out.println("Average amount by status: " + avgAmountByStatus);
        
        // Example 3: Find specific orders
        System.out.println("\n=== EXAMPLE 3: Find Specific Orders ===");
        
        // High value completed orders
        List<Order> highValueOrders = orders.stream()
            .filter(o -> o.getStatus().equals("COMPLETED"))
            .filter(o -> o.getAmount() > 200000)
            .sorted(Comparator.comparing(Order::getAmount).reversed())
            .collect(Collectors.toList());
        System.out.println("High value orders: " + highValueOrders);
        
        // Pending orders for specific customer
        List<Order> aliPendingOrders = orders.stream()
            .filter(o -> o.getCustomer().equals("Ali"))
            .filter(o -> o.getStatus().equals("PENDING"))
            .collect(Collectors.toList());
        System.out.println("Ali's pending orders: " + aliPendingOrders);
        
        // Example 4: Data Transformation
        System.out.println("\n=== EXAMPLE 4: Data Transformation ===");
        
        // Create summary objects
        List<String> orderSummaries = orders.stream()
            .map(o -> String.format("%s: %s - Rp %.0f (%s)", 
                o.getOrderId(), o.getCustomer(), o.getAmount(), o.getStatus()))
            .collect(Collectors.toList());
        System.out.println("Order summaries:");
        orderSummaries.forEach(System.out::println);
        
        // Example 5: Validation
        System.out.println("\n=== EXAMPLE 5: Validation ===");
        
        boolean allHaveCustomer = orders.stream()
            .allMatch(o -> o.getCustomer() != null && !o.getCustomer().isEmpty());
        System.out.println("All orders have customer: " + allHaveCustomer);
        
        boolean anyHighValue = orders.stream()
            .anyMatch(o -> o.getAmount() > 500000);
        System.out.println("Any order > 500k: " + anyHighValue);
        
        boolean noCancelledHighValue = orders.stream()
            .filter(o -> o.getStatus().equals("CANCELLED"))
            .noneMatch(o -> o.getAmount() > 300000);
        System.out.println("No cancelled order > 300k: " + noCancelledHighValue);
        
        // Example 6: Pagination
        System.out.println("\n=== EXAMPLE 6: Pagination ===");
        
        int pageSize = 2;
        int pageNumber = 1; // 0-based
        
        List<Order> page = orders.stream()
            .sorted(Comparator.comparing(Order::getOrderId))
            .skip(pageNumber * pageSize)
            .limit(pageSize)
            .collect(Collectors.toList());
        System.out.println("Page " + (pageNumber + 1) + ": " + page);
    }

    // ========================================================================
    // PART 10: BEST PRACTICES & COMMON MISTAKES
    // ========================================================================
    public static void part10_BestPractices() {
        System.out.println("\n--- PART 10: BEST PRACTICES ---");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        System.out.println("\n=== BEST PRACTICES ===");
        
        System.out.println("\n1. Prefer method references over lambdas when possible");
        // GOOD
        numbers.stream().forEach(System.out::println);
        // LESS GOOD
        numbers.stream().forEach(n -> System.out.println(n));
        
        System.out.println("\n2. Use appropriate terminal operations");
        // GOOD - for side effects
        numbers.stream().forEach(n -> System.out.println(n));
        // GOOD - for collecting
        List<Integer> list = numbers.stream().collect(Collectors.toList());
        
        System.out.println("\n3. Avoid modifying source during stream operations");
        List<Integer> mutableList = new ArrayList<>(numbers);
        // BAD - don't do this!
        // mutableList.stream().forEach(n -> mutableList.add(n * 2));
        
        System.out.println("\n4. Use parallel streams carefully");
        // GOOD - independent operations
        long sum = numbers.parallelStream().mapToLong(n -> n * n).sum();
        // BAD - shared state
        // List<Integer> result = new ArrayList<>();
        // numbers.parallelStream().forEach(n -> result.add(n)); // NOT THREAD SAFE!
        
        System.out.println("\n5. Handle Optional properly");
        Optional<Integer> optional = numbers.stream().findFirst();
        // GOOD
        optional.ifPresent(System.out::println);
        // or
        Integer value = optional.orElse(0);
        // BAD
        // Integer badValue = optional.get(); // can throw exception!
        
        System.out.println("\n=== COMMON MISTAKES ===");
        
        System.out.println("\n1. Reusing streams");
        Stream<Integer> stream = numbers.stream();
        stream.count(); // OK
        // stream.count(); // ERROR - stream already used!
        
        System.out.println("\n2. Forgetting terminal operation");
        // This does NOTHING - no terminal operation!
        numbers.stream().filter(n -> n > 3).map(n -> n * 2);
        // Need terminal operation:
        List<Integer> result = numbers.stream()
            .filter(n -> n > 3)
            .map(n -> n * 2)
            .collect(Collectors.toList());
        
        System.out.println("\n3. Using forEach for transformation");
        // BAD - don't use forEach for transformation
        List<Integer> bad = new ArrayList<>();
        numbers.stream().forEach(n -> bad.add(n * 2));
        // GOOD - use map and collect
        List<Integer> good = numbers.stream()
            .map(n -> n * 2)
            .collect(Collectors.toList());
        
        System.out.println("\n4. Not handling null values");
        List<String> withNulls = Arrays.asList("A", null, "B");
        // BAD - can throw NullPointerException
        // withNulls.stream().map(String::toLowerCase).collect(Collectors.toList());
        // GOOD - filter nulls first
        List<String> safe = withNulls.stream()
            .filter(Objects::nonNull)
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        
        System.out.println("\n=== PERFORMANCE TIPS ===");
        System.out.println("1. Use primitive streams (IntStream) for primitives");
        System.out.println("2. Use parallel() only for large datasets and CPU-intensive ops");
        System.out.println("3. Put filter() before expensive operations like map()");
        System.out.println("4. Use limit() early to reduce processing");
        System.out.println("5. Prefer Collection.removeIf() over filter().collect()");
        
        System.out.println("\n=== DEBUGGING TIPS ===");
        System.out.println("Use peek() to debug stream pipeline:");
        List<Integer> debugResult = numbers.stream()
            .filter(n -> n > 2)
            .peek(n -> System.out.println("After filter: " + n))
            .map(n -> n * 2)
            .peek(n -> System.out.println("After map: " + n))
            .collect(Collectors.toList());
    }
}

// ============================================================================
// HELPER CLASSES
// ============================================================================

class Person {
    private String name;
    private int age;
    private String city;
    private double salary;
    
    public Person(String name, int age, String city, double salary) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.salary = salary;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCity() { return city; }
    public double getSalary() { return salary; }
    
    @Override
    public String toString() {
        return String.format("Person{name='%s', age=%d, city='%s', salary=%.0f}", 
            name, age, city, salary);
    }
}

class Order {
    private String orderId;
    private String customer;
    private double amount;
    private String status;
    
    public Order(String orderId, String customer, double amount, String status) {
        this.orderId = orderId;
        this.customer = customer;
        this.amount = amount;
        this.status = status;
    }
    
    public String getOrderId() { return orderId; }
    public String getCustomer() { return customer; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    
    @Override
    public String toString() {
        return String.format("Order{id='%s', customer='%s', amount=%.0f, status='%s'}", 
            orderId, customer, amount, status);
    }
}

/*
 * ============================================================================
 * KESIMPULAN DAN NEXT STEPS
 * ============================================================================
 * 
 * Kamu sudah belajar:
 * ✓ Dasar-dasar Stream dan keuntungannya
 * ✓ Cara membuat Stream dari berbagai sumber
 * ✓ Intermediate operations (filter, map, flatMap, dll)
 * ✓ Terminal operations (collect, reduce, forEach, dll)
 * ✓ Collectors yang powerful
 * ✓ Advanced operations dan chaining
 * ✓ Parallel streams
 * ✓ Primitive streams
 * ✓ Real world examples
 * ✓ Best practices dan common mistakes
 * 
 * NEXT STEPS:
 * 1. Praktikkan dengan menulis code sendiri
 * 2. Coba convert existing loops ke Stream
 * 3. Eksperimen dengan different collectors
 * 4. Pelajari lebih dalam tentang lambda expressions
 * 5. Baca Java documentation untuk detail lebih lanjut
 * 
 * RESOURCES:
 * - Java API Documentation: https://docs.oracle.com/javase/8/docs/api/
 * - Java Stream Tutorial: https://docs.oracle.com/javase/tutorial/collections/streams/
 * 
 * Happy Coding! 🚀
 */
