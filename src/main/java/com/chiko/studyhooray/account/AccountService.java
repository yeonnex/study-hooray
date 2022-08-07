package com.chiko.studyhooray.account;

import com.chiko.studyhooray.domain.Account;
import com.chiko.studyhooray.model.PasswordForm;
import com.chiko.studyhooray.model.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account account = saveNewAccount(signUpForm);
        sendSignUpConfirmEmail(account);
        return account;
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("Study Hooray, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                account.getNickname(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional
    public void completeSignUp(Account account) {
        account.setJoinedAt(LocalDateTime.now());
        account.setEmailVerified(true);
    }
<<<<<<< HEAD
=======

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) {
        Account account = findUser(emailOrNickname);
        return new UserAccount(account);
    }

    private Account findUser(String emailOrNickname) {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return account;
    }

    public void updateProfile(Account account, Profile profile) {
        account.setBio(profile.getBio());
        account.setUrl(profile.getUrl());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        // TODO 프로필 이미지
        accountRepository.save(account);
    }

    public void updatePassword(Account account, PasswordForm form) {
        String newPassword = passwordEncoder.encode(form.getNewPassword());
        account.setPassword(newPassword);

        accountRepository.save(account);
    }
>>>>>>> 0033a13 (패스워드 수정, 패스워드 수정 테스트)
}
