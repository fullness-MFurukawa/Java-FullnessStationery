package jp.co.fullness.ec.backend.application.product.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.application.product.ProductUpdateCommand;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.model.ProductStock;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.domain.storage.ImageStorage;

@ExtendWith(MockitoExtension.class)
class ProductUpdateServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ImageStorage imageStorage;

    @InjectMocks
    private ProductUpdateServiceImpl service;

    @Test
    void findById_リポジトリに委譲する() {
        Product product = Product.builder().id(1).build();
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThat(service.findById(1)).containsSame(product);
    }

    @Test
    void findAllCategories_カテゴリを委譲して返す() {
        when(productCategoryRepository.findAll())
                .thenReturn(List.of(ProductCategory.builder().id(1).name("文房具").build()));

        assertThat(service.findAllCategories()).hasSize(1);
    }

    @Test
    void uploadImage_キーを生成しStorageへアップロードする() {
        byte[] content = {1, 2, 3};

        String key = service.uploadImage(content, "image/png", "photo.png");

        assertThat(key).startsWith("products/").endsWith(".png");
        verify(imageStorage).upload(key, content, "image/png");
    }

    @Test
    void generateImageUrl_Storageに委譲する() {
        when(imageStorage.generatePresignedUrl("products/x.png")).thenReturn("https://signed-url");

        assertThat(service.generateImageUrl("products/x.png")).isEqualTo("https://signed-url");
    }

    @Test
    void update_既存在庫がある場合は数量を更新して保存する() {
        ProductUpdateCommand command = mock(ProductUpdateCommand.class);
        when(command.getProductId()).thenReturn(1);
        when(command.getCategoryId()).thenReturn(2);
        when(command.getName()).thenReturn("更新名");
        when(command.getPrice()).thenReturn(300);
        when(command.getImageKey()).thenReturn("products/new.png");
        when(command.getStockQuantity()).thenReturn(20);

        Product product = Product.builder()
                .id(1)
                .stock(ProductStock.builder().quantity(5).build())
                .build();
        ProductCategory category = ProductCategory.builder().id(2).name("ノート").build();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productCategoryRepository.findById(2)).thenReturn(Optional.of(category));

        service.update(command);

        assertThat(product.getName()).isEqualTo("更新名");
        assertThat(product.getPrice()).isEqualTo(300);
        assertThat(product.getCategory()).isSameAs(category);
        assertThat(product.getImageUrl()).isEqualTo("products/new.png");
        assertThat(product.getStock().getQuantity()).isEqualTo(20);   // 既存在庫の数量更新
        verify(productRepository).update(product);
    }

    @Test
    void update_在庫が無い場合は新規在庫を作成する() {
        ProductUpdateCommand command = mock(ProductUpdateCommand.class);
        when(command.getProductId()).thenReturn(1);
        when(command.getCategoryId()).thenReturn(2);
        when(command.getName()).thenReturn("更新名");
        when(command.getPrice()).thenReturn(300);
        when(command.getImageKey()).thenReturn("products/new.png");
        when(command.getStockQuantity()).thenReturn(20);

        Product product = Product.builder().id(1).build();   // stock は null
        ProductCategory category = ProductCategory.builder().id(2).name("ノート").build();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productCategoryRepository.findById(2)).thenReturn(Optional.of(category));

        service.update(command);

        assertThat(product.getStock()).isNotNull();
        assertThat(product.getStock().getQuantity()).isEqualTo(20);
        verify(productRepository).update(product);
    }

    @Test
    void update_商品が存在しなければ例外で保存しない() {
        ProductUpdateCommand command = mock(ProductUpdateCommand.class);
        when(command.getProductId()).thenReturn(99);
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(DomainException.class);
        verify(productRepository, never()).update(any());
    }

    @Test
    void update_カテゴリが存在しなければ例外で保存しない() {
        ProductUpdateCommand command = mock(ProductUpdateCommand.class);
        when(command.getProductId()).thenReturn(1);
        when(command.getCategoryId()).thenReturn(99);
        when(productRepository.findById(1)).thenReturn(Optional.of(Product.builder().id(1).build()));
        when(productCategoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(DomainException.class);
        verify(productRepository, never()).update(any());
    }
}