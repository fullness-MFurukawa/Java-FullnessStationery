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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.backend.application.product.ProductDeleteService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.model.ProductStock;

@WebMvcTest(ProductDeleteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductDeleteService productDeleteService;

    /** delete_confirm.html が参照する項目を満たした商品。 */
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

    @Test
    void confirm_削除確認画面を表示する() throws Exception {
        when(productDeleteService.findById(1)).thenReturn(fullProduct());
        when(productDeleteService.generateImageUrl(any())).thenReturn("https://signed-url");

        mockMvc.perform(get("/admin/product/delete").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/delete_confirm"))
                .andExpect(model().attributeExists("product", "imageUrl", "productDeleteForm"));
    }

    @Test
    void confirm_id未指定なら検索へリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/product/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }

    @Test
    void delete_論理削除して完了へリダイレクトする() throws Exception {
        when(productDeleteService.findById(1)).thenReturn(fullProduct());

        mockMvc.perform(post("/admin/product/delete").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product/delete/complete"));

        verify(productDeleteService).delete(1);
    }

    @Test
    void complete_直接アクセスは検索へリダイレクトする() throws Exception {
        mockMvc.perform(get("/admin/product/delete/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }

    @Test
    void confirm_存在しない商品はエラー画面を表示する() throws Exception {
        when(productDeleteService.findById(99))
                .thenThrow(new DomainException("対象の商品が存在しません。"));

        mockMvc.perform(get("/admin/product/delete").param("id", "99"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/error"));
    }
}