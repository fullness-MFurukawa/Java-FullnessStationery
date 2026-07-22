package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.repository.ProductRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductCategoryAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductStockAdapter;
import jp.co.fullness.ec.frontend.infrastructure.factory.ProductReconstructFactoryImpl;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        ProductRepositoryImpl.class,
        ProductReconstructFactoryImpl.class,
        ProductAdapter.class,
        ProductStockAdapter.class,
        ProductCategoryAdapter.class
})
class ProductRepositoryImplTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataSource dataSource;

    private Integer productId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        productId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('テスト商品', 250, 'products/t.png', 1, 0) RETURNING id", Integer.class);
        jdbc.update("INSERT INTO product_stock(quantity, product_id) VALUES (7, ?)", productId);
    }

    @Test
    void findById_カテゴリと在庫を組み立てて返す() {
        Optional<Product> found = productRepository.findById(productId);

        assertThat(found).isPresent();
        Product product = found.get();
        assertThat(product.getName()).isEqualTo("テスト商品");
        assertThat(product.getCategory()).isNotNull();
        assertThat(product.getCategory().getName()).isEqualTo("文房具");
        assertThat(product.getStock()).isNotNull();
        assertThat(product.getStock().getQuantity()).isEqualTo(7);
    }

    @SuppressWarnings("null")
    @Test
    void searchByCategory_集約を組み立てて返す() {
        List<Product> products = productRepository.searchByCategory(1);

        assertThat(products).extracting(Product::getId).contains(productId);
        assertThat(products.get(0).getCategory()).isNotNull();
        assertThat(products.get(0).getStock()).isNotNull();
    }
}
