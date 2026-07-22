package jp.co.fullness.ec.frontend.application.purchase.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.fullness.ec.frontend.application.purchase.PurchaseService;
import jp.co.fullness.ec.frontend.domain.exception.DomainException;
import jp.co.fullness.ec.frontend.domain.model.Cart;
import jp.co.fullness.ec.frontend.domain.model.CartItem;
import jp.co.fullness.ec.frontend.domain.model.Customer;
import jp.co.fullness.ec.frontend.domain.model.Order;
import jp.co.fullness.ec.frontend.domain.model.OrderDetail;
import jp.co.fullness.ec.frontend.domain.model.OrderStatus;
import jp.co.fullness.ec.frontend.domain.model.PaymentMethod;
import jp.co.fullness.ec.frontend.domain.model.Product;
import jp.co.fullness.ec.frontend.domain.model.ProductStock;
import jp.co.fullness.ec.frontend.domain.repository.OrderRepository;
import jp.co.fullness.ec.frontend.domain.repository.PaymentMethodRepository;
import jp.co.fullness.ec.frontend.domain.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    /** 注文ステータス「注文済」。 */
    private static final Integer ORDER_STATUS_ORDERED = 1;

    private final Cart cart;   // セッションスコープ
    private final ProductStockRepository productStockRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public List<PaymentMethod> findPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    @Transactional
    public Integer placeOrder(Integer customerId, Integer paymentMethodId) {

        if (cart.isEmpty()) {
            throw new DomainException("カートに商品がありません");
        }

        // 支払い方法の妥当性チェック
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new DomainException("支払い方法を選択してください"));

        List<OrderDetail> details = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            // 在庫を悲観ロックで取得(SELECT ... FOR UPDATE)
            ProductStock stock = productStockRepository.lockByProductId(item.getProductId())
                    .orElseThrow(() -> new DomainException(
                            "申し訳ありませんが、商品「" + item.getProductName() + "」の在庫が不足しています"));

            // 在庫数チェック
            if (stock.getQuantity() < item.getQuantity()) {
                throw new DomainException(
                        "申し訳ありませんが、商品「" + item.getProductName() + "」の在庫が不足しています");
            }

            // 在庫を購入数分減算
            productStockRepository.decreaseQuantity(item.getProductId(), item.getQuantity());

            // 明細を構築(商品はIDのみ、数量はカート数量)
            details.add(OrderDetail.builder()
                    .product(Product.builder().id(item.getProductId()).build())
                    .count(item.getQuantity())
                    .build());
        }

        // 注文を構築して登録(注文済ステータス)
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .amountTotal(cart.getTotalAmount())
                .customer(Customer.builder().id(customerId).build())
                .orderStatus(OrderStatus.builder().id(ORDER_STATUS_ORDERED).build())
                .paymentMethod(paymentMethod)
                .details(details)
                .build();

        orderRepository.register(order);

        return order.getId();
    }
}
