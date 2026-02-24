package com.bisma.foundation.jakson_learn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class LearnJakson {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testCreateObjectMapper() {

        Assertions.assertNotNull(MAPPER);
    }

    @Test
    void testSerializationJakson() throws JsonProcessingException {
        Person person = new Person("bisma", "jl merangin", 23);
        String resultStringValue = MAPPER.writeValueAsString(person);
        System.out.println(resultStringValue);
    }

    @Test
    void testDeserializtionJakson() throws JsonProcessingException {
        String json = """
                {"name":"bisma","address":"jl merangin","age":23}
                """;

        // cara 1 pakai type reference, cocok untuk balikan api
        Map<String, Object> result = MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
        });

        System.out.println(result.toString());

        // pakai class langsung
        Person personParse = MAPPER.readValue(json, Person.class);

        System.out.println(personParse.getName());
    }

}
