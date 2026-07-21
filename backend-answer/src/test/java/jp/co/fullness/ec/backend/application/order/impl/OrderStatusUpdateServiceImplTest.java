package jp.co.fullness.ec.backend.application.order.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.backend.domain.exception.DomainException;
import jp.co.fullness.ec.backend.domain.model.Order;
import jp.co.fullness.ec.backend.domain.model.OrderStatus;
import jp.co.fullness.ec.backend.domain.repository.OrderRepository;
import jp.co.fullness.ec.backend.domain.repository.OrderStatusRepository;

@ExtendWith(MockitoExtension.class)
class OrderStatusUpdateServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private OrderStatusUpdateServiceImpl service;

    @Test
    void findById_存在すれば注文を返す() {
        Order order = Order.builder().id(1).build();
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertThat(service.findById(1)).isSameAs(order);
    }

    @Test
    void findById_存在しなければDomainException() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void resolveStatus_IDからステータスを解決する() {
        when(orderStatusRepository.findAll()).thenReturn(List.of(
                OrderStatus.builder().id(1).name("注文済").build(),
                OrderStatus.builder().id(3).name("配送中").build()));

        assertThat(service.resolveStatus(3).getName()).isEqualTo("配送中");
    }

    @Test
    void resolveStatus_該当なしでDomainException() {
        when(orderStatusRepository.findAll()).thenReturn(List.of(
                OrderStatus.builder().id(1).name("注文済").build()));

        assertThatThrownBy(() -> service.resolveStatus(99))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void updateStatus_存在チェック後に更新を呼ぶ() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(Order.builder().id(1).build()));

        service.updateStatus(1, 3);

        verify(orderRepository).updateStatus(1, 3);
    }

    @Test
    void updateStatus_存在しなければ更新せずDomainException() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(1, 3))
                .isInstanceOf(DomainException.class);
        verify(orderRepository, never()).updateStatus(anyInt(), anyInt());
    }
}