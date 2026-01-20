import java.util.List;

public class UtilHelper {


    // generic juga bisa di method aja syaratnya type parameter sebelum type return
    public static <T> void printArray(T[] arrays) {
        for (T element : arrays) {
            System.out.println(element);
        }
    }


    // contravariant hanya bisa class integer dan parentnya
    public static void addNumValue(List<? super Integer> list) {
        list.add(3);
        list.add(20);
    }

}
