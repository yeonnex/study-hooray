package com.chiko.studyhooray.main;

import com.chiko.studyhooray.account.AccountRepository;
import com.chiko.studyhooray.account.AccountService;
import com.chiko.studyhooray.model.SignUpForm;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;


    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }


    @Test
    @DisplayName("이메일로 로그인 성공")
    void login_with_email() throws Exception {
        signUp();
        mockMvc.perform(post("/login")
                        .param("username", "yeonnex@gmail.com")
                        .param("password", "12345678")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("yeonnex"));

    }

    @Test
    @DisplayName("닉네임으로 로그인 성공")
    void login_with_nickname() throws Exception {
        signUp();
        mockMvc.perform(post("/login")
                .param("username","yeonnex")
                .param("password", "12345678")
                .with(csrf())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"))
        .andExpect(authenticated().withUsername("yeonnex"));
    }

    @Test
    @DisplayName("로그인 실패")
    void loginFail() throws Exception {
        signUp();
        mockMvc.perform(post("/login")
                .param("username","yeonnex")
                .param("password", "wrong-password")
                .with(csrf())
        )
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그 아웃")
    void logout() throws Exception {
        signUp();
        mockMvc.perform(post("/logout")
                .with(csrf())
        )
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    private void signUp() {
        SignUpForm form = new SignUpForm("yeonnex", "yeonnex@gmail.com", "12345678");
        accountService.processNewAccount(form);
    }
}