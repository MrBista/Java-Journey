import java.util.*;

public class CollectionMain {
    public static void main(String[] args) {
        System.out.println("Hallo dunia");

        // code dibawah akan error karena list of itu imutable jadi fiks saat pertama run dan ga bisa di tambah lagi
//        Collection<String> names = List.of("baba");
//        names.add("Bobo");

        // ============================= list ==================================
        Collection<String> names = new ArrayList<>();
        names.add("Bubu");
        names.add("Bebe");
        names.add("Baraga siang");
        names.addAll(Arrays.asList("Baba", "baba", "bebe", "bobo"));
        names.remove("bobo");
        names.removeAll(Arrays.asList("Bubu", "Bebe", "bebe"));
        System.out.println(names.contains("Baraga siang") ? "Ada baraga siang" : "-");
        System.out.println(Arrays.toString(names.toArray()));

        names.forEach(el -> {
            System.out.println("Hello " + el);
        });

        Person person1 = new Person();
        person1.addHobie("Berak");
//        person1.getHobbies().add("Kok berak lagi ?");
        person1.addHobie("Nah bener berak lagi");


        System.out.println(Arrays.toString(person1.getHobbies().toArray()));



        // ============================= set ==================================

        // hash set itu array yg uniq, ga punya index, pakai hascode, mirip hash table
        // bagus banget kalau kita butuh uniq di array nya
        Set<String> namesSet = new HashSet<>();
        namesSet.add("Bismen");
        namesSet.add("Gusti");
        namesSet.add("Bismen");


        namesSet.forEach(el -> {
            System.out.println("Hallo " + el);
        });



        // ============================= map ==================================

        // map itu ga ngikut ke collection interface
        // dia justru merupakan parent interface yg memiliki method-method yg bisa di impelementasikan
        // hashmap contoh implementasi yang sangat powerful mengunakan hash table

        Map<String, String> person = new HashMap<>();
        person.put("Name", "Bisma Bratha");
        person.put("age", String.valueOf(2));


        Map<String, Integer> frequentWord = new HashMap<>();
        String[] namesMap = {"Bisma", "bisboy", "mama", "joko", "jumen", "bisboy", "ego"};

        for (String name : namesMap) {
            frequentWord.computeIfAbsent(name, (k) -> {
                System.out.println("Value k apa ya: " + k);
                return 0;
            } );
            frequentWord.put(name, frequentWord.get(name) + 1);
        }

        System.out.println(frequentWord.toString());


    }
}
