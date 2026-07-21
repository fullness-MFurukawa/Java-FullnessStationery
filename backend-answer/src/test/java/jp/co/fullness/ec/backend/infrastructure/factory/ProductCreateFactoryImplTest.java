package jp.co.fullness.ec.backend.infrastructure.factory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.factory.ProductCreateSource;
import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.model.ProductCategory;

class ProductCreateFactoryImplTest {

    private final ProductCreateFactoryImpl factory = new ProductCreateFactoryImpl();

    @Test
    void create_Sourceから商品集約を組み立てる() {
        ProductCategory category = ProductCategory.builder().id(1).name("文房具").build();

        // ProductCreateSource(name, price, imageKey, category, stockQuantity)
        ProductCreateSource source =
                new ProductCreateSource("テストペン", 500, "products/test.png", category, 100);

        Product product = factory.create(source);

        assertThat(product.getName()).isEqualTo("テストペン");
        assertThat(product.getPrice()).isEqualTo(500);
        assertThat(product.getImageUrl()).isEqualTo("products/test.png");
        assertThat(product.getCategory()).isNotNull();
        assertThat(product.getCategory().getId()).isEqualTo(1);
        assertThat(product.getStock()).isNotNull();
        assertThat(product.getStock().getQuantity()).isEqualTo(100);
    }

    @Test
    void create_新規商品は未削除で組み立てられる() {
        ProductCategory category = ProductCategory.builder().id(1).name("文房具").build();
        ProductCreateSource source =
                new ProductCreateSource("テストペン", 500, "products/test.png", category, 100);

        Product product = factory.create(source);

        assertThat(product.isDeleted()).isFalse();
    }
}
