package src.section.oop.main.class_materi;
/*
ini adalah class, class itu bluprint tuk buat object, jadi nanti setiap object akan dibuat dari class
setiap object yg dibuat dari class akan memiliki value dan behavior sendiri dengan object yg lain yg dibuat dengan class yg sama
*/
public class ClassExample {

    // ini adalah field
    private String name;
    private int age;
    // ini juga field, dengan access modifier public
    public String info;

    public ClassExample() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
