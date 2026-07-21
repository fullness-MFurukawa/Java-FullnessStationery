package jp.co.fullness.ec.backend.application.order.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.backend.application.order.OrderStatusUpdateService;
import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.domain.repository.OrderRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderStatusUpdateServiceImpl implements OrderStatusUpdateService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Override
    public Order findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException("指定された注文は存在しません。"));
    }

    @Override
    public List<OrderStatus> findAllStatuses() {
        return orderStatusRepository.findAll();
    }

    @Override
    public OrderStatus resolveStatus(Integer statusId) {
        return orderStatusRepository.findAll().stream()
                .filter(status -> status.getId().equals(statusId))
                .findFirst()
                .orElseThrow(() -> new DomainException("指定されたステータスは存在しません。"));
    }

    @Override
    @Transactional
    public void updateStatus(Integer orderId, Integer newStatusId) {
        // 存在チェック(なければ例外→コントローラでBP015へリダイレクト)
        orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException("指定された注文は存在しません。"));
        orderRepository.updateStatus(orderId, newStatusId);
    }
}
