package src.section.oop.fourprinciple.inheritance;

public class CompositionConcept {
    public static void main(String[] args) {
        /*
            Composition konsepnya hampir sama dengan inheritance, cuma inheritance ini dia tight compling, yg mana child
            hanya bisa punya satu parent serta kalau ada perubahan di parent bisa merusak childnya
            composition ini relasinya adalah HAS-A
            kalau Inheritance ini IS-A
         */


        Animal dogy = new Dog();
        dogy.setLeg(new Leg(1));
        System.out.println(dogy.getLeg().getNumberOfLegs());


    }
}
