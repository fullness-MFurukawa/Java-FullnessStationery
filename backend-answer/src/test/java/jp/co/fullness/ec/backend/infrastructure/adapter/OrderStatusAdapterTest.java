package jp.co.fullness.ec.backend.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderStatusEntity;

class OrderStatusAdapterTest {

    private final OrderStatusAdapter adapter = new OrderStatusAdapter();

    @Test
    void toDomain_idとnameを変換する() {
        OrderStatusEntity entity = new OrderStatusEntity();
        entity.setId(3);
        entity.setName("配送中");

        OrderStatus domain = adapter.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(3);
        assertThat(domain.getName()).isEqualTo("配送中");
    }

    @Test
    void fromDomain_idとnameを変換する() {
        OrderStatus domain = OrderStatus.builder().id(3).name("配送中").build();

        OrderStatusEntity entity = adapter.fromDomain(domain);

        assertThat(entity.getId()).isEqualTo(3);
        assertThat(entity.getName()).isEqualTo("配送中");
    }
}