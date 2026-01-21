import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionMain {
    public static void main(String[] args) {
        System.out.println("Hallo dunia");

        // code dibawah akan error karena list of itu imutable jadi fiks saat pertama run dan ga bisa di tambah lagi
//        Collection<String> names = List.of("baba");
//        names.add("Bobo");

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
    }
}
