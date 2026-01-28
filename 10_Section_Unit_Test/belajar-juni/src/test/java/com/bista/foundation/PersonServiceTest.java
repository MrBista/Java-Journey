package com.bista.foundation;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static  org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    private PersonService personService;

    @BeforeEach
    public void setUp() {
        personService = new PersonService(personRepository);
    }

    @Test
    public void getNameTest() {
        when(personRepository.selectByName("bisma")).thenReturn(new Person("bisma", 23));

        var person = personService.getByName("bisma");
        assertEquals("bisma", person.getName());

    }

    @Test
    public void isnertPeronTest() {
        Person person = new Person("Bismen", 23);
        personService.insertPerson(person);
        assertEquals("Bismen", person.getName());
        assertEquals(23, person.getAge());

        // digunakan untuk memastikan bahwa function itu ada di call
        verify(personRepository, times(1))
                .insert(person);
    }
}
