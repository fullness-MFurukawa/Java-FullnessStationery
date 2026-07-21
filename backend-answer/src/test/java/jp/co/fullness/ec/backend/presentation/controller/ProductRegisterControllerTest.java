package jp.co.fullness.ec.backend.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.product.ProductCreateService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.presentation.form.ProductRegisterForm;

@WebMvcTest(ProductRegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCreateService productCreateService;

    private List<ProductCategory> categories() {
        return List.of(ProductCategory.builder().id(1).name("文房具").build());
    }

    @Test
    void add_入力画面を表示する() throws Exception {
        when(productCreateService.findAllCategories()).thenReturn(categories());

        mockMvc.perform(get("/admin/product/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/register"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void confirm_正常なら確認画面を表示する() throws Exception {
        when(productCreateService.findAllCategories()).thenReturn(categories());
        when(productCreateService.generateImageUrl(any())).thenReturn("https://signed-url");

        // imageKey を持たせることで画像必須チェックを通す(確認から戻った状態を想定)
        mockMvc.perform(post("/admin/product/confirm")
                        .param("name", "新ペン")
                        .param("price", "500")
                        .param("stockQuantity", "100")
                        .param("categoryId", "1")
                        .param("imageKey", "products/new.png")
                        .sessionAttr("productRegisterForm", new ProductRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/register_confirm"))
                .andExpect(model().attributeExists("categoryName", "imageUrl"));
    }

    @Test
    void confirm_入力エラーなら入力画面に戻す() throws Exception {
        when(productCreateService.findAllCategories()).thenReturn(categories());

        // 必須項目も画像も無し → バリデーションエラー
        mockMvc.perform(post("/admin/product/confirm")
                        .sessionAttr("productRegisterForm", new ProductRegisterForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/register"));
    }

    @Test
    void complete_登録して完了へリダイレクトする() throws Exception {
        mockMvc.perform(post("/admin/product/complete")
                        .param("name", "新ペン")
                        .param("price", "500")
                        .param("stockQuantity", "100")
                        .param("categoryId", "1")
                        .param("imageKey", "products/new.png")
                        .sessionAttr("productRegisterForm", new ProductRegisterForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product/complete"));

        verify(productCreateService).register(any());
    }

    @Test
    void complete_業務エラーなら入力へ戻す() throws Exception {
        doThrow(new DomainException("選択されたカテゴリが存在しません"))
                .when(productCreateService).register(any());

        mockMvc.perform(post("/admin/product/complete")
                        .param("name", "新ペン")
                        .param("price", "500")
                        .param("stockQuantity", "100")
                        .param("categoryId", "1")
                        .param("imageKey", "products/new.png")
                        .sessionAttr("productRegisterForm", new ProductRegisterForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product/add"));
    }

    @Test
    void completeView_直接アクセスは商品検索へリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/product/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }

    @Test
    void cancel_商品検索へリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/product/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }
}