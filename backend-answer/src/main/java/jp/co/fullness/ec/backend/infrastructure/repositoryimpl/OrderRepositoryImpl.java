package jp.co.fullness.ec.backend.infrastructure.repositoryimpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderDetail;
import jp.co.fullness.ec.backend.domain.repository.OrderRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderSearchCondition;
import jp.co.fullness.ec.backend.infrastructure.adapter.CustomerAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.OrderAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.OrderDetailAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.OrderStatusAdapter;
import jp.co.fullness.ec.backend.infrastructure.adapter.ProductAdapter;
import jp.co.fullness.ec.backend.infrastructure.entity.CustomerEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderDetailEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.OrderStatusEntity;
import jp.co.fullness.ec.backend.infrastructure.entity.ProductEntity;
import jp.co.fullness.ec.backend.infrastructure.mapper.CustomerMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.OrderDetailMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.OrderMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.OrderStatusMapper;
import jp.co.fullness.ec.backend.infrastructure.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final CustomerMapper customerMapper;
    private final OrderStatusMapper orderStatusMapper;
    private final ProductMapper productMapper;

    private final OrderAdapter orderAdapter;
    private final OrderDetailAdapter orderDetailAdapter;
    private final CustomerAdapter customerAdapter;
    private final OrderStatusAdapter orderStatusAdapter;
    private final ProductAdapter productAdapter;

    @Override
    public List<Order> search(OrderSearchCondition condition, int limit, int offset) {
        List<Order> orders = new ArrayList<>();
        for (OrderEntity entity : orderMapper.search(condition, limit, offset)) {
            orders.add(assemble(entity));
        }
        return orders;
    }

    @Override   
    public long count(OrderSearchCondition condition) {
        return orderMapper.count(condition);
    }

    @Override
    public Optional<Order> findById(Integer id) {
        OrderEntity entity = orderMapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(assemble(entity));
    }

    @Override
    public void updateStatus(Integer orderId, Integer newStatusId) {
        orderMapper.updateStatus(orderId, newStatusId);
    }

    @Override
    public List<LocalDate> findDistinctOrderDates() {
        return orderMapper.selectDistinctOrderDates();
    }

    /** OrderEntity から Order 集約を組み立てる(顧客・ステータス・明細+商品を補完)。 */
    private Order assemble(OrderEntity entity) {
        Order order = orderAdapter.toDomain(entity);

        // 顧客
        CustomerEntity customerEntity = customerMapper.selectById(entity.getCustomerId());
        if (customerEntity != null) {
            order.setCustomer(customerAdapter.toDomain(customerEntity));
        }

        // ステータス
        OrderStatusEntity statusEntity = orderStatusMapper.selectById(entity.getOrderStatusId());
        if (statusEntity != null) {
            order.setOrderStatus(orderStatusAdapter.toDomain(statusEntity));
        }

        // 明細 + 各明細の商品
        List<OrderDetail> details = new ArrayList<>();
        for (OrderDetailEntity detailEntity : orderDetailMapper.selectByOrderId(entity.getId())) {
            OrderDetail detail = orderDetailAdapter.toDomain(detailEntity);
            ProductEntity productEntity = productMapper.selectById(detailEntity.getProductId());
            if (productEntity != null) {
                detail.setProduct(productAdapter.toDomain(productEntity));
            }
            details.add(detail);
        }
        order.setDetails(details);

        return order;
    }

}