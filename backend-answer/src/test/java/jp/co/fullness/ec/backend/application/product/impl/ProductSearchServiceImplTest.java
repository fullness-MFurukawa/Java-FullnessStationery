package jp.co.fullness.ec.backend.application.product.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.domain.storage.ImageStorage;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ImageStorage imageStorage;

    @InjectMocks
    private ProductSearchServiceImpl service;

    @Test
    void findAllCategories_カテゴリを委譲して返す() {
        when(productCategoryRepository.findAll())
                .thenReturn(List.of(ProductCategory.builder().id(1).name("文房具").build()));

        assertThat(service.findAllCategories()).hasSize(1);
    }

    @Test
    void search_オフセットを計算してリポジトリに渡す() {
        // page=3, size=5 → offset = (3-1)*5 = 10
        when(productRepository.search(2, 5, 10))
                .thenReturn(List.of(Product.builder().id(1).build()));

        List<Product> result = service.search(2, 3, 5);

        assertThat(result).hasSize(1);
        verify(productRepository).search(2, 5, 10);
    }

    @Test
    void search_1ページ目はオフセット0() {
        when(productRepository.search(null, 10, 0)).thenReturn(List.of());

        service.search(null, 1, 10);

        verify(productRepository).search(null, 10, 0);
    }

    @Test
    void count_リポジトリに委譲する() {
        when(productRepository.count(2)).thenReturn(7L);

        assertThat(service.count(2)).isEqualTo(7L);
    }

    @Test
    void generateImageUrl_Storageに委譲する() {
        when(imageStorage.generatePresignedUrl("products/x.png")).thenReturn("https://signed-url");

        assertThat(service.generateImageUrl("products/x.png")).isEqualTo("https://signed-url");
    }
}
