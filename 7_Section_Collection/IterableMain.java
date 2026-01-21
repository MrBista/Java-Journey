import java.util.Iterator;
import java.util.List;

public class IterableMain {
    public static void main(String[] args) {
        Iterable<String> nameList = List.of("Bambang","Sugiono" ,"Superaman");

        // itnerface iterable itu cuma ada foreach
        // interface collection bapaknya itu iterable
        nameList.forEach(el -> {
            System.out.println("Hell " + el);
        });


        // everything happend for a reasone kalau di java
        // iterable ini bisa dilakukin karena ada iterator
        // sebelum foreach ada / sebelum java 5 tuk melakukan iterasi biasanya ya pakai iterator

        Iterable<String> nameSiswa = List.of("Buba", "baba", "bebe");

        Iterator<String> nameSiswaIterator = nameSiswa.iterator();
        while (nameSiswaIterator.hasNext()) {
            String name  = nameSiswaIterator.next();
            System.out.println("Iterator siswa " + name);
        }
    }
}
