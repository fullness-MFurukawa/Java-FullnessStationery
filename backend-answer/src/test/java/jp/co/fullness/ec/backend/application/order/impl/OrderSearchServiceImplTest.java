package jp.co.fullness.ec.backend.application.order.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.application.order.OrderSearchResult;
import jp.co.fullness.ec.backend.domain.model.Customer;
import jp.co.fullness.ec.backend.domain.repository.CustomerRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderSearchCondition;

@ExtendWith(MockitoExtension.class)
class OrderSearchServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderSearchServiceImpl service;

    @Test
    void search_ページング計算と条件の受け渡し() {
        LocalDate date = LocalDate.of(2024, 5, 12);
        when(orderRepository.count(any())).thenReturn(25L);
        when(orderRepository.search(any(), anyInt(), anyInt())).thenReturn(List.of());

        OrderSearchResult result = service.search(date, 1, 2, 10);

        assertThat(result.getTotalCount()).isEqualTo(25);
        assertThat(result.getTotalPages()).isEqualTo(3);   // ceil(25 / 10)
        assertThat(result.getCurrentPage()).isEqualTo(2);

        ArgumentCaptor<OrderSearchCondition> captor = ArgumentCaptor.forClass(OrderSearchCondition.class);
        verify(orderRepository).search(captor.capture(), eq(10), eq(10));   // offset = (2-1)*10
        assertThat(captor.getValue().getOrderDate()).isEqualTo(date);
        assertThat(captor.getValue().getCustomerId()).isEqualTo(1);
    }

    @Test
    void search_ページ番号が1未満なら1に丸める() {
        when(orderRepository.count(any())).thenReturn(5L);
        when(orderRepository.search(any(), anyInt(), anyInt())).thenReturn(List.of());

        OrderSearchResult result = service.search(null, null, 0, 10);

        assertThat(result.getCurrentPage()).isEqualTo(1);
        verify(orderRepository).search(any(), eq(10), eq(0));
    }

    @Test
    void search_0件ならtotalPagesは0() {
        when(orderRepository.count(any())).thenReturn(0L);
        when(orderRepository.search(any(), anyInt(), anyInt())).thenReturn(List.of());

        OrderSearchResult result = service.search(null, null, 1, 10);

        assertThat(result.getTotalCount()).isZero();
        assertThat(result.getTotalPages()).isZero();
    }

    @Test
    void findOrderDates_リポジトリに委譲する() {
        when(orderRepository.findDistinctOrderDates())
                .thenReturn(List.of(LocalDate.of(2024, 5, 12)));

        assertThat(service.findOrderDates()).containsExactly(LocalDate.of(2024, 5, 12));
    }

    @Test
    void findCustomers_リポジトリに委譲する() {
        when(customerRepository.findAll())
                .thenReturn(List.of(Customer.builder().id(1).username("testuser").build()));

        assertThat(service.findCustomers()).hasSize(1);
    }
}