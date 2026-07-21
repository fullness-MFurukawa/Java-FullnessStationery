package jp.co.fullness.ec.backend.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.security.LoginAttemptService;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private LoginAttemptService loginAttemptService;

    @Test
    void showLogin_ログイン画面を表示する() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @Test
    void login_入力エラーなら画面に戻す() throws Exception {
        // username / password 未入力 → バリデーションエラー
        mockMvc.perform(post("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @Test
    void login_ロック中ならエラーメッセージを表示する() throws Exception {
        when(loginAttemptService.isLocked("fullness")).thenReturn(true);

        mockMvc.perform(post("/admin/login")
                        .param("username", "fullness")
                        .param("password", "fullness123"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void login_認証成功ならメニューへリダイレクトし失敗記録をクリアする() throws Exception {
        when(loginAttemptService.isLocked("fullness")).thenReturn(false);
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
                "fullness", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        mockMvc.perform(post("/admin/login")
                        .param("username", "fullness")
                        .param("password", "fullness123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(loginAttemptService).reset("fullness");
    }

    @Test
    void login_認証失敗ならエラーメッセージを表示し失敗を記録する() throws Exception {
        when(loginAttemptService.isLocked("fullness")).thenReturn(false);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        mockMvc.perform(post("/admin/login")
                        .param("username", "fullness")
                        .param("password", "wrong-pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(loginAttemptService).recordFailure("fullness");
    }
}
