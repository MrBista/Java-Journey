package src.section.oop.fourprinciple.abstraction;

public class MainAbstractoin {
    public static void main(String[] args) {
        System.out.println("Hello world abstraction");

        /*
            abstraction itu menyembunyikan
            yang disembunyikan itu adalah implementasi dari luar
            diluar ga perlu tau function ini gimana
            cukup mengimplementasikannya
            contoh real world
            lu punya remote ya tinggal pakai aja dan lu ga perlu tau gimana remote itu bekerja, cukup memakainya aja
            class abstract ini ga bisa di initatiate
            abstract class harus punya minimal 1 abstract method, kalau ga ya bukan abstract namanya
            child wajib implemnt semua abstract method
            abstract method ga punya body

         */

        // abstract bisa di initate asal dia buat anonymus class, jatuhnya sama aja buat class baru sih yak
//        PaymentMethod pay = new PaymentMethod() {
//            @Override
//            public void paymentProcess() {
//
//            }
//
//            @Override
//            public boolean sendReciept() {
//                return false;
//            }
//        };

        // kalau seperti ini bisa karena ini bukan ngebuat abstractnya tapi yg ngemiplementasikannya(polymorphism)
        GopayPaymentMethod gopeyMethodDetail = new GopayPaymentMethod();
        gopeyMethodDetail.setPhoneNumber("2302323894834");
        PaymentMethod payGopay = gopeyMethodDetail;
        payGopay.generateTransactionId();

        payGopay.paymentProcess();
        payGopay.sendReciept();


    }
}
