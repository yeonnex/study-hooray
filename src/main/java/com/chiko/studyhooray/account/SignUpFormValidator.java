package com.chiko.studyhooray.account;

import com.chiko.studyhooray.model.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm form = (SignUpForm) target;
        if (accountRepository.existsByEmail(form.getEmail())) {
            errors.rejectValue("email", "duplicated email", "이미 사용중인 이메일입니다");
        }
        if (accountRepository.existsByNickname(form.getNickname())) {
            errors.rejectValue("nickname", "duplicated nickname", "이미 사용중인 닉네임입니다.");
        }
    }
}
