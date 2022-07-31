package com.chiko.studyhooray.account;

import com.chiko.studyhooray.domain.Account;
import com.chiko.studyhooray.model.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
//        accountService.login(account); TODO 이 코드 지워야할것 같다. 회원가입 절차를 아직 다 complete 하게 거치지 못했는데, 시큐리티 컨텍스트에 벌써 넣으면 안된다고 생각함.
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

        redirectModel.addFlashAttribute("numberOfUser", accountRepository.count());
        redirectModel.addFlashAttribute("nickname", account.getNickname());
        return "redirect:/";
    }

}
