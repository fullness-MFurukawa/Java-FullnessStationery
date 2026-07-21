package jp.co.fullness.ec.backend.presentation.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.account.AccountRegisterService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.presentation.form.AccountRegisterForm;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountRegisterService accountRegisterService;

    private List<Employee> employees() {
        return List.of(Employee.builder().id(2).name("フルネス花子").build());
    }

    @Test
    void form_入力画面を表示する() throws Exception {
        when(accountRegisterService.findRegisterableEmployees()).thenReturn(employees());

        mockMvc.perform(get("/admin/account/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/account/form"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    void confirm_入力エラーなら入力画面に戻す() throws Exception {
        when(accountRegisterService.findRegisterableEmployees()).thenReturn(employees());

        // 必須項目を送らない → バリデーションエラー
        mockMvc.perform(post("/admin/account/confirm")
                        .sessionAttr("accountRegisterForm", new AccountRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/account/form"));
    }

    @Test
    void confirm_正常なら確認画面を表示する() throws Exception {
        when(accountRegisterService.findRegisterableEmployees()).thenReturn(employees());
        when(accountRegisterService.isAccountNameDuplicated("hanako")).thenReturn(false);

        mockMvc.perform(post("/admin/account/confirm")
                        .param("employeeId", "2")
                        .param("accountName", "hanako")
                        .param("password", "fullness123")
                        .param("confirmPassword", "fullness123")
                        .sessionAttr("accountRegisterForm", new AccountRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/account/confirm"))
                .andExpect(model().attributeExists("employeeName"));
    }

    @Test
    void confirm_アカウント名が重複なら入力画面に戻す() throws Exception {
        when(accountRegisterService.findRegisterableEmployees()).thenReturn(employees());
        when(accountRegisterService.isAccountNameDuplicated("hanako")).thenReturn(true);

        mockMvc.perform(post("/admin/account/confirm")
                        .param("employeeId", "2")
                        .param("accountName", "hanako")
                        .param("password", "fullness123")
                        .param("confirmPassword", "fullness123")
                        .sessionAttr("accountRegisterForm", new AccountRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/account/form"));
    }

    @Test
    void complete_登録成功なら完了へリダイレクトする() throws Exception {
        when(accountRegisterService.findRegisterableEmployees()).thenReturn(employees());

        mockMvc.perform(post("/admin/account/complete")
                        .param("employeeId", "2")
                        .param("accountName", "hanako")
                        .param("password", "fullness123")
                        .sessionAttr("accountRegisterForm", new AccountRegisterForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/account/complete"));
    }

    @Test
    void complete_業務エラーなら入力へ戻す() throws Exception {
        when(accountRegisterService.findRegisterableEmployees()).thenReturn(employees());
        doThrow(new DomainException("このアカウント名は既に使用されています"))
                .when(accountRegisterService).register(anyInt(), anyString(), anyString());

        mockMvc.perform(post("/admin/account/complete")
                        .param("employeeId", "2")
                        .param("accountName", "hanako")
                        .param("password", "fullness123")
                        .sessionAttr("accountRegisterForm", new AccountRegisterForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/account/form"));
    }

    @Test
    void completeView_直接アクセスはメニューへリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/account/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void cancel_メニューへリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/account/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }
}