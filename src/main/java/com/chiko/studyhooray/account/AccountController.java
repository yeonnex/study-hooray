package com.chiko.studyhooray.account;

import com.chiko.studyhooray.domain.Account;
import com.chiko.studyhooray.model.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractXlsView;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Validated SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        signUpFormValidator.validate(signUpForm, errors);

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    /**
     *
     * @param token
     * @param email
     * @param redirectModel
     * @param defaultModel
     * @return
     */
    @GetMapping("/check-email-token")
    public String checkEmailToken(@RequestParam String token,
                                  @RequestParam String email,
                                  RedirectAttributes redirectModel,
                                  Model defaultModel) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if (account == null) {
            defaultModel.addAttribute("error", "wrong.email");
            return view;
        }
        if (!account.isValidToken(token)) {
            defaultModel.addAttribute("error", "wrong.email");
            return view;
        }

        accountService.completeSignUp(account);
        accountService.login(account);

        defaultModel.addAttribute("numberOfUser", accountRepository.count());
        defaultModel.addAttribute("nickname", account.getNickname());

        return view;
    }

    @GetMapping("/check-email")
    public String alertEmail(@CurrentUser Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }
        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String profileView(@PathVariable String nickname, Model model, @CurrentUser Account account) throws IllegalAccessException {
        Account byNickname = accountRepository.findByNickname(nickname);
        if (byNickname == null) {
            throw new IllegalAccessException(nickname);
        }
        model.addAttribute("account", account);
        model.addAttribute("isOwner", byNickname.equals(account));

        return "account/profile";
    }

}
