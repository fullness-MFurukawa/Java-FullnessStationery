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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.application.product.ProductRegisterCommand;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.factory.Factory;
import jp.co.fullness.ec.backend.domain.factory.ProductCreateSource;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.repository.ProductCategoryRepository;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;
import jp.co.fullness.ec.backend.domain.storage.ImageStorage;

@ExtendWith(MockitoExtension.class)
class ProductCreateServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private Factory<Product, ProductCreateSource> productCreateFactory;

    @Mock
    private ImageStorage imageStorage;

    @InjectMocks
    private ProductCreateServiceImpl service;

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

        // products/<uuid>.png 形式
        assertThat(key).startsWith("products/").endsWith(".png");
        // 生成したキーで upload される
        verify(imageStorage).upload(key, content, "image/png");
    }

    @Test
    void uploadImage_拡張子が無ければ拡張子なしのキー() {
        byte[] content = {1};

        String key = service.uploadImage(content, "image/png", "noext");

        assertThat(key).startsWith("products/");
        assertThat(key).doesNotContain(".");
    }

    @Test
    void generateImageUrl_Storageに委譲する() {
        when(imageStorage.generatePresignedUrl("products/x.png")).thenReturn("https://signed-url");

        assertThat(service.generateImageUrl("products/x.png")).isEqualTo("https://signed-url");
    }

    @Test
    void register_カテゴリ確認しFactoryで生成して登録する() {
        ProductRegisterCommand command = mock(ProductRegisterCommand.class);
        when(command.getCategoryId()).thenReturn(1);
        when(command.getName()).thenReturn("テストペン");
        when(command.getPrice()).thenReturn(500);
        when(command.getImageKey()).thenReturn("products/abc.png");
        when(command.getStockQuantity()).thenReturn(100);

        ProductCategory category = ProductCategory.builder().id(1).name("文房具").build();
        Product product = Product.builder().id(10).name("テストペン").build();

        when(productCategoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(productCreateFactory.create(any())).thenReturn(product);

        service.register(command);

        // Factory へ渡した Source の中身を検証
        ArgumentCaptor<ProductCreateSource> captor = ArgumentCaptor.forClass(ProductCreateSource.class);
        verify(productCreateFactory).create(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("テストペン");
        assertThat(captor.getValue().getPrice()).isEqualTo(500);
        assertThat(captor.getValue().getCategory()).isSameAs(category);
        assertThat(captor.getValue().getStockQuantity()).isEqualTo(100);

        // 生成された商品が登録される
        verify(productRepository).register(product);
    }

    @Test
    void register_カテゴリが存在しなければ例外で登録しない() {
        ProductRegisterCommand command = mock(ProductRegisterCommand.class);
        when(command.getCategoryId()).thenReturn(99);
        when(productCategoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(DomainException.class);

        verify(productRepository, never()).register(any());
    }
}