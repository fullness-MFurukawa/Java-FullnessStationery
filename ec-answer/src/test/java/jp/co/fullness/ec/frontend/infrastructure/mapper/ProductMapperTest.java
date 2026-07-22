package jp.co.fullness.ec.frontend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.frontend.infrastructure.entity.ProductEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private DataSource dataSource;

    private Integer activeId;
    private Integer deletedId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        // カテゴリ1(文房具)配下に有効/削除済みの商品を投入
        activeId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('テスト商品A', 120, 'products/a.png', 1, 0) RETURNING id", Integer.class);
        deletedId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('削除済み商品', 100, 'products/x.png', 1, 1) RETURNING id", Integer.class);
    }

    @SuppressWarnings("null")
    @Test
    void searchByCategory_カテゴリで有効商品のみ取得できる() {
        List<ProductEntity> products = productMapper.searchByCategory(1);

        assertThat(products).extracting(ProductEntity::getId).contains(activeId);
        assertThat(products).extracting(ProductEntity::getId).doesNotContain(deletedId); // 削除済みは除外
    }

    @SuppressWarnings("null")
    @Test
    void searchByCategory_nullは全カテゴリ対象() {
        List<ProductEntity> products = productMapper.searchByCategory(null);

        assertThat(products).extracting(ProductEntity::getId).contains(activeId);
    }

    @Test
    void selectById_商品を取得できる() {
        ProductEntity product = productMapper.selectById(activeId);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo("テスト商品A");
        assertThat(product.getPrice()).isEqualTo(120);
    }
}
