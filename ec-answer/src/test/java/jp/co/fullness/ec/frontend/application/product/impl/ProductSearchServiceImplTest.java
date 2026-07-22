package jp.co.fullness.ec.frontend.application.product.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.model.ProductCategory;
import jp.co.fullness.ec.frontend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.frontend.domain.repository.ProductRepository;
import jp.co.fullness.ec.frontend.domain.storage.ImageStorage;

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
    void searchByCategory_リポジトリに委譲する() {
        when(productRepository.searchByCategory(1))
                .thenReturn(List.of(Product.builder().id(1).build()));

        assertThat(service.searchByCategory(1)).hasSize(1);
    }

    @Test
    void findById_存在すれば商品を返す() {
        Product product = Product.builder().id(1).name("テスト商品").build();
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThat(service.findById(1)).isSameAs(product);
    }

    @Test
    void findById_存在しなければDomainException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void generateImageUrl_キーが空ならnull() {
        assertThat(service.generateImageUrl(null)).isNull();
        assertThat(service.generateImageUrl("  ")).isNull();
    }

    @Test
    void generateImageUrl_Storageに委譲する() {
        when(imageStorage.generatePresignedUrl("products/1.png")).thenReturn("https://signed-url");

        assertThat(service.generateImageUrl("products/1.png")).isEqualTo("https://signed-url");
    }
}