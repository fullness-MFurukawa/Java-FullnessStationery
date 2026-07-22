package jp.co.fullness.ec.frontend.infrastructure.adapter;

import org.springframework.stereotype.Component;

import jp.co.fullness.ec.frontend.domain.adapter.Adapter;
import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;
import jp.co.fullness.ec.frontend.infrastructure.entity.PaymentMethodEntity;

@Component
public class PaymentMethodAdapter implements Adapter<PaymentMethod, PaymentMethodEntity> {

    @Override
    public PaymentMethod toDomain(PaymentMethodEntity entity) {
        return PaymentMethod.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    public PaymentMethodEntity fromDomain(PaymentMethod domain) {
        PaymentMethodEntity entity = new PaymentMethodEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        return entity;
    }
}
