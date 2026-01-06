public class TipeData {
    public TipeData() {
    }

    public static void NumberTipeData() {
        int angkaInt = 10;
        long angkaBanayak = 10000L;
        double angkaDes = 10.2;
        Integer angkaObjek = 100;
        Long angkabanyakObjek = 100000L;
        System.out.println("ini adalah angka int " + angkaInt);
        System.out.println("Ini adalah angka long " + angkaBanayak);
        System.out.println("Decimal angka " + angkaDes);
        System.out.println("Angka objek" + angkaObjek);
        System.out.println("Angka banyak objek " + angkabanyakObjek);
    }

    public static void TipeDataChar() {
        char angkaA = 'A';
        char angkaB = 'B';
        System.out.println("Angka " + angkaA + " dan Angka " + angkaB);
    }

    public static void TipeDataBoolean() {
        boolean tipeDataBenar = true;
        boolean tipeDataSalah = false;
        System.out.println("tipe data benar " + tipeDataBenar);
        System.out.println("tipe data salah " + tipeDataSalah);
    }

    public static void TipeDataString() {
        String nama = "Bisman Taka";
        System.out.println(nama);
    }
}
