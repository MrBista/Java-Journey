package com.bista.foundation;

public class PersonService {
    PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    public Person getByName(String name) {
        Person person = personRepository.selectByName(name);

        if (person == null) {
            throw new IllegalArgumentException("Person tidak ditemukan");
        }

        return person;
    }

    public void insertPerson(Person person) {
        Person personTobeInsert = new Person(person.getName(), person.getAge());

        personRepository.insert(personTobeInsert);

    }
}
