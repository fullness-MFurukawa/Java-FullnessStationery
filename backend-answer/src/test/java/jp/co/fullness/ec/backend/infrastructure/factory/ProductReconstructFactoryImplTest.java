package jp.co.fullness.ec.backend.infrastructure.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;
import jp.co.fullness.ec.backend.domain.model.ProductStock;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductCategoryAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductStockAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductCategoryEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductStockEntity;

@ExtendWith(MockitoExtension.class)
class ProductReconstructFactoryImplTest {

    @Mock
    private ProductAdapter productAdapter;

    @Mock
    private ProductStockAdapter productStockAdapter;

    @Mock
    private ProductCategoryAdapter productCategoryAdapter;

    @InjectMocks
    private ProductReconstructFactoryImpl factory;

    @Test
    void create_Adapterの結果からProduct集約を組み立てる() {
        ProductEntity productEntity = new ProductEntity();
        ProductCategoryEntity categoryEntity = new ProductCategoryEntity();
        ProductStockEntity stockEntity = new ProductStockEntity();

        // Adapter が返すドメイン(スタブ)
        Product stubProduct = Product.builder().id(1).name("テストペン").price(120).build();
        ProductCategory stubCategory = ProductCategory.builder().id(1).name("文房具").build();
        ProductStock stubStock = ProductStock.builder().id(1).quantity(10).build();

        when(productAdapter.toDomain(productEntity)).thenReturn(stubProduct);
        when(productCategoryAdapter.toDomain(categoryEntity)).thenReturn(stubCategory);
        when(productStockAdapter.toDomain(stockEntity)).thenReturn(stubStock);

        ProductReconstructSource source =
                new ProductReconstructSource(productEntity, stockEntity, categoryEntity);
        Product product = factory.create(source);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("テストペン");
        // Adapter の結果でカテゴリ・在庫が補完される
        assertThat(product.getCategory()).isSameAs(stubCategory);
        assertThat(product.getStock()).isSameAs(stubStock);
    }

    @Test
    void create_sourceがnullならnullを返す() {
        assertThat(factory.create(null)).isNull();
    }

    @Test
    void create_商品Entityがnullならnullを返す() {
        ProductReconstructSource source =
                new ProductReconstructSource(null, new ProductStockEntity(), new ProductCategoryEntity());

        assertThat(factory.create(source)).isNull();
    }
}
