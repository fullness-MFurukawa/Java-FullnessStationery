package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.backend.infrastructure.entity.OrderDetailEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderDetailMapperTest {

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private DataSource dataSource;

    private Integer testOrderId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        testOrderId = jdbc.queryForObject(
                "INSERT INTO orders(order_date, amount_total, customer_id, order_status_id, payment_method_id) " +
                "VALUES (?, 250, 1, 1, 1) RETURNING id",
                Integer.class, Timestamp.valueOf(LocalDateTime.of(2099, 1, 1, 10, 0)));
        jdbc.update("INSERT INTO orders_detail(order_id, product_id, count) VALUES (?, 1, 2)", testOrderId);
        jdbc.update("INSERT INTO orders_detail(order_id, product_id, count) VALUES (?, 2, 1)", testOrderId);
    }

    @SuppressWarnings("null")
    @Test
    void selectByOrderId_注文の明細を取得できる() {
        List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(testOrderId);

        assertThat(details).hasSize(2);
        assertThat(details).extracting(OrderDetailEntity::getProductId).containsExactly(1, 2);
        assertThat(details).extracting(OrderDetailEntity::getCount).containsExactly(2, 1);
    }
}
