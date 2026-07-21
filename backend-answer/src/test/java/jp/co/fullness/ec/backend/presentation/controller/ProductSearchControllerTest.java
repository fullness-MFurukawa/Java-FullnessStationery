package jp.co.fullness.ec.backend.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import jp.co.fullness.ec.backend.application.product.ProductSearchService;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;

@WebMvcTest(ProductSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductSearchService productSearchService;

    @Test
    void search_商品一覧を表示する() throws Exception {
        // search.html が単価を整形するので price は必須
        Product product = Product.builder()
                .id(1).name("水性ボールペン(黒)").price(120).imageUrl("products/1.png").build();

        when(productSearchService.count(any())).thenReturn(1L);
        when(productSearchService.search(any(), anyInt(), anyInt())).thenReturn(List.of(product));
        when(productSearchService.findAllCategories())
                .thenReturn(List.of(ProductCategory.builder().id(1).name("文房具").build()));
        when(productSearchService.generateImageUrl(any())).thenReturn("https://signed-url");

        mockMvc.perform(get("/admin/product"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/search"))
                .andExpect(model().attributeExists(
                        "products", "imageUrls", "categories", "currentPage", "totalPages", "totalCount"));
    }

    @Test
    void search_該当0件でも画面を表示する() throws Exception {
        when(productSearchService.count(any())).thenReturn(0L);
        when(productSearchService.search(any(), anyInt(), anyInt())).thenReturn(List.of());
        when(productSearchService.findAllCategories()).thenReturn(List.of());

        mockMvc.perform(get("/admin/product"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/search"));
    }
}
