package fr.ul.miage.bank.entities;

import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Service
public class AccountValidator {

    private Validator validator;

    AccountValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(AccountInput account) {
        Set<ConstraintViolation<AccountInput>> violations = validator.validate(account);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
