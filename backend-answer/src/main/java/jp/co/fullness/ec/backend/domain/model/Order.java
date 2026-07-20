package jp.co.fullness.ec.backend.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 注文(orders)を表すドメインオブジェクト(Order 集約のルート)。
 * 顧客・注文ステータス・支払い方法を参照として保持し、
 * 注文明細({@link OrderDetail})のリストを内包する。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    /** 注文ID */
    private Integer id;

    /** 注文日時 */
    private LocalDateTime orderDate;

    /** 合計金額 */
    private Integer amountTotal;

    /** 注文した顧客 */
    private Customer customer;

    /** 注文ステータス */
    private OrderStatus orderStatus;

    /** 支払い方法 */
    private PaymentMethod paymentMethod;

    /** 注文明細のリスト */
    @Builder.Default
    private List<OrderDetail> details = new ArrayList<>();
}
