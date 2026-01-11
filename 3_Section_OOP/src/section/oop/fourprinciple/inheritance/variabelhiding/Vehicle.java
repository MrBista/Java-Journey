package src.section.oop.fourprinciple.inheritance.variabelhiding;

public class Vehicle {
    public String name = "Vehicle name";
    public String type = "Type parent";


    public Vehicle() {
    }

    public Vehicle(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String move() {
        return "prety much fast";
    }
    public Boolean isChiper() {
        return false;
    }
}
