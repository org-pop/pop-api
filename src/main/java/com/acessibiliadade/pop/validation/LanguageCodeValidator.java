package com.acessibiliadade.pop.validation;

import com.acessibiliadade.pop.enums.SupportedLanguageCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LanguageCodeValidator implements ConstraintValidator<ValidLanguageCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // combine com @NotNull/@NotBlank se o campo virar obrigatório
        return SupportedLanguageCode.isSupported(value);
    }
}
