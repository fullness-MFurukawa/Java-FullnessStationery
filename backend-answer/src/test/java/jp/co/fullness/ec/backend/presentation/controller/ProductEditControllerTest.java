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
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.product.ProductUpdateService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.model.ProductStock;
import jp.co.fullness.ec.backend.presentation.form.ProductEditForm;

@WebMvcTest(ProductEditController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductEditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductUpdateService productUpdateService;

    private Product fullProduct() {
        return Product.builder()
                .id(1)
                .name("水性ボールペン(黒)")
                .price(120)
                .imageUrl("products/1.png")
                .category(ProductCategory.builder().id(1).name("文房具").build())
                .stock(ProductStock.builder().quantity(10).build())
                .build();
    }

    private List<ProductCategory> categories() {
        return List.of(ProductCategory.builder().id(1).name("文房具").build());
    }

    @Test
    void edit_対象商品をプリセットして入力画面を表示する() throws Exception {
        when(productUpdateService.findById(1)).thenReturn(Optional.of(fullProduct()));
        when(productUpdateService.findAllCategories()).thenReturn(categories());
        when(productUpdateService.generateImageUrl(any())).thenReturn("https://signed-url");

        mockMvc.perform(get("/admin/product/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/edit"))
                .andExpect(model().attributeExists("productEditForm", "categories", "imageUrl"));
    }

    @Test
    void edit_対象が存在しなければ商品検索へリダイレクトする() throws Exception {
        when(productUpdateService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/product/edit/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }

    @Test
    void confirm_正常なら確認画面を表示する() throws Exception {
        when(productUpdateService.findAllCategories()).thenReturn(categories());
        when(productUpdateService.generateImageUrl(any())).thenReturn("https://signed-url");

        mockMvc.perform(post("/admin/product/edit/confirm")
                        .param("productId", "1")
                        .param("name", "更新ペン")
                        .param("price", "300")
                        .param("stockQuantity", "20")
                        .param("categoryId", "1")
                        .param("imageKey", "products/1.png")
                        .sessionAttr("productEditForm", new ProductEditForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/edit_confirm"))
                .andExpect(model().attributeExists("categoryName", "imageUrl"));
    }

    @Test
    void confirm_入力エラーなら入力画面に戻す() throws Exception {
        when(productUpdateService.findAllCategories()).thenReturn(categories());
        when(productUpdateService.generateImageUrl(any())).thenReturn("https://signed-url");

        // name / price 未指定 → バリデーションエラー
        mockMvc.perform(post("/admin/product/edit/confirm")
                        .param("productId", "1")
                        .param("imageKey", "products/1.png")
                        .sessionAttr("productEditForm", new ProductEditForm()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/edit"));
    }

    @Test
    void complete_更新して完了へリダイレクトする() throws Exception {
        mockMvc.perform(post("/admin/product/edit/complete")
                        .param("productId", "1")
                        .param("name", "更新ペン")
                        .param("price", "300")
                        .param("stockQuantity", "20")
                        .param("categoryId", "1")
                        .param("imageKey", "products/1.png")
                        .sessionAttr("productEditForm", new ProductEditForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product/edit/complete"));

        verify(productUpdateService).update(any());
    }

    @Test
    void complete_業務エラーなら入力画面へ戻す() throws Exception {
        doThrow(new DomainException("対象の商品が存在しません"))
                .when(productUpdateService).update(any());

        mockMvc.perform(post("/admin/product/edit/complete")
                        .param("productId", "1")
                        .param("name", "更新ペン")
                        .param("price", "300")
                        .param("stockQuantity", "20")
                        .param("categoryId", "1")
                        .param("imageKey", "products/1.png")
                        .sessionAttr("productEditForm", new ProductEditForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product/edit/1"));
    }

    @Test
    void completeView_直接アクセスは商品検索へリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/product/edit/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }

    @Test
    void cancel_商品検索へリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/product/edit/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }
}