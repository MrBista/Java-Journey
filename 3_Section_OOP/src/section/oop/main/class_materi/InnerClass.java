package src.section.oop.main.class_materi;

public class InnerClass {
    public static void main(String[] args) {
        Company cartenz = new Company("Cartenz Technology Indonesia");

        Company.Employee employeeOfCartenz = cartenz.new Employee("Bisma");

        System.out.println("Company of : " + employeeOfCartenz.nameOfCompany() + " name of employe: " + employeeOfCartenz.getName());

        // biasanya inner calss ini digunakan tuk builder pattern

        Person bismaBuilder = new Person.Builder()
                .age(23)
                .name("Bismen")
                .education("High School todhler")
                .build();


        System.out.println("name: " + bismaBuilder.getName() + " age: " + bismaBuilder.getAge());

    }
}
