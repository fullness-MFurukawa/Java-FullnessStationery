package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void selectById_商品を取得できる() {
        ProductEntity product = productMapper.selectById(1);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("水性ボールペン(黒)");
        assertThat(product.getPrice()).isEqualTo(120);
    }

    @SuppressWarnings("null")
    @Test
    void search_有効な商品を取得できる() {
        // 引数: categoryId(null=全件), limit, offset
        List<ProductEntity> products = productMapper.search(null, 100, 0);

        assertThat(products).isNotEmpty();
        assertThat(products).extracting(ProductEntity::getId).contains(1);
    }

    @Test
    void count_件数を返す() {
        long count = productMapper.count(null);

        assertThat(count).isGreaterThanOrEqualTo(5);
    }

    @SuppressWarnings("null")
    @Test
    void logicalDelete_削除後は一覧から除外される() {
        productMapper.logicalDelete(1);

        List<ProductEntity> products = productMapper.search(null, 100, 0);
        assertThat(products).extracting(ProductEntity::getId).doesNotContain(1);
    }
}