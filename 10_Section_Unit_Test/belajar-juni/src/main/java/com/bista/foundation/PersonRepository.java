package com.bista.foundation;

public interface PersonRepository {
    Person selectByName(String name);
    void insert(Person person);
}
