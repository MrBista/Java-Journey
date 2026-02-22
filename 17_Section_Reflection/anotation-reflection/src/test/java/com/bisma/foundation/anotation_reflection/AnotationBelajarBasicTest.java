package com.bisma.foundation.anotation_reflection;

import com.bisma.foundation.anotation_reflection.dto.UserDto;
import org.junit.jupiter.api.Test;

public class AnotationBelajarBasicTest {

    @Test
    void testBasicAnotation() {
        UserDto userDto = new UserDto();
        System.out.println("UserDtoValue: " + userDto.toString());
        // basicly anotation itu hanya metadata yg seperti label yg bisa di tempel di class, field, atau method
        // yg membuat anotation ini superpower adalah reflection
        // kita bisa gunakan untuk cross cutting concern

    }
}
