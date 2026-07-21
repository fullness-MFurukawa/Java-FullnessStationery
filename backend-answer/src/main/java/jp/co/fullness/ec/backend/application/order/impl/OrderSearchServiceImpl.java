package jp.co.fullness.ec.backend.application.order.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.fullness.ec.backend.application.order.OrderSearchResult;
import jp.co.fullness.ec.backend.application.order.OrderSearchService;
import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderSearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSearchServiceImpl implements OrderSearchService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Override
    public OrderSearchResult search(LocalDate orderDate, Integer customerId, int page, int size) {
        OrderSearchCondition condition = new OrderSearchCondition(orderDate, customerId);

        long total = orderRepository.count(condition);
        int totalPages = (total == 0) ? 0 : (int) Math.ceil((double) total / size);

        // page は 1 始まり。範囲外は 1 に丸める。
        int currentPage = (page < 1) ? 1 : page;
        int offset = (currentPage - 1) * size;

        List<Order> orders = orderRepository.search(condition, size, offset);
        return new OrderSearchResult(orders, total, totalPages, currentPage);
    }

    @Override
    public List<LocalDate> findOrderDates() {
        return orderRepository.findDistinctOrderDates();
    }

    @Override
    public List<Customer> findCustomers() {
        return customerRepository.findAll();
    }
}
