package jp.co.fullness.ec.frontend.application.purchase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Cart;
import jp.co.fullness.ec.frontend.domain.model.CartItem;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;
import jp.co.fullness.ec.frontend.domain.model.ProductStock;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;
import jp.co.fullness.ec.frontend.domain.repository.PaymentMethodRepository;
import jp.co.fullness.ec.frontend.domain.repository.ProductStockRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private ProductStockRepository productStockRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    private Cart cart;
    private PurchaseServiceImpl purchaseService;

    @BeforeEach
    void setUp() {
        cart = new Cart();   // 実体を使用(単純なPOJO)
        purchaseService = new PurchaseServiceImpl(
                cart, productStockRepository, orderRepository, paymentMethodRepository);
    }

    @Test
    void placeOrder_在庫十分なら在庫を減らし注文登録して採番IDを返す() {
        cart.add(new CartItem(1, "ボールペン", 100, null, 2));
        when(paymentMethodRepository.findById(1))
                .thenReturn(Optional.of(PaymentMethod.builder().id(1).name("現金").build()));
        when(productStockRepository.lockByProductId(1))
                .thenReturn(Optional.of(ProductStock.builder().productId(1).quantity(10).build()));
        doAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(555);   // 採番IDを反映
            return null;
        }).when(orderRepository).register(any(Order.class));

        Integer orderId = purchaseService.placeOrder(100, 1);

        assertThat(orderId).isEqualTo(555);
        verify(productStockRepository).decreaseQuantity(1, 2);
        verify(orderRepository).register(any(Order.class));
    }

    @Test
    void placeOrder_在庫不足なら例外を投げ減算も登録もしない() {
        cart.add(new CartItem(1, "ボールペン", 100, null, 5));
        when(paymentMethodRepository.findById(1))
                .thenReturn(Optional.of(PaymentMethod.builder().id(1).name("現金").build()));
        when(productStockRepository.lockByProductId(1))
                .thenReturn(Optional.of(ProductStock.builder().productId(1).quantity(3).build()));

        assertThatThrownBy(() -> purchaseService.placeOrder(100, 1))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("在庫が不足しています");

        verify(productStockRepository, never()).decreaseQuantity(anyInt(), anyInt());
        verify(orderRepository, never()).register(any());
    }

    @Test
    void placeOrder_カートが空なら例外() {
        assertThatThrownBy(() -> purchaseService.placeOrder(100, 1))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("カートに商品がありません");
    }
}