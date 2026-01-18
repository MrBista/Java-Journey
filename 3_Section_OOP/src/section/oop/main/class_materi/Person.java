package src.section.oop.main.class_materi;

public class Person {
    private String name;
    private int age;
    private String education;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEducation() {
        return education;
    }

    public Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.education = builder.education;
    }

    public static class Builder {

        private String name;
        private int age;
        private String education;

        public Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder education(String education) {
            this.education = education;
            return this;
        }


        public Person build() {
            return new Person(this);
        }
    }
}
