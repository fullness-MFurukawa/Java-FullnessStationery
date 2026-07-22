package jp.co.fullness.ec.frontend.infrastructure.repositoryimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.model.OrderDetail;
import jp.co.fullness.ec.frontend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;
import jp.co.fullness.ec.frontend.domain.repository.OrderStatusRepository;
import jp.co.fullness.ec.frontend.domain.repository.PaymentMethodRepository;
import jp.co.fullness.ec.frontend.domain.repository.ProductRepository;
import jp.co.fullness.ec.frontend.infrastructure.adapter.OrderAdapter;
import jp.co.fullness.ec.frontend.infrastructure.adapter.OrderDetailAdapter;
import jp.co.fullness.ec.frontend.infrastructure.entity.OrderDetailEntity;
import jp.co.fullness.ec.frontend.infrastructure.entity.OrderEntity;
import jp.co.fullness.ec.frontend.infrastructure.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;
    private final OrderAdapter orderAdapter;
    private final OrderDetailAdapter orderDetailAdapter;

    // 参照補完用
    private final CustomerRepository customerRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ProductRepository productRepository;

    @Override
    public void register(Order order) {
        // 1) 注文を登録し、採番IDを取得
        OrderEntity orderEntity = orderAdapter.fromDomain(order);
        orderMapper.insertOrder(orderEntity);
        order.setId(orderEntity.getId());

        // 2) 明細を採番IDに紐づけて登録
        for (OrderDetail detail : order.getDetails()) {
            OrderDetailEntity detailEntity = orderDetailAdapter.fromDomain(detail);
            detailEntity.setOrderId(orderEntity.getId());   
            orderMapper.insertOrderDetail(detailEntity);

            detail.setId(detailEntity.getId());
            detail.setOrderId(orderEntity.getId());          
        }
    }

    @Override
    public Optional<Order> findById(Integer id) {
        OrderEntity entity = orderMapper.selectById(id);
        return entity == null ? Optional.empty() : Optional.of(assemble(entity));
    }

    @Override
    public List<Order> findByCustomerId(Integer customerId) {
        return orderMapper.selectByCustomerId(customerId).stream()
                .map(this::assemble)
                .toList();
    }

    /** 注文エンティティに顧客・ステータス・支払い方法・明細(商品含む)を補完する。 */
    private Order assemble(OrderEntity entity) {
        Order order = orderAdapter.toDomain(entity);

        customerRepository.findById(entity.getCustomerId()).ifPresent(order::setCustomer);
        orderStatusRepository.findById(entity.getOrderStatusId()).ifPresent(order::setOrderStatus);
        paymentMethodRepository.findById(entity.getPaymentMethodId()).ifPresent(order::setPaymentMethod);

        List<OrderDetail> details = orderMapper.selectDetailsByOrderId(entity.getId()).stream()
                .map(de -> {
                    OrderDetail detail = orderDetailAdapter.toDomain(de);
                    productRepository.findById(de.getProductId()).ifPresent(detail::setProduct);
                    return detail;
                })
                .toList();
        order.setDetails(details);

        return order;
    }
}
