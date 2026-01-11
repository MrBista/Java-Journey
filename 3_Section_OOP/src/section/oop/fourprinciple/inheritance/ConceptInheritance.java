package src.section.oop.fourprinciple.inheritance;

import src.section.oop.fourprinciple.inheritance.variabelhiding.Car;
import src.section.oop.fourprinciple.inheritance.variabelhiding.Vehicle;

public class ConceptInheritance {

    public static void main(String[] args) {
        // inheritance adalah is a relathisionship jadi parent dapat mewariskan sifatnya ke anaknya



        /*
            Intinya konsep inheritance, kalau dia itu bisa ambil semua method dan property milik parentnya
            tapi parentnya ga bisa ambil method serta variabel milik childnya
         */

        Car newCarBos = new Car();
        System.out.println(newCarBos.type);
        Vehicle mobilBaru = new Vehicle();


        // ============================= POLYMORPHISM ===================
        // ini konsep polymorphism, NANTI LEBIH DALAM DI POLYMORPHISM
        Vehicle lambo = new Car();

        // kalau method ini akan di ambil dari runtime type atau tipe object aslinya yg dari class car
        System.out.println("sound move like - "+lambo.move());
        // kalau variabel atau static method dia akan mengambil dari reference type atau yg sebelah kiri nya
        System.out.println("variabel hiding mana yg dipanggil - " + lambo.name);

        // ! ingat variabel hiding ini hanya saat kita akses nama variabelnya langsung ya
        System.out.println("ini manggil yang mana ya - " + lambo.getName());

        /* !
         ini juga konsep polymorpihsm atau perubahan bentuk, jadi sebenernya kita buat object car yg reference adalah Vehcile
         nantinya ini akan mengambil method-method milik reference atau vehicle saat runtime akan diubah ke object milik carnya


         */
        lambo.isChiper();
        lambo.move();
//        lambo.getParentName(); // ini harus di casting dulu ke object car karena getParentName ada di car class



    }


}
