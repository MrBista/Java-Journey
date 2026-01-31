package com.bisma.main;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaStreamLearnTest {

    @Test
    void testCreateStream() {

        // perlu diingat bahwa stream itu adalah aliran data
        // yang mana kalau aliran itu pasti mengalir satu kali
        // stream ini pass by value dia tidak mengubah data aslinya
        // didalam stream juga terdapat yang namanya stream operation
        // stream operations ini lah yang paling membuat stream populer
        // dengan stream operation kita bisa memanipulasi stream itu sendiri
        // secara garis besar stream itu memiliki dua operation utama yaitu intermediate operation dan terminal operation
        // defaultnya adalah intermedieate operation(lazy evaluation)
        // karena lazy dia akan mengembalikan stream lagi
        // sedangkan terminal operation tidak mengembalikan apa apa lagi

        Stream<String> stream1 = Stream.of("b", "a");
        Stream<String> stream2  = Stream.empty();
        Stream<String> stream3 = Stream.ofNullable(null);


        String[] names = {"bisma", "bimen"};

        Stream<String> nameStreamArray = Arrays.stream(names);

        System.out.println("Stream result for nameStreamArray: " + nameStreamArray.toString());


        List<String> nameStringList = new ArrayList<>(List.of("bismen", "bomen", "bamen"));
        nameStringList.add("Baba");
        Stream<String> nameStringListToStream = nameStringList.stream(); // di buat dari collection
        System.out.println(nameStringList.toString());

    }

    @Test
    void testRunSimpleStream() {
        Stream<String> names = Stream.of("Bismen", "Gusti", "InI");
        names.forEach(System.out::println);

        List<String> regions = new ArrayList<>(List.of("lumajang", "Bogor", "Bekasi", "Depok"));
        regions.stream().forEach(System.out::println);
    }

    @Test
    void testStreamBuilder() {
        Stream.Builder<String> builder = Stream.builder();
        builder.accept("Bismen");
        builder.add("Brataha").add("Yudha");

        Stream<String> names = builder.build();
        names.forEach(System.out::println);

    }

    @Test
    void testBasicOperationStream() {
        List<String> names = new ArrayList<>(List.of("Bismen", "Gusti", "Taka"));
        Stream<String> streamNames = names.stream();
        Stream<String> streamNamesCapitalize = streamNames.map(String::toUpperCase);
        streamNamesCapitalize.forEach(System.out::println);
    }


    @Test
    void testStreamPipeline() {
        // basicly stream pipeline itu yg chaining yang nanti bisa melakukan operation stream sampai ditutup dengan end operation

        List<String> names = new ArrayList<>(List.of("Bismen", "Gusti", "Taka"));
        names
            .stream()
            .map(String::toUpperCase)
            .map(val -> "Mr. " + val)
            .forEach(System.out::println);
    }


    @Test
    void testInterMediateOperationBasic() {
        List<String> names = new ArrayList<>(List.of("Bismeng", "BoyKUn", "BroBis"));
        Stream<String> nameStrem = names.stream().map(val -> {
            System.out.println("Memanggail stream dengan value: " + val);
            return val.toUpperCase();
        });

        nameStrem.forEach(System.out::println);
    }


    @Test
    void testTerminalOperationBasic() {
        List<String> names = new ArrayList<>(List.of("Bismeng", "BoyKUn", "BroBis"));
        Stream<String> nameStrem = names.stream().map(val -> {
            System.out.println("Memanggail stream dengan value: " + val);
            return val.toUpperCase();
        });


        nameStrem.forEach(val -> {
            System.out.println("Print name : " + val);
        });
    }


    @Test
    void testTransformationOperation() {
       /*
         seperti namanya transformation operation ini adalah salah satu dari intermediate operation yang berfungsi untuk
         mengubah data, terdapat dua transformastion operation ini yakni map dan flatmap
         yang sering dipakai ya map,
         flatmap itu lebih kaya tuk ngenyatuin data yg neseted
        */

        List.of("Jepang", "Belanda", "Malaysia", "England", "Swish", "New zeland", "Neterland")
                .stream()
                .map(String::toUpperCase)
                .map(val -> "Dream Place: " + val)
                .forEach(System.out::println);


        List<Integer> evens = Arrays.asList(2, 4, 6);
        List<Integer> odds = Arrays.asList(3, 5, 7);
        List<Integer> primes = Arrays.asList(2, 3, 5, 7, 11);

        List<List<Integer>> listOfLists = Arrays.asList(evens, odds, primes);

        listOfLists.stream()          // Stream<List<Integer>>
                .flatMap(List::stream)                                // Stream<Integer>
                .forEach(System.out::println);

        // ini jadi di gabung
        List<Integer> flattenedList = listOfLists.stream()          // Stream<List<Integer>>
                .flatMap(List::stream)                                // Stream<Integer>
                .collect(Collectors.toList());


        System.out.println("Data sebelum digabung -" + Arrays.toString(listOfLists.toArray()));
        System.out.println("Semua setelah di gabung -" +Arrays.toString(flattenedList.toArray()));

    }

    @Test
    void testFilteringOperation() {
        /*
            Sama dengan transform operation filtering operation ini adalah intermediate operation atau lazy evaluation
            filtering operation digunakan untuk menyaraing data pada aliran stream
            terdapat dua operation yakni
            filter: mengambil data yang masuk kriteria filter data
            distinct: menghapus semua data yang duplicate
         */
        List<String> listOfName = Arrays.asList("Bismen", "Boboboiy", "Bismen", "Taka", "A", "Ultraman", "Kak", "kukuku", "Kak");
        List<String> names = new ArrayList<>(listOfName);

        // ngefilter nama yg memiliki lebih dari 4 karaketer
//        names.stream().filter(val -> val.length() > 4).forEach(System.out::println);

        // mengambil nama yg uniq
        names.stream().filter(val -> val.length() > 4).distinct().forEach(System.out::println);
    }

    @Test
    void testRetrivingOperation() {
        /*
            mirip dengan filtering operaiton
            retrving operation adalah operation pada stream yang digunakan untuk melakukan sebagian data
            terdapat beberapa operation yakni
            limit: mengambil sejumlah n data
            skip: menghiraukan sejumlah n data
            takeWhile: mengambil data selama data true
            dropWhile: minghiraukan data selama kondisi true
         */
        List<String> listOfName = Arrays.asList("Bismen", "Boboboiy", "Bismen", "Taka", "A", "Ultraman", "Kak", "kukuku", "Kak");

        List<String> names = new ArrayList<>(listOfName);

//        names.stream().limit(2).forEach(System.out::println);
//        names.stream().skip(3).forEach(System.out::println);
//        names.stream().takeWhile(name -> name.length() != 4).forEach(System.out::println);
        names.stream().dropWhile(name -> name.length() < 4).forEach(System.out::println);

    }

    @Test
    void testRetrivingSingleElement() {
        List<String> listOfName = Arrays.asList("Bismen", "Boboboiy", "Taka", "A", "Ultraman", "Kak", "kukuku", "Kak");

        List<String> names = new ArrayList<>(listOfName);

        Optional<String> firstElementWithName = names.stream().findFirst(); // mengambil data dengan index 0, atau data pertama
        firstElementWithName.ifPresent(System.out::println );

        Optional<String> randomElementWithName = names.stream().findAny(); // mengambil random value dari array
        randomElementWithName.ifPresent(System.out::println);

    }

    @Test
    void testOrderingOperations() {
        List<Integer> angka = List.of(4, 3, 2, 11, 6, 7, 8, 3, 4);
        angka.stream().sorted().forEach(System.out::print);
        printline();
        angka.stream().sorted(Comparator.reverseOrder()).forEach(System.out::print);
    }

    void printline() {
        System.out.println();
        System.out.println("=================");
    }


    @Test
    void testAggregateOperation() {
        List<Integer> angka = List.of(4, 3, 2, 11, 6, 7, 8, 3, 4);
        Optional<Integer> maxAngka = angka.stream().max(Comparator.naturalOrder());
        System.out.println("max angka: "+ maxAngka.get());
        Optional<Integer> minAngka = angka.stream().min(Comparator.naturalOrder());
        System.out.println("Min angka: " + minAngka.get());

        long count = angka.stream().count(); // menghitung length
        System.out.println("Jumlah: " + count);
    }


    @Test
    void testCollectorsCollection() {
        List<String> listOfName = Arrays.asList("Bismen", "Boboboiy", "Taka", "Ultraman", "kukuku", "Gusti");

        List<String> names = new ArrayList<>(listOfName);

        Set<String> namesSet = names.stream().collect(Collectors.toSet());
        Set<String> namesSetImutable = names.stream().collect(Collectors.toUnmodifiableSet());


        List<String> nameList = listOfName.stream().collect(Collectors.toList());
        List<String> nameListImutable = listOfName.stream().collect(Collectors.toUnmodifiableList());

    }

    @Test
    void testCollectorsMap() {
        List<String> listOfName = Arrays.asList("Bismen", "Boboboiy", "Taka", "Ultraman", "kukuku", "Gusti");

        List<String> names = new ArrayList<>(listOfName);

        Map<String, Integer> namesValueLength = names.stream().collect(Collectors.toMap(val -> val, String::length));

        System.out.println(namesValueLength);
    }

    @Test
    void testCollectorsGroupingBy() {
        List<Integer> numbers = List.of(1, 3, 4, 5, 7, 12, 4, 7, 8, 9, 10, 22);

        Map<String, List<Integer>> groupingByBesarKecil = numbers
                .stream()
                .collect(Collectors.groupingBy(val -> val > 5 ? "Besar" : "Kecil"));

        System.out.println(groupingByBesarKecil);
    }

}
