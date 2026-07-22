package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.frontend.domain.model.OrderStatus;
import jp.co.fullness.ec.frontend.domain.repository.OrderStatusRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.OrderStatusAdapter;
import jp.co.fullness.ec.frontend.infrastructure.mapper.OrderStatusMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderStatusRepositoryImpl implements OrderStatusRepository {

    private final OrderStatusMapper orderStatusMapper;
    private final OrderStatusAdapter orderStatusAdapter;

    @Override
    public List<OrderStatus> findAll() {
        return orderStatusMapper.selectAll().stream()
                .map(orderStatusAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<OrderStatus> findById(Integer id) {
        return Optional.ofNullable(orderStatusMapper.selectById(id))
                .map(orderStatusAdapter::toDomain);
    }
}
