package jp.co.fullness.ec.frontend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.frontend.infrastructure.entity.OrderDetailEntity;
import jp.co.fullness.ec.frontend.infrastructure.entity.OrderEntity;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DataSource dataSource;

    private Integer customerId;
    private Integer productId;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        customerId = jdbc.queryForObject(
                "INSERT INTO customer(name, name_kana, address1, phone_number, " +
                "mail_address, username, password) " +
                "VALUES ('注文太郎','チュウモンタロウ','東京都1-1','03-1111-2222'," +
                "'order@example.com','orderuser','$2a$10$dummy') RETURNING id",
                Integer.class);
        productId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('注文テスト商品', 200, 'products/o.png', 1, 0) RETURNING id",
                Integer.class);
        jdbc.update("INSERT INTO product_stock(quantity, product_id) VALUES (50, ?)", productId);
    }

    @Test
    void insertOrder_採番IDが反映され取得できる() {
        OrderEntity order = OrderEntity.builder()
                .orderDate(LocalDateTime.now()).amountTotal(400)
                .customerId(customerId).orderStatusId(1).paymentMethodId(1).build();

        orderMapper.insertOrder(order);

        assertThat(order.getId()).isNotNull();
        OrderEntity found = orderMapper.selectById(order.getId());
        assertThat(found.getAmountTotal()).isEqualTo(400);
        assertThat(found.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void insertOrderDetail_明細を登録し取得できる() {
        OrderEntity order = OrderEntity.builder()
                .orderDate(LocalDateTime.now()).amountTotal(400)
                .customerId(customerId).orderStatusId(1).paymentMethodId(1).build();
        orderMapper.insertOrder(order);

        OrderDetailEntity detail = OrderDetailEntity.builder()
                .orderId(order.getId()).productId(productId).count(2).build();
        orderMapper.insertOrderDetail(detail);

        assertThat(detail.getId()).isNotNull();
        List<OrderDetailEntity> details = orderMapper.selectDetailsByOrderId(order.getId());
        assertThat(details).hasSize(1);
        assertThat(details.get(0).getCount()).isEqualTo(2);
        assertThat(details.get(0).getProductId()).isEqualTo(productId);
    }

    @SuppressWarnings("null")
    @Test
    void selectByCustomerId_顧客の注文を取得できる() {
        OrderEntity order = OrderEntity.builder()
                .orderDate(LocalDateTime.now()).amountTotal(400)
                .customerId(customerId).orderStatusId(1).paymentMethodId(1).build();
        orderMapper.insertOrder(order);

        assertThat(orderMapper.selectByCustomerId(customerId))
                .extracting(OrderEntity::getId).contains(order.getId());
    }
}