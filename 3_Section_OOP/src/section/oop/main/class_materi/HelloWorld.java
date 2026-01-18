package src.section.oop.main.class_materi;

public interface HelloWorld {
    void sayHello();
    void sayHello(String name);
    default void sayHai(String name) {
        System.out.println("Hai " + name);
    }
}
