package ua.team3.carsharingservice.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;
import ua.team3.carsharingservice.validation.FieldMatch;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object fieldVal = getFieldValue(value, field);
            Object fieldMatchVal = getFieldValue(value, fieldMatch);

            return Objects.equals(fieldVal, fieldMatchVal);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

    private Object getFieldValue(Object value, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = value.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(value);
    }
}
