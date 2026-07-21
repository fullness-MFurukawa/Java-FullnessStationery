package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.domain.model.Product;
import jp.co.fullness.ec.backend.domain.repository.ProductRepository;

@SpringBootTest
@Transactional
class ProductRepositoryImplTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataSource dataSource;

    private Integer testProductId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        testProductId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('テスト商品', 999, 'products/test.png', 1, 0) RETURNING id",
                Integer.class);
        jdbc.update("INSERT INTO product_stock(quantity, product_id) VALUES (7, ?)", testProductId);
    }

    @Test
    void findById_カテゴリと在庫を組み立てて返す() {
        Optional<Product> found = productRepository.findById(testProductId);

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
    void search_有効商品に投入商品が含まれる() {
        // 引数: categoryId(null=全件), limit, offset
        List<Product> products = productRepository.search(null, 1000, 0);

        assertThat(products).extracting(Product::getId).contains(testProductId);
    }

    @SuppressWarnings("null")
    @Test
    void logicalDelete_削除後は一覧から除外される() {
        productRepository.logicalDelete(testProductId);

        List<Product> products = productRepository.search(null, 1000, 0);
        assertThat(products).extracting(Product::getId).doesNotContain(testProductId);
    }
}