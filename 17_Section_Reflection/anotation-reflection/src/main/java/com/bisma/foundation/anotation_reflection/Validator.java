package com.bisma.foundation.anotation_reflection;

import com.bisma.foundation.anotation_reflection.anotation.NotBlank;

import java.lang.reflect.Field;

public class Validator {

    public static void validateNotBlank(Object obj) throws IllegalAccessException {
        Class<?> aClass = obj.getClass();
        for(Field filed : aClass.getDeclaredFields()) {
            // kalau anotation ada cek apakah valuenya true atau false
            if (filed.isAnnotationPresent(NotBlank.class)) {
                filed.setAccessible(true);
                NotBlank notBlank = filed.getAnnotation(NotBlank.class);
                // kalau required maka cek apakah dia kosong atau ga value field-nya
                if (notBlank.value()) {
                    String valObj = (String) filed.get(obj);
                    valObj = valObj.trim();
                    if (valObj.isBlank()) {
                        throw new RuntimeException("Field: " + filed.getName() + " is required");
                    }
                }
            }
        }
    }
}
