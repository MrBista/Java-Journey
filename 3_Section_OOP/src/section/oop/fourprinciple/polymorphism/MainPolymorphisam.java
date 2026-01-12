package src.section.oop.fourprinciple.polymorphism;

public class MainPolymorphisam {
    public static void main(String[] args) {
        System.out.println("Hello pollymorphism");
        // intinya polymorphism itu adalah perubahan bentuk
        // salah satu concept oop yang sangat penting tuk diketahui
        // terdapat dua polymorphism tipe, yakni compile time(static) dan runtime(dynamic)

        // 1. compiletime polymorhsim
        Calculator  calculator = new Calculator();
        int a = calculator.add(1, 2);
        int b = calculator.add(2, 3, 4);

        System.out.println("Jumlah penambahan a = " + a);
        System.out.println("Jumlah penambahan b = " +  b );


        // 2. runtime polymorphism (dynamic one)
        Animal myAnimal = new Dog(); // ini adalah upcasting
        // ini saat runtime akan dibuah ke methodnya dog
        myAnimal.makeSound();


        // dalam runtime polymorphism ini terdapat dua tipe lagi yakni upcasting dan downcasting
        // upcasting itu ketika referencenya dari child di ubah ke reference ke reference parentnya
        // downcasting itu ketika mengubah referecenya dari parent ke childnya (ini tidak aman)

        // 1. upcasting
        Dog myDog = new Dog();
        Animal myDogChange = myDog;
        myDogChange.makeSound();

        // 2. downcasting
        Animal myNewAnimal = new Dog();
        Dog myDogFromAniml = (Dog) myNewAnimal;
        myDogFromAniml.makeSound();
        // supaya lebih aman pakai instanceof tuk downcasting
        if (myNewAnimal instanceof Dog) {
            Dog dog2 = (Dog) myNewAnimal;
            dog2.makeSound();
        }

    }
}
