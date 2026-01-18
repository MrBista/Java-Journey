package src.section.oop.main.static_keyword_materi;

public class StaticKeyword {
    public static void main(String[] args) {
        // ada rule di static keyword
        // 1. ga bisa manggil non static method ke dalam method yang static
        // biasanya tuk util dan constanta

        int penjumlahan = MathUtil.add(3, 1, 3, 2, 5, 200);

        System.out.println("Hasil penjumlahannya adalah " + penjumlahan);
    }
}
