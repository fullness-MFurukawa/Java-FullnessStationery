package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.repository.OrderRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderSearchCondition;
import jp.co.fullness.ec.backend.infrastructure.adapter.CustomerAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.OrderAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.OrderDetailAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.OrderStatusAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductAdapter;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        OrderRepositoryImpl.class,
        OrderAdapter.class,
        OrderDetailAdapter.class,
        CustomerAdapter.class,
        OrderStatusAdapter.class,
        ProductAdapter.class
})
class OrderRepositoryImplTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DataSource dataSource;

    private Integer testProductId;
    private Integer testOrderId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        // 明細用のテスト商品(シード商品の編集・削除に依存しない)
        testProductId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('テスト商品A', 100, 'products/testA.png', 1, 0) RETURNING id",
                Integer.class);

        // テスト注文(顧客1・ステータス1=注文済・支払1 はシードのマスタを利用)
        testOrderId = jdbc.queryForObject(
                "INSERT INTO orders(order_date, amount_total, customer_id, order_status_id, payment_method_id) " +
                "VALUES (?, 200, 1, 1, 1) RETURNING id",
                Integer.class, Timestamp.valueOf(LocalDateTime.of(2099, 1, 1, 10, 0)));

        jdbc.update("INSERT INTO orders_detail(order_id, product_id, count) VALUES (?, ?, 2)",
                testOrderId, testProductId);
    }

    @Test
    void findById_顧客ステータス明細商品を補完した集約を返す() {
        Optional<Order> found = orderRepository.findById(testOrderId);

        assertThat(found).isPresent();
        Order order = found.get();

        assertThat(order.getId()).isEqualTo(testOrderId);
        assertThat(order.getAmountTotal()).isEqualTo(200);

        // 顧客(補完)
        assertThat(order.getCustomer()).isNotNull();
        assertThat(order.getCustomer().getUsername()).isEqualTo("testuser");

        // ステータス(補完)
        assertThat(order.getOrderStatus()).isNotNull();
        assertThat(order.getOrderStatus().getName()).isEqualTo("注文済");

        // 明細 + 商品(補完)
        assertThat(order.getDetails()).hasSize(1);
        assertThat(order.getDetails().get(0).getCount()).isEqualTo(2);
        assertThat(order.getDetails().get(0).getProduct()).isNotNull();
        assertThat(order.getDetails().get(0).getProduct().getName()).isEqualTo("テスト商品A");
    }

    @Test
    void findById_存在しなければempty() {
        assertThat(orderRepository.findById(999_999)).isEmpty();
    }

    @SuppressWarnings("null")
    @Test
    void search_条件で検索し集約が組み立つ() {
        OrderSearchCondition condition = new OrderSearchCondition(LocalDate.of(2099, 1, 1), null);

        List<Order> orders = orderRepository.search(condition, 10, 0);

        assertThat(orders).extracting(Order::getId).contains(testOrderId);
        assertThat(orders.get(0).getCustomer()).isNotNull();
        assertThat(orders.get(0).getOrderStatus()).isNotNull();
    }

    @Test
    void count_条件件数を返す() {
        OrderSearchCondition condition = new OrderSearchCondition(LocalDate.of(2099, 1, 1), null);

        assertThat(orderRepository.count(condition)).isEqualTo(1);
    }

    @Test
    void updateStatus_ステータスを更新する() {
        orderRepository.updateStatus(testOrderId, 3);

        Order updated = orderRepository.findById(testOrderId).orElseThrow();
        assertThat(updated.getOrderStatus().getName()).isEqualTo("配送中");
    }

    @Test
    void findDistinctOrderDates_投入日を含む() {
        assertThat(orderRepository.findDistinctOrderDates()).contains(LocalDate.of(2099, 1, 1));
    }
}