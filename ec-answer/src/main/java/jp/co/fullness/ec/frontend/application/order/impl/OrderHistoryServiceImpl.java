package jp.co.fullness.ec.frontend.application.order.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jp.co.fullness.ec.frontend.application.order.OrderHistoryService;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> findHistory(Integer customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<Order> findOwnedOrder(Integer orderId, Integer customerId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getCustomer() != null
                        && customerId.equals(order.getCustomer().getId()));
    }
}