package src.section.oop.main.object_materi;

public class Dog {

    private String name;
    private String color;
    private String type;


    // ini adalah constructor, constructor akan dipanggil ketika object pertama kali dipanggil
    public Dog() {
    }

    // constructor bisa di overloading
    public Dog(String name, String color, String type) {
        this.name = name;
        this.color = color;
        this.type = type;
    }

    // ini adalah method
    public void bark() {
        System.out.println("Huf huf huf");
    }

    // ini juga method dengan string sebagai balikannya
    public String sayHelloDog() {
        return "dog " + this.name + " say hello";
    }




}
