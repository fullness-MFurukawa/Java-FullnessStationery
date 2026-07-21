package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.domain.model.PaymentMethod;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderEntity;

class OrderAdapterTest {

    private final OrderAdapter adapter = new OrderAdapter();

    @Test
    void toDomain_基本項目を変換し参照先はnull() {
        OrderEntity entity = new OrderEntity();
        entity.setId(1);
        entity.setOrderDate(LocalDateTime.of(2024, 5, 12, 15, 30));
        entity.setAmountTotal(100);
        entity.setCustomerId(2);
        entity.setOrderStatusId(3);
        entity.setPaymentMethodId(1);

        Order order = adapter.toDomain(entity);

        assertThat(order.getId()).isEqualTo(1);
        assertThat(order.getOrderDate()).isEqualTo(LocalDateTime.of(2024, 5, 12, 15, 30));
        assertThat(order.getAmountTotal()).isEqualTo(100);
        // 参照先は RepositoryImpl で補完するため、この時点では null
        assertThat(order.getCustomer()).isNull();
        assertThat(order.getOrderStatus()).isNull();
        assertThat(order.getPaymentMethod()).isNull();
    }

    @Test
    void fromDomain_参照先のIDを抽出する() {
        Order order = Order.builder()
                .id(1)
                .orderDate(LocalDateTime.of(2024, 5, 12, 15, 30))
                .amountTotal(100)
                .customer(Customer.builder().id(2).build())
                .orderStatus(OrderStatus.builder().id(3).build())
                .paymentMethod(PaymentMethod.builder().id(1).build())
                .build();

        OrderEntity entity = adapter.fromDomain(order);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getAmountTotal()).isEqualTo(100);
        assertThat(entity.getCustomerId()).isEqualTo(2);
        assertThat(entity.getOrderStatusId()).isEqualTo(3);
        assertThat(entity.getPaymentMethodId()).isEqualTo(1);
    }

    @Test
    void fromDomain_参照先がnullならID項目もnull() {
        Order order = Order.builder().id(1).amountTotal(100).build();

        OrderEntity entity = adapter.fromDomain(order);

        assertThat(entity.getCustomerId()).isNull();
        assertThat(entity.getOrderStatusId()).isNull();
        assertThat(entity.getPaymentMethodId()).isNull();
    }
}
