package com.chiko.studyhooray.account;

import com.chiko.studyhooray.model.SignUpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("request", new SignUpRequest());
        return "account/sign-up";
    }
}
