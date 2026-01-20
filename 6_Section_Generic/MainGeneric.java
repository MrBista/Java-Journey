import java.util.List;

public class MainGeneric {
    public static void main(String[] args) {
        System.out.println("Hello Generic");

        // generic class sederhana
        Box<String> buah = new Box<>();
        buah.setBarang("Buah adalah buah");

        // invariant kalau pakai wildcard gimana ya
//        Box<?> buahApaAja = new Box<>("barang bos q");
//
//        buahApaAja.setBarang(323); // error ternyata ga bisa wkwkwk karena memang ya invariant


        // method generic
        String[] names = {"Gusti", "Bisman", "Taka"};
        UtilHelper.printArray(names);

        Integer[]angka = {2, 3, 12, 34};
        UtilHelper.printArray(angka);

        // contoh upper bound (covariant)
//        NumberBox<String> numString = new NumberBox<String>(); // ini pasti error karena string bukan subclass number
        NumberBox<Integer> intNih = new NumberBox<>();
        intNih.setNumber(3);

//        NumberBox<Double> doubleNih = intNih; // ga boleh karena invariant, kalau ini boleh nanti bahaya, harusnya double malah jadi int valuenya makannya ada contravariant tuk mengubah valuenya


        // pakai cara ini kalau mau ngubah (contravariant/ lower bound)
        // ini works karena dia hanya bisa tuk class itu atau parent dari class tersebut otomatis akan aman
        List<Number> numBros = List.of(3,2,4);
        UtilHelper.addNumValue(numBros);


    }
}
