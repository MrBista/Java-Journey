package src.section.oop.fourprinciple.inheritance;

public class Animal {

    private String name;
    private String hasLeg;
    private String type;
    // composition has a relationsip
    private Leg leg;

    public Animal() {
    }

    public Animal(String name, String hasLeg, String type) {
        this.name = name;
        this.hasLeg = hasLeg;
        this.type = type;
    }


    public void eatSomething() {
        System.out.println("yum yum delecious");
    }

    public void sayHai() {
        System.out.println(this.name + " say hello to everyone");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHasLeg() {
        return hasLeg;
    }

    public void setHasLeg(String hasLeg) {
        this.hasLeg = hasLeg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Leg getLeg() {
        return leg;
    }

    public void setLeg(Leg leg) {
        this.leg = leg;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", hasLeg='" + hasLeg + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
