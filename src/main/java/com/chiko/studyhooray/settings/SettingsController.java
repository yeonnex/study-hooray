package com.chiko.studyhooray.settings;

import com.chiko.studyhooray.account.AccountService;
import com.chiko.studyhooray.account.CurrentUser;
import com.chiko.studyhooray.domain.Account;
import com.chiko.studyhooray.model.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {
    private final AccountService accountService;
    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/settings/password";

    @GetMapping(SETTINGS_PROFILE_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model){
        model.addAttribute("account", account);
        model.addAttribute("profile", new Profile(account));

        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(@CurrentUser Account account,
                                @Valid @ModelAttribute Profile profile,
                                Errors errors,
                                Model model,
                                RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다");
        return "redirect:/" + SETTINGS_PROFILE_VIEW_NAME;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("passwordForm", new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(@CurrentUser Account account,
                                 @Valid @ModelAttribute PasswordForm form,
                                 Errors errors,
                                 Model model,
                                 RedirectAttributes attributes) {
        // validate
        new PasswordFormValidator().validate(form,errors);

        if (errors.hasErrors()) {
            model.addAttribute("passwordForm", form);
            model.addAttribute("account", account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }
        accountService.updatePassword(account, form) ;
        attributes.addFlashAttribute("message", "패스워드를 성공적으로 변경하였습니다");
        attributes.addFlashAttribute("account", account);
        return "redirect:/" + SETTINGS_PASSWORD_VIEW_NAME;
    }
}
