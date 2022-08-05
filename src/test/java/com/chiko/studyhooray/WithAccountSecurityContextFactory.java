package com.chiko.studyhooray;

import com.chiko.studyhooray.account.AccountService;
import com.chiko.studyhooray.account.UserAccount;
import com.chiko.studyhooray.domain.Account;
import com.chiko.studyhooray.model.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccountAnnotation) {
        String nickname = withAccountAnnotation.value();

        SignUpForm form = new SignUpForm();
        form.setNickname(nickname);
        form.setEmail(nickname + "@gmail.com");
        form.setPassword("12345678");

        Account account = accountService.processNewAccount(form);
        accountService.login(account);

        UserAccount principal = (UserAccount) accountService.loadUserByUsername(nickname);
        Account principalAccount = principal.getAccount();
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalAccount, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        return context;
    }
}
