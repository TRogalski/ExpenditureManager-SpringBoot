package com.my.expenditure.validator;

import com.my.expenditure.model.UserDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordCheckValidator implements ConstraintValidator<PasswordCheck, Object> {

    @Override
    public void initialize(PasswordCheck passwordCheck) {
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        UserDto user = (UserDto) object;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}