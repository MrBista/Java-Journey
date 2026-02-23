package com.bisma.foundation.anotation_reflection.anotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlank {
    boolean value() default true;
}
