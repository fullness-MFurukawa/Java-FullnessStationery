package jp.co.fullness.ec.backend.presentation.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.security.LoginUserDetails;
import jp.co.fullness.ec.backend.domain.model.Employee;
import jp.co.fullness.ec.backend.domain.model.EmployeeAccount;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void menu_ログインユーザ名を表示する() throws Exception {
        // @AuthenticationPrincipal で受け取る LoginUserDetails を用意
        Employee employee = Employee.builder().id(1).name("フルネス太郎").build();
        EmployeeAccount account = EmployeeAccount.builder()
                .id(1).name("fullness").password("hashed").employee(employee).build();
        LoginUserDetails principal = new LoginUserDetails(account);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());

        mockMvc.perform(get("/admin").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/menu"))
                .andExpect(model().attributeExists("displayName"));
    }
}