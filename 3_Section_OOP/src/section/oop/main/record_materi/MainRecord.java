package src.section.oop.main.record_materi;

import java.util.List;

public class MainRecord {
    public static void main(String[] args) {
        // record itu tuk imutablitiy
        // semisal kita ingin sebuah object yang sudah final dan ga akan berubah field-field nya record ini sangat cocok
        User user = new User("Bisma", 12, List.of("Ngising"));
        System.out.println(user.toString());

        System.out.println("Name: " + user.name() + " Age: " + user.age());
    }
}
