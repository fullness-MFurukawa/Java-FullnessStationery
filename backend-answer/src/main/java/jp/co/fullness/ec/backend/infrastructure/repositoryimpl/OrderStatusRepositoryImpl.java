package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.domain.repository.OrderStatusRepository;
import jp.co.fullness.ec.backend.infrastructure.adapter.OrderStatusAdapter;
import jp.co.fullness.ec.backend.infrastructure.mapper.OrderStatusMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderStatusRepositoryImpl implements OrderStatusRepository {

    private final OrderStatusMapper orderStatusMapper;
    private final OrderStatusAdapter orderStatusAdapter;

    @Override
    public List<OrderStatus> findAll() {
        return orderStatusAdapter.toDomainList(orderStatusMapper.selectAll());
    }

    @Override
    public Optional<OrderStatus> findById(Integer id) {
        return Optional.of(orderStatusAdapter.toDomain(orderStatusMapper.selectById(id)));
    }
}
