package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.model.OrderDetail;
import jp.co.fullness.ec.frontend.domain.model.OrderStatus;
import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.CustomerAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.OrderAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.OrderDetailAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.OrderStatusAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.PaymentMethodAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductCategoryAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.ProductStockAdapter;
import jp.co.fullness.ec.frontend.infrastructure.factory.ProductReconstructFactoryImpl;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        OrderRepositoryImpl.class, OrderAdapter.class, OrderDetailAdapter.class,
        CustomerRepositoryImpl.class, CustomerAdapter.class,
        OrderStatusRepositoryImpl.class, OrderStatusAdapter.class,
        PaymentMethodRepositoryImpl.class, PaymentMethodAdapter.class,
        ProductRepositoryImpl.class, ProductReconstructFactoryImpl.class,
        ProductAdapter.class, ProductStockAdapter.class, ProductCategoryAdapter.class
})
class OrderRepositoryImplTest {

    @Autowired
    private OrderRepository orderRepository;

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
                "VALUES ('注文花子','チュウモンハナコ','大阪府2-2','090-3333-4444'," +
                "'o2@example.com','o2user','$2a$10$dummy') RETURNING id",
                Integer.class);
        productId = jdbc.queryForObject(
                "INSERT INTO product(name, price, image_url, product_category_id, delete_flg) " +
                "VALUES ('集約テスト商品', 150, 'products/a.png', 1, 0) RETURNING id",
                Integer.class);
        jdbc.update("INSERT INTO product_stock(quantity, product_id) VALUES (30, ?)", productId);
    }

    @Test
    void register_注文と明細を登録し採番IDが反映される() {
        Order order = buildOrder();

        orderRepository.register(order);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getDetails().get(0).getId()).isNotNull();
        assertThat(order.getDetails().get(0).getOrderId()).isEqualTo(order.getId());
    }

    @Test
    void findById_顧客ステータス支払い方法明細商品まで組み立てる() {
        Order order = buildOrder();
        orderRepository.register(order);

        Order found = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(found.getCustomer().getId()).isEqualTo(customerId);
        assertThat(found.getOrderStatus().getName()).isEqualTo("注文済");
        assertThat(found.getPaymentMethod().getName()).isEqualTo("現金");
        assertThat(found.getDetails()).hasSize(1);
        assertThat(found.getDetails().get(0).getProduct().getId()).isEqualTo(productId);
        assertThat(found.getDetails().get(0).getCount()).isEqualTo(2);
    }

    @SuppressWarnings("null")
    @Test
    void findByCustomerId_顧客の注文一覧を取得できる() {
        Order order = buildOrder();
        orderRepository.register(order);

        assertThat(orderRepository.findByCustomerId(customerId))
                .extracting(Order::getId).contains(order.getId());
    }

    private Order buildOrder() {
        Product product = Product.builder()
                .id(productId).name("集約テスト商品").price(150).build();
        OrderDetail detail = OrderDetail.builder().product(product).count(2).build();
        return Order.builder()
                .orderDate(LocalDateTime.now())
                .amountTotal(300)
                .customer(Customer.builder().id(customerId).build())
                .orderStatus(OrderStatus.builder().id(1).build())
                .paymentMethod(PaymentMethod.builder().id(1).build())
                .details(new ArrayList<>(List.of(detail)))
                .build();
    }
}
