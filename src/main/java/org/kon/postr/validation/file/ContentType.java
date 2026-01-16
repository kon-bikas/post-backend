package org.kon.postr.validation.file;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = { FileContentTypeValidator.class }
)
public @interface ContentType {
    String[] types() default {};

    String message() default "File's content type is not supported";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
