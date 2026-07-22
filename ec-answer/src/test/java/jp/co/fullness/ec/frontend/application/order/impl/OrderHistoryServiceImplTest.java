package jp.co.fullness.ec.frontend.application.order.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderHistoryServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderHistoryServiceImpl orderHistoryService;

    @Test
    void findHistory_顧客IDでリポジトリに委譲する() {
        Order o = Order.builder().id(1).build();
        when(orderRepository.findByCustomerId(100)).thenReturn(List.of(o));
        assertThat(orderHistoryService.findHistory(100)).containsExactly(o);
    }

    @Test
    void findOwnedOrder_自分の注文なら返す() {
        Order o = Order.builder().id(1)
                .customer(Customer.builder().id(100).build()).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(o));

        assertThat(orderHistoryService.findOwnedOrder(1, 100)).contains(o);
    }

    @Test
    void findOwnedOrder_他人の注文なら空() {
        Order o = Order.builder().id(1)
                .customer(Customer.builder().id(999).build()).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(o));

        assertThat(orderHistoryService.findOwnedOrder(1, 100)).isEmpty();
    }

    @Test
    void findOwnedOrder_存在しないIDなら空() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());
        assertThat(orderHistoryService.findOwnedOrder(1, 100)).isEmpty();
    }
}
