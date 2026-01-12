package src.section.oop.fourprinciple.polymorphism;

// ini adalah polymorphism tipe compile time, jadi di ubah bentuknya saat di compile
public class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    int add (int a, int b, int c) {
        return a + b + c;
    }

    double add (double a, double b) {
        return a + b;
    }
}
