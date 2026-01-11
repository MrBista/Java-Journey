package src.section.oop.fourprinciple.inheritance.variabelhiding;

public class Car extends Vehicle{
    public String name = "Car name";

    public Car() {
    }

    public Car(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getParentName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }



}
