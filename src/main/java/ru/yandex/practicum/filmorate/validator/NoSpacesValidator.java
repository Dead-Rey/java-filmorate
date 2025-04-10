package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpacesValidator implements ConstraintValidator<NoSpaces, String> {


    @Override
    public void initialize(NoSpaces constraintAnnotation) {
    }

    // Метод для валидации строки
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }
        return !value.contains(" ");
    }
}