package src.section.oop.main.class_materi;

public class Company {
    private String companyName;

    public Company() {
    }

    public Company(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public class Employee {
        private String name;

        public Employee() {
        }

        public Employee(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String nameOfCompany() {
            return companyName;
        }
    }


}
