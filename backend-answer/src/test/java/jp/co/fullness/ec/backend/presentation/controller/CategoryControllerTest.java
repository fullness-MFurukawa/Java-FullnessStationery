package jp.co.fullness.ec.backend.presentation.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.category.CategoryRegisterService;
import jp.co.fullness.ec.backend.presentation.form.CategoryRegisterForm;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryRegisterService categoryRegisterService;

    @Test
    void form_入力画面を表示する() throws Exception {
        mockMvc.perform(get("/admin/category/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/form"));
    }

    @Test
    void confirm_入力エラーなら入力画面に戻す() throws Exception {
        // カテゴリ名を送らない → バリデーションエラー
        mockMvc.perform(post("/admin/category/confirm")
                        .sessionAttr("categoryRegisterForm", new CategoryRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/form"));
    }

    @Test
    void confirm_正常なら確認画面を表示する() throws Exception {
        mockMvc.perform(post("/admin/category/confirm")
                        .param("name", "新カテゴリ")
                        .sessionAttr("categoryRegisterForm", new CategoryRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/confirm"));
    }

    @Test
    void complete_登録して完了へリダイレクトする() throws Exception {
        mockMvc.perform(post("/admin/category/complete")
                        .param("name", "新カテゴリ")
                        .sessionAttr("categoryRegisterForm", new CategoryRegisterForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/category/complete"));

        verify(categoryRegisterService).register("新カテゴリ");
    }

    @Test
    void completeView_直接アクセスはメニューへリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/category/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void cancel_メニューへリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/category/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }
}
