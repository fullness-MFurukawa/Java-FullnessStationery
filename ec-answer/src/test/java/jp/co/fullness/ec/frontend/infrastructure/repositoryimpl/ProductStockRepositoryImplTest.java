package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.frontend.domain.repository.ProductStockRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductStockAdapter;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ ProductStockRepositoryImpl.class, ProductStockAdapter.class })
class ProductStockRepositoryImplTest {

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private DataSource dataSource;

    private Integer productId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        productId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('在庫テスト商品', 100, 'products/s.png', 1, 0) RETURNING id", Integer.class);
        jdbc.update("INSERT INTO product_stock(quantity, product_id) VALUES (10, ?)", productId);
    }

    @Test
    void findByProductId_在庫を取得できる() {
        assertThat(productStockRepository.findByProductId(productId).orElseThrow().getQuantity())
                .isEqualTo(10);
    }

    @Test
    void decreaseQuantity_在庫を減らせる() {
        productStockRepository.decreaseQuantity(productId, 4);

        assertThat(productStockRepository.findByProductId(productId).orElseThrow().getQuantity())
                .isEqualTo(6);
    }

    @Test
    void lockByProductId_ロックして取得できる() {
        assertThat(productStockRepository.lockByProductId(productId)).isPresent();
    }
}