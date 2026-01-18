package src.section.oop.main.static_keyword_materi;

public class MathUtil {
    public static int add(int...values) {
        int valInit = 0;
        for (int val : values) {
            valInit += val;
        }

        return valInit;
    }

}
