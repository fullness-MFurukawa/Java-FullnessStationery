package jp.co.fullness.ec.frontend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.frontend.domain.adapter.Adapter;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.infrastructure.entity.OrderEntity;

@Component
public class OrderAdapter implements Adapter<Order, OrderEntity> {

    @Override
    public Order toDomain(OrderEntity entity) {
        return Order.builder()
                .id(entity.getId())
                .orderDate(entity.getOrderDate())
                .amountTotal(entity.getAmountTotal())
                // customer / orderStatus / paymentMethod / details は RepositoryImpl で補完
                .build();
    }

    @Override
    public OrderEntity fromDomain(Order domain) {
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.getId());
        entity.setOrderDate(domain.getOrderDate());
        entity.setAmountTotal(domain.getAmountTotal());
        entity.setCustomerId(domain.getCustomer() != null ? domain.getCustomer().getId() : null);
        entity.setOrderStatusId(domain.getOrderStatus() != null ? domain.getOrderStatus().getId() : null);
        entity.setPaymentMethodId(domain.getPaymentMethod() != null ? domain.getPaymentMethod().getId() : null);
        return entity;
    }
}
