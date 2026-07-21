package jp.co.fullness.ec.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.backend.domain.adapter.Adapter;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderStatusEntity;

@Component
public class OrderStatusAdapter implements Adapter<OrderStatus, OrderStatusEntity> {

    @Override
    public OrderStatus toDomain(OrderStatusEntity entity) {
        return OrderStatus.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    public OrderStatusEntity fromDomain(OrderStatus domain) {
        OrderStatusEntity entity = new OrderStatusEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        return entity;
    }
}
