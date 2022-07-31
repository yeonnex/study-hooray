package com.chiko.studyhooray.account;

import com.chiko.studyhooray.domain.Account;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    JavaMailSender javaMailSender;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 화면 보이는지 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
        ;
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "kk")
                        .param("email", "e!mail")
                        .param("password", "pas~")
                        .with(csrf()) // 시큐리티
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
        ;
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 정상")
    void signUpSubmit_with_normal_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "pikachu")
                        .param("email", "syhoneyjam@gmail.com")
                        .param("password", "abcd123@")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
        ;
        Account account = accountRepository.findByEmail("syhoneyjam@gmail.com");


        assertThat(accountRepository.existsByEmail("syhoneyjam@gmail.com")).isTrue();
        assertThat(account.getPassword()).isNotEqualTo("abcd123@");
        assertThat(account.getEmailCheckToken()).isNotNull();
        then(javaMailSender).should().send(ArgumentMatchers.any(SimpleMailMessage.class));

    }

    @Test
    @DisplayName("인증 메일 확인 - 인증 메일 체크 정상")
    void checkEmailToken() throws Exception {
        Account account = generateAndSaveUser();
        mockMvc.perform(get("/check-email-token")
                .param("token", account.getEmailCheckToken())
                .param("email", account.getEmail())
        )
                .andExpect(view().name("redirect:/"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(flash().attributeExists("numberOfUser"))
                .andExpect(flash().attributeExists("nickname"))
                .andExpect(authenticated())
        ;

        Account savedUser = accountRepository.findByEmail(account.getEmail());
        assertThat(savedUser.isEmailVerified()).isTrue();
    }

    @Test
    @DisplayName("인증 메일 확인 - 인증 메일 체크 비정상")
    void checkEmailToken_with_wrong_input() throws Exception {
        Account account = generateAndSaveUser();

        mockMvc.perform(get("/check-email-token")
                .param("token", "asoefjlsadjf")
                .param("email", account.getEmail())
        )
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated())
        ;

        Account savedUser = accountRepository.findByEmail(account.getEmail());
        assertThat(savedUser.isEmailVerified()).isFalse();

    }


    private Account generateAndSaveUser() {
        Account account = Account.builder()
                .email("testuser@gmail.com")
                .nickname("testuser")
                .password(passwordEncoder.encode("123456abc@"))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        account.generateEmailCheckToken();

        return accountRepository.save(account);
    }
}