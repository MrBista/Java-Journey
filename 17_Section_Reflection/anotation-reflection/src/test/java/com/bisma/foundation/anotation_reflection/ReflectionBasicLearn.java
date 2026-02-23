package com.bisma.foundation.anotation_reflection;

import com.bisma.foundation.anotation_reflection.anotation.NotBlank;
import com.bisma.foundation.anotation_reflection.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionBasicLearn {

    @Test
    void testClass() {
        Class<UserDto> personClass1 = UserDto.class;

        System.out.println(Arrays.toString(personClass1.getInterfaces()));
        System.out.println(personClass1.getClasses());
        System.out.println(personClass1.getClass());
        System.out.println(personClass1.getName());
        System.out.println(personClass1.getSuperclass());
    }

    @Test
    void testFieldReflection() {
        Class<UserDto> userClass = UserDto.class;
        Field[] fields = userClass.getFields();
        Field[] fieldsDeclare = userClass.getDeclaredFields();
        System.out.println(Arrays.toString(fields));
        System.out.println(Arrays.toString(fieldsDeclare));
    }

    @Test
    void testGetFromFieldUsingReflection() throws NoSuchFieldException, IllegalAccessException {
        UserDto user = new UserDto("gustibisman", "gustibimsan@mail.com", "gustibimsan");


        Class<UserDto> userDtoClass = UserDto.class;

        Field usernameFiled = userDtoClass.getDeclaredField("username");
        usernameFiled.setAccessible(true);

        Object usernameFiledValue = usernameFiled.get(user);

        System.out.println("username field: " + usernameFiledValue);
    }

    @Test
    void testSetFromFieldUseingReflection() throws NoSuchFieldException, IllegalAccessException {
        UserDto user = new UserDto("gustibisman", "gustibimsan", "gustibimsan");

        Class<UserDto> userDtoClass = UserDto.class;

        Field emailFiled = userDtoClass.getDeclaredField("email");

        emailFiled.setAccessible(true);

        Object emailFieldValue = emailFiled.get(user);

        if (emailFieldValue instanceof String val) {
           if(!val.contains("@")) {
               emailFiled.set(user, emailFieldValue + "@mail.com");
           }
        }
        System.out.println(user.getEmmil());

    }

    @Test
    void testMethodReflection() {
        UserDto user = new UserDto("gustibisman", "gustibimsan", "gustibimsan");

        Class<UserDto> userDtoClass = UserDto.class;

        Method[] methods = userDtoClass.getDeclaredMethods();

        for (Method method : methods) {
            System.out.println(method.getName());
            System.out.println(method.getReturnType());
            System.out.println(Arrays.toString(method.getParameters()));
            System.out.println(Arrays.toString(method.getExceptionTypes()));

            System.out.println("========================================");
        }

    }

    @Test
    void testInvokeMethodReflection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UserDto user = new UserDto("gustibisman", "gustibimsan", "gustibimsan");

        Class<UserDto> userDtoClass = UserDto.class;


        Method getUsername = userDtoClass.getDeclaredMethod("getUsername");

        String value = (String) getUsername.invoke(user);
        System.out.println("Value: " + value);
    }

    @Test
    void testAnotationReflection() throws NoSuchFieldException {

        UserDto user = new UserDto("gustibisman", "gustibimsan", "gustibimsan");


        Class<UserDto> userDtoClass = UserDto.class;

        Field usernameFiled = userDtoClass.getDeclaredField("username");
        Annotation[] anotations = userDtoClass.getDeclaredAnnotations();


        boolean isAnotationPresent = userDtoClass.isAnnotationPresent(NotBlank.class);
        System.out.println(Arrays.toString(anotations));
        System.out.println(isAnotationPresent);
        System.out.println(usernameFiled.isAnnotationPresent(NotBlank.class));

    }

    @Test
    void testAnnotationImplementationValidator() throws IllegalAccessException {
        UserDto user = new UserDto("gustibisman", "", "gustibimsan");

        Validator.validateNotBlank(user);
    }
}
