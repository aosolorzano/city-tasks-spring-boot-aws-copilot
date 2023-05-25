package com.hiperium.city.tasks.api.utils;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDto;
import com.hiperium.city.tasks.api.dto.TaskDto;
import com.hiperium.city.tasks.api.exception.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

public final class BeanValidationUtil {

    private BeanValidationUtil() {
        // Empty constructor.
    }

    public static void validateBean(TaskDto taskDto) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeanValidationUtil::throwValidationTaskDtoException);
            }
        }
    }

    public static void validateBean(TaskCriteriaDto taskCriteriaDto) {
        try (ValidatorFactory factory = buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<TaskCriteriaDto>> violations = validator.validate(taskCriteriaDto);
            if (!violations.isEmpty()) {
                violations.stream()
                        .findFirst()
                        .ifPresent(BeanValidationUtil::throwValidationTaskCriteriaDtoException);
            }
        }
    }

    private static void throwValidationTaskDtoException(ConstraintViolation<TaskDto> violation) {
        throw new ValidationException(violation.getMessage());
    }

    private static void throwValidationTaskCriteriaDtoException(ConstraintViolation<TaskCriteriaDto> violation) {
        throw new ValidationException(violation.getMessage());
    }
}
