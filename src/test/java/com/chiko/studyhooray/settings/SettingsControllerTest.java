package com.chiko.studyhooray.settings;

import com.chiko.studyhooray.WithAccount;
import com.chiko.studyhooray.account.AccountRepository;
import com.chiko.studyhooray.account.AccountService;
import com.chiko.studyhooray.domain.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder encoder;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("seoyeon")
    @DisplayName("프로필 수정 폼 보여주기")
    @Test
    void profileUpdateForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
        ;
    }

    @WithAccount("seoyeon")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));
        Account seoyeon = accountRepository.findByNickname("seoyeon");

        assertEquals(bio, seoyeon.getBio());

    }

    @WithAccount("seoyeon")
    @DisplayName("프로필 수정하기 - 입력값 비정상")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 너무나도 길게 소개를 수정하는 경우";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("seoyeon")
    @DisplayName("패스워드 수정 폼 보여주기")
    void changePasswordForm() {

    }

    @WithAccount("seoyeon")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    void changePassword() throws Exception {
        String newPassword = "chikorita123!";

        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm",newPassword)
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"))
        ;

        String updatedPassword = accountRepository.findByNickname("seoyeon").getPassword();

        assertTrue(encoder.matches(newPassword, updatedPassword));
    }

    @WithAccount("seoyeon")
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @Test
    void changePassword_Error() throws Exception {
        String newPassword = "imShort";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("password", newPassword)
                .param("checkPassword", newPassword)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME));
    }

}