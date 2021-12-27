package fr.ul.miage.bank.entities;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

public class CardValidator {

    private Validator validator;

    CardValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(CardInput card) {
        Set<ConstraintViolation<CardInput>> violations = validator.validate(card);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
