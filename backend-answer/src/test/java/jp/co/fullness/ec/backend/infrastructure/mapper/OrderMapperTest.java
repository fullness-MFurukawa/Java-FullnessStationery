package jp.co.fullness.ec.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.backend.domain.repository.OrderSearchCondition;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbc;
    private Integer testOrderId;

    /** 既存マスタ(customer=1 / order_status / payment_method=1)を利用してテスト注文を投入。 */
    @BeforeEach
    void setUp() {
        jdbc = new JdbcTemplate(dataSource);
        testOrderId = jdbc.queryForObject(
                "INSERT INTO orders(order_date, amount_total, customer_id, order_status_id, payment_method_id) " +
                "VALUES (?, ?, 1, 1, 1) RETURNING id",
                Integer.class,
                Timestamp.valueOf(LocalDateTime.of(2099, 1, 1, 10, 0)), 250);
        jdbc.update("INSERT INTO orders_detail(order_id, product_id, count) VALUES (?, 1, 2)", testOrderId);
    }

    @Test
    void selectById_投入した注文を取得できる() {
        OrderEntity entity = orderMapper.selectById(testOrderId);

        assertThat(entity).isNotNull();
        assertThat(entity.getAmountTotal()).isEqualTo(250);
        assertThat(entity.getCustomerId()).isEqualTo(1);
        assertThat(entity.getOrderStatusId()).isEqualTo(1);
    }

    @SuppressWarnings("null")
    @Test
    void search_購入日で絞り込める() {
        OrderSearchCondition condition =
                new OrderSearchCondition(LocalDate.of(2099, 1, 1), null);

        List<OrderEntity> result = orderMapper.search(condition, 10, 0);

        assertThat(result).extracting(OrderEntity::getId).contains(testOrderId);
        assertThat(result).allSatisfy(o ->
                assertThat(o.getOrderDate().toLocalDate()).isEqualTo(LocalDate.of(2099, 1, 1)));
    }

    @Test
    void search_注文ID降順で返る() {
        OrderSearchCondition condition = new OrderSearchCondition(null, null);

        List<OrderEntity> result = orderMapper.search(condition, 100, 0);

        // 新しい(id大)順であること
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getId()).isGreaterThan(result.get(i + 1).getId());
        }
    }

    @Test
    void count_購入日条件の件数を返す() {
        OrderSearchCondition condition =
                new OrderSearchCondition(LocalDate.of(2099, 1, 1), null);

        long count = orderMapper.count(condition);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void updateStatus_ステータスを更新できる() {
        orderMapper.updateStatus(testOrderId, 3);

        OrderEntity updated = orderMapper.selectById(testOrderId);
        assertThat(updated.getOrderStatusId()).isEqualTo(3);
    }

    @Test
    void selectDistinctOrderDates_投入日を含む() {
        List<LocalDate> dates = orderMapper.selectDistinctOrderDates();

        assertThat(dates).contains(LocalDate.of(2099, 1, 1));
    }
}