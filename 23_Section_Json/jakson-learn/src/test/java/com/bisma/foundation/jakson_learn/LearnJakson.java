package com.bisma.foundation.jakson_learn;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LearnJakson {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testCreateObjectMapper() {

        Assertions.assertNotNull(MAPPER);
    }


}
