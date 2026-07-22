package jp.co.fullness.ec.frontend.presentation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.frontend.application.customer.CustomerRegisterService;
import jp.co.fullness.ec.frontend.config.CartConfig;

@WebMvcTest(CustomerRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CartConfig.class)
class CustomerRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerRegisterService customerRegisterService;

    /** 正しい入力値を param として組み立てるヘルパ。 */
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder validForm() {
        return post("/account/confirm")
                .param("name", "山田太郎")
                .param("nameKana", "ヤマダタロウ")
                .param("address1", "東京都1-1")
                .param("address2", "101号室")
                .param("phoneNumber", "090-1111-2222")
                .param("mailAddress", "yamada@example.com")
                .param("username", "yamada")
                .param("password", "pass1");
    }

    @Test
    void form_入力画面を表示する() throws Exception {
        mockMvc.perform(get("/account/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/form"));
    }

    @Test
    void confirm_正常入力なら確認画面へ() throws Exception {
        when(customerRegisterService.isMailAddressTaken("yamada@example.com")).thenReturn(false);
        when(customerRegisterService.isUsernameTaken("yamada")).thenReturn(false);

        mockMvc.perform(validForm())
                .andExpect(status().isOk())
                .andExpect(view().name("account/confirm"));
    }

    @Test
    void confirm_入力エラーなら入力画面へ戻る() throws Exception {
        mockMvc.perform(post("/account/confirm")
                        .param("name", "")               // 未入力
                        .param("nameKana", "ヤマダ")
                        .param("address1", "東京都1-1")
                        .param("phoneNumber", "090-1111-2222")
                        .param("mailAddress", "yamada@example.com")
                        .param("username", "yamada")
                        .param("password", "pass1"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/form"))
                .andExpect(model().attributeHasFieldErrors("customerRegisterForm", "name"));
    }

    @Test
    void confirm_メール重複なら入力画面へ戻る() throws Exception {
        when(customerRegisterService.isMailAddressTaken("yamada@example.com")).thenReturn(true);
        when(customerRegisterService.isUsernameTaken("yamada")).thenReturn(false);

        mockMvc.perform(validForm())
                .andExpect(status().isOk())
                .andExpect(view().name("account/form"))
                .andExpect(model().attributeHasFieldErrors("customerRegisterForm", "mailAddress"));
    }
}
