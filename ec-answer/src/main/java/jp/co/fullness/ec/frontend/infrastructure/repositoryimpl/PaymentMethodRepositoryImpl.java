package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;
import jp.co.fullness.ec.frontend.domain.repository.PaymentMethodRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.PaymentMethodAdapter;
import jp.co.fullness.ec.frontend.infrastructure.mapper.PaymentMethodMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentMethodRepositoryImpl implements PaymentMethodRepository {

    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentMethodAdapter paymentMethodAdapter;

    @Override
    public List<PaymentMethod> findAll() {
        return paymentMethodMapper.selectAll().stream()
                .map(paymentMethodAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<PaymentMethod> findById(Integer id) {
        return Optional.ofNullable(paymentMethodMapper.selectById(id))
                .map(paymentMethodAdapter::toDomain);
    }
}
