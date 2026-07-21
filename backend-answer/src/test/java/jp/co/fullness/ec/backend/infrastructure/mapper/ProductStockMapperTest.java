package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import jp.co.fullness.ec.backend.infrastructure.entity.ProductStockEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductStockMapperTest {

    @Autowired
    private ProductStockMapper productStockMapper;

    @Test
    void selectByProductId_在庫を取得できる() {
        ProductStockEntity stock = productStockMapper.selectByProductId(1);

        assertThat(stock).isNotNull();
        assertThat(stock.getQuantity()).isEqualTo(10);
    }
}