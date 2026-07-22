package jp.co.fullness.ec.frontend.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jp.co.fullness.ec.frontend.application.product.ProductSearchService;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.model.ProductCategory;
import jp.co.fullness.ec.frontend.domain.model.ProductStock;

import org.springframework.context.annotation.Import;
import jp.co.fullness.ec.frontend.config.CartConfig;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CartConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductSearchService productSearchService;

    @Test
    void search_商品一覧を表示する() throws Exception {
        Product product = Product.builder()
                .id(1).name("テスト商品").price(120).imageUrl("products/1.png").build();

        when(productSearchService.searchByCategory(any())).thenReturn(List.of(product));
        when(productSearchService.findAllCategories())
                .thenReturn(List.of(ProductCategory.builder().id(1).name("文房具").build()));
        when(productSearchService.generateImageUrl(any())).thenReturn("https://signed-url");

        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/search"))
                .andExpect(model().attributeExists("products", "categories", "imageUrls"));
    }

    @Test
    void search_0件なら該当なしメッセージ() throws Exception {
        when(productSearchService.searchByCategory(any())).thenReturn(List.of());
        when(productSearchService.findAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("message", "該当する商品が見つかりませんでした"));
    }

    @Test
    void detail_商品詳細を表示する() throws Exception {
        Product product = Product.builder()
                .id(1).name("テスト商品").price(120).imageUrl("products/1.png")
                .stock(ProductStock.builder().quantity(5).build())
                .build();

        when(productSearchService.findById(1)).thenReturn(product);
        when(productSearchService.generateImageUrl(any())).thenReturn("https://signed-url");

        mockMvc.perform(get("/products/detail/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/detail"))
                .andExpect(model().attributeExists("product", "stock", "quantityOptions"));
    }

    @Test
    void detail_不正IDはメッセージを表示する() throws Exception {
        when(productSearchService.findById(999)).thenThrow(new DomainException("なし"));

        mockMvc.perform(get("/products/detail/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/detail"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}