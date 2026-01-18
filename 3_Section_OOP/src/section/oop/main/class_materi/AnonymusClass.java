package src.section.oop.main.class_materi;

public class AnonymusClass {
    public static void main(String[] args) {

        // ini bukan lah object tapi anonymus class, ingat tuk interface dan abstract itu ga bisa di buat jadi object
        // mereka berdua hanya bisa tuk di implementasikan saja
        HelloWorld sayHelloEveryOne = new HelloWorld() {
            @Override
            public void sayHello() {

            }

            @Override
            public void sayHello(String name) {

            }

            @Override
            public void sayHai(String name) {
                HelloWorld.super.sayHai(name);
            }
        };

        sayHelloEveryOne.sayHai("Bisma");


    }
}
