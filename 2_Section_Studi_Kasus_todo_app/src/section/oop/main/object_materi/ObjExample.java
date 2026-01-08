package src.section.oop.main.object_materi;

import src.section.oop.main.class_materi.ClassExample;

public class ObjExample {
    public static void main(String[] args) {
        // ini adalah object yg dibuat dari class
        ClassExample ex = new ClassExample();
        ex.setName("Bisma Taka");
        ex.setAge(3);
        System.out.println("name : "+ ex.getName());
        System.out.println("age : "+ ex.getAge());

        // ini juga object yg dibuat dari class yg sama namun dia akan beda behaviour
        ClassExample ex2 = new ClassExample();
        ex.setName("BisBoy");
        ex.setAge(25);
        System.out.println("name 2: " + ex.getName());


        Dog dogi = new Dog();
        dogi.bark();

        Dog dogiJumbo = new Dog("Jumbo doggy", "blue", "cihua hua");
        // karena dia ada return value, jadi bisa di tampung di variabel
        String dogiSayHello = dogiJumbo.sayHelloDog();
        System.out.println(dogiSayHello);
    }
}
