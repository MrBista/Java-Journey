import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MainLamda {
    public static void main(String[] args) {
        System.out.println("Hallo dunia");
        // basicly lamda itu adalah anonimu class versi paling sederhana
        // ini versi anonim class
        Sallam ucapSallam = new Sallam() {
            @Override
            public void sayHello(String name) {
                System.out.println("Hallo anonim class " + name);
            }
        };
        ucapSallam.sayHello("Bisboy");

        Sallam ucapSallamBro = name -> {
            System.out.println("Hall bro " + name);
        };

        ucapSallamBro.sayHello("BisMenBre");


        // ============================================================================================
        // 1. consumer
        // ini implementasinya
        Consumer<String> sayHai = value -> System.out.println("Hai tuan " + value);

        // ini pemakaiannya
        sayHai.accept("Bista");

        // 2. function
        Function<String, Integer> getLengthName = value -> value.length();
        Integer bismaLength = getLengthName.apply("Bisma");
        System.out.println("Bisma length - " + bismaLength);

        // 3. predicate
        Predicate<String> isNameMoreThan5Char = val -> val.length() > 5;

        boolean isNameMoreThan5CharAndBoyKunMa = isNameMoreThan5Char
                .and(v -> v.equals("BoyKunMa"))
                .test("BoyKunMa");

        System.out.println("Is true : " + isNameMoreThan5CharAndBoyKunMa);


        // 4. suplier
        Supplier<String> getNameBoyKun = () -> "Boy Kun";
        System.out.println(getNameBoyKun.get());

        // etc bisa di lihat di package java.util.function;

        List<String> listOfName = Arrays.asList("Bisma", "Bratah", "BoyKunMa", "Ads");
        listOfName.forEach(v -> {
            Integer lengthOfName = getLengthName.apply(v);
            boolean charMoreThan5 = isNameMoreThan5Char.test(v);
            if (charMoreThan5) {
                System.out.println("Name - " + lengthOfName + " - have more than 5 char");
            }else {
                System.out.println("Name - " + lengthOfName + " - have less than 5 char");
            }
        });

        // ================================================================================
        // method reference
        // bisa pakai method reference kalau signature nya sama, common nya pakai static method yang memiliki signature sama
        // mulai dari param hingga tipe data balikannya


//        Predicate<String> nameLowerCase = (val) -> StringUtil.isAllLowerCase(val); // ini tanpa method references
            Predicate<String> nameLowerCase = StringUtil::isAllLowerCase; // ini dengan method reference lebih sederhana

        System.out.println("Is bista all lower case - " + nameLowerCase.test("bista"));
        System.out.println("Is Bista all lower case - " + nameLowerCase.test("Bista"));


        // =====================================================================================
        // lazy paramter

        // tanpa lay paramter getName walau ga dibutuhin tetap di panggil
        testScore(60, getName());

        // dengan lazy paramter, konsepnya mirip seperti callback
//        testScore(100, () -> getName());

        testScore(60, MainLamda::getName);


    }

    public static void testScore(int value, String name) {
        if (value >= 80) {
            System.out.println("Selamat " + name + ", Anda lulus");
        }else {
            System.out.println("Coba lagi tahun depan");
        }
    }

    public static void testScore(int value, Supplier<String> name) {
        if (value >= 80) {
            System.out.println("Selamat " + name.get() + ", Anda lulus");
        }else {
            System.out.println("Coba lagi tahun depan");
        }
    }

    public static String getName() {
        System.out.println("getName() dipanggil and imagine there is heavey logic here");
        return "BisMen";
    }
}
