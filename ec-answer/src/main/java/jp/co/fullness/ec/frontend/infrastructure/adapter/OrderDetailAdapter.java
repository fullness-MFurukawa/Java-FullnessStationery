package jp.co.fullness.ec.frontend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.frontend.domain.adapter.Adapter;
import jp.co.fullness.ec.frontend.domain.model.OrderDetail;
import jp.co.fullness.ec.frontend.infrastructure.entity.OrderDetailEntity;

@Component
public class OrderDetailAdapter implements Adapter<OrderDetail, OrderDetailEntity> {

    @Override
    public OrderDetail toDomain(OrderDetailEntity entity) {
        OrderDetail detail = new OrderDetail();
        detail.setCount(entity.getCount());
        // product は RepositoryImpl で補完
        return detail;
    }

    @Override
    public OrderDetailEntity fromDomain(OrderDetail domain) {
        OrderDetailEntity entity = new OrderDetailEntity();
        entity.setProductId(domain.getProduct() != null ? domain.getProduct().getId() : null);
        entity.setCount(domain.getCount());
        return entity;
    }
}
