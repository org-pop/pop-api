package com.acessibiliadade.pop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LanguageCodeValidator.class)
public @interface ValidLanguageCode {
    String message() default "Idioma não suportado. Consulte GET /api/accessibility/languages para a lista válida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
