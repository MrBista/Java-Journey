package com.bisma.main;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JavaStreamLearnTest {

    @Test
    void testCreateStream() {

        // perlu diingat bahwa stream itu adalah aliran data
        // yang mana kalau aliran itu pasti mengalir satu kali
        // stream ini pass by value dia tidak mengubah data aslinya
        //

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
}
